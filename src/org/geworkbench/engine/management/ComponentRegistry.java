package org.geworkbench.engine.management;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.engine.config.PluginDescriptor;
import org.geworkbench.engine.config.VisualPlugin;

/**
 * Component registry implementation.
 * @version $Id$
 */
public class ComponentRegistry {

    static Log log = LogFactory.getLog(ComponentRegistry.class);

    private static ComponentRegistry componentRegistry;

    /**
     * Gets an instance of the ComponentRegistry.
     *
     * @return the componentRegistry, creating one if it does not yet exist.
     */
    public static ComponentRegistry getRegistry() {
        if (componentRegistry == null) {
            componentRegistry = new ComponentRegistry();
        }
        return componentRegistry;
    }

    private static class MethodProfile {

        private Method method;
        private Class<?> type;
        private Class<? extends SynchModel> synchModelType;

        public MethodProfile(Method method, Class<?> type, Class<? extends SynchModel> synchModelType) {
            this.method = method;
            this.type = type;
            this.synchModelType = synchModelType;
        }

        public Method getMethod() {
            return method;
        }

        public Class<? extends SynchModel> getSynchModelType() {
            return synchModelType;
        }

        @Override public String toString() {
            return "Method: " + method + " on Model: " + synchModelType;    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

    /**
     * Inner class that manages the selection of @Subscribe methods for a subscriber and a published object.
     */
    private static class Subscriptions {


        private Class<?> publishedType;

        private List<MethodProfile> methods;

        public Subscriptions(Class<?> publishedType) {
            this.publishedType = publishedType;
            methods = new ArrayList<MethodProfile>();
        }

        public void addMethod(Method method, Class<?> type, Class<? extends SynchModel> synchModelType) {
            if (type.isAssignableFrom(publishedType)) {
                Iterator<MethodProfile> existingIterator = methods.iterator();
                while (existingIterator.hasNext()) {
                    MethodProfile existing = existingIterator.next();
                    if (existing.type.isAssignableFrom(type)) {
                        existingIterator.remove();
                    } else if (type.isAssignableFrom(existing.type)) {
                        // Ignore this method, a more specific method will handle the object.
                        return;
                    }
                }
                MethodProfile profile = new MethodProfile(method, type, synchModelType);
                methods.add(profile);
            }
        }

        public List<MethodProfile> getMethodProfiles() {
            return methods;
        }

    }

    /**
     * Inner class that handles the CGLIB extension of the publishers.
     */
    private class ComponentExtension<T> implements MethodInterceptor {

        Class<T> base;
        PluginDescriptor descriptor;

        public ComponentExtension(Class<T> base, PluginDescriptor descriptor) {
            this.base = base;
            this.descriptor = descriptor;
        }

        /**
         * Intercepts method calls of the super-class and performs publishing action, if required.
         */
        public Object intercept(Object callingObject, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
            if (method.getAnnotation(Module.class) != null) {
                String methodName = method.getName().toLowerCase();
                Class<?> returnType = method.getReturnType();
                if (methodName.startsWith("get")) {
                    methodName = methodName.substring(3);
                    Object module = descriptor.getModule(methodName);
                    if (module == null) {
                        // Look up the ID associated with the method from the config.
                        String id = descriptor.getModuleID(methodName);
                        if (id != null) {
                            // Look up the descriptor for that ID
                            PluginDescriptor moduleDescriptor = idToDescriptor.get(id);
                            if (moduleDescriptor != null) {
                                // Get the module-- if it is not the right type, then it will return null!
                                module = getModuleByID(returnType, id);
                                if (module != null) {
                                    moduleDescriptor.setModule(methodName, module);
                                    return module;
                                }
                            }
                        }
                    } else {
                        return module;
                    }
                    // Failed to get the module!
                    return null;
                } else if (methodName.startsWith("set")) {
                    methodName = methodName.substring(3);
                    // Set the passed in module (overriding config)
                    // TODO: validation should ensure that there is one param here
                    Object module = params[0];
                    descriptor.setModule(methodName, module);
                    return null;
                } else {
                    log.info("Should be a non-get non-set @Module method here!");
                    // Just call super
                    return methodProxy.invokeSuper(callingObject, params);
                }
            }
            // Call original method
            Object result = methodProxy.invokeSuper(callingObject, params);
            if (method.isAnnotationPresent(Publish.class)) {
                // This is a @Publish method, so pass on to componentRegistry
                if (result != null) {
                    publish(result, callingObject);
                }
            }
            return result;
        }

        public T getExtension() {
            Enhancer enhancer = new Enhancer();
            enhancer.setClassLoader(descriptor.getClassLoader());
            enhancer.setSuperclass(base);
            // Indicates that no callback should be used for this method
            Callback noOp = NoOp.INSTANCE;
            enhancer.setCallbacks(new Callback[]{noOp, this});
            CallbackFilter filter = new CallbackFilter() {
                public int accept(Method method) {
                    // Only use callback if the original method has the @Publish annotation
                    if (method.isAnnotationPresent(Publish.class)) {
                        return 1;
                    } else if (method.isAnnotationPresent(Module.class)) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            };
            enhancer.setCallbackFilter(filter);
            return base.cast(enhancer.create());
        }
    }

    // Holds the listener componentRegistry.
    private TypeMap<List<Object>> listeners;
    // Holds lists of components that are registered to accept specified types.
    private HashMap<Class<?>, List<Class<?>>> acceptors;
    // Executor Service for asynchronous event dispatching.
    private Map<Class<?>, SynchModel> synchModels;
    // List of the components themselves.
    private List<Object> components;
    // Map from component ID to PluginDescriptor.
    private Map<String, PluginDescriptor> idToDescriptor;
    // Map from component source name to ComponentResource.
    private Map<String, ComponentResource> nameToComponentResource;

    private ComponentRegistry() {
        listeners = new TypeMap<List<Object>>();
        acceptors = new HashMap<Class<?>, List<Class<?>>>();
        synchModels = new HashMap<Class<?>, SynchModel>();
        components = new ArrayList<Object>();
        idToDescriptor = new HashMap<String, PluginDescriptor>();
        nameToComponentResource = new HashMap<String, ComponentResource>();
    }

    /**
     * Adds a listener to the componentRegistry.
     *
     * @param type       the type to listen to.
     * @param subscriber the listening subscriber.
     */
    private synchronized void addListener(Class<?> type, Object subscriber) {
        List<Object> list = listeners.get(type);
        if (list == null) {
            list = new ArrayList<Object>();
            listeners.put(type, list);
        }
        list.add(subscriber);
    }

    /**
     * Gets the listeners for the given type.
     *
     */
    @SuppressWarnings("rawtypes")
	private synchronized Set<Object> getListeners(Class<?> type) {
        Set<Class> targetTypes = listeners.keySet();
        Set<Object> subscribers = new HashSet<Object>();
        for (Class<?> targetType : targetTypes) {
            if (targetType.isAssignableFrom(type)) {
                subscribers.addAll(listeners.get(targetType));
            }
        }
        return subscribers;
    }

    /**
     * Adds a type accepter the componentRegistry.
     *
     * @param type       the type to listen to.
     * @param subscriber the listening subscriber.
     */
    public synchronized void addAcceptor(Class<?> type, Class<?> subscriber) {
        List<Class<?>> list = acceptors.get(type);
        if (list == null) {
            list = new ArrayList<Class<?>>();
            acceptors.put(type, list);
        }
        list.add(subscriber);
    }

    /**
     * Gets the acceptors for the given type.
     *
     * @todo fix
     */
    public synchronized Set<Class<?>> getAcceptors(Class<?> type) {
        if (type == null) {
            return new HashSet<Class<?>>(acceptors.get(null));
        }
        log.debug("getAcceptors for " + type.toString());
        Set<Class<?>> targetTypes = acceptors.keySet();
        Set<Class<?>> subscribers = new HashSet<Class<?>>();
        for (Class<?> targetType : targetTypes) {
            if (targetType != null) {
                log.debug("TargetType: " + targetType.toString());
                if (targetType.isAssignableFrom(type)) {
                    log.debug("is assignable.");
                    List<Class<?>> results = acceptors.get(targetType);
                    for (Class<?> aClass : results) {
                        log.debug("Found: " + aClass.toString());
                    }
                    subscribers.addAll(results);
                }
            }
        }
        return subscribers;
    }

    private synchronized void registerSynchModel(SynchModel model) {
        model.initialize();
        synchModels.put(model.getClass(), model);
    }

    private synchronized SynchModel getSynchModel(Class<? extends SynchModel> type) {
        SynchModel model = synchModels.get(type);
        if (model == null) {
            try {
                model = type.newInstance();
            } catch (Exception e) {
                // Unable to instantiate synch model
                throw new EventException("Synch Models must have a no-arg constructor!", e);
            }
            registerSynchModel(model);
        }
        return model;
    }

    /**
     * Handles the mechanism of sending an object to a subscriber.
     *
     * @param object
     * @param publisher
     * @param subscriber
     */
    private void publishToSubscriberHelper(final Object object, final Object publisher, final Object subscriber) {
        // Use the original class to introspect methods (rather than the CGLib class).
        Class<?> type = subscriber.getClass().getSuperclass();
        // Get all valid subscriptions (@Subscribe methods) for this object.
        Subscriptions subs = new Subscriptions(object.getClass());
        {
            Method[] methods = type.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                Subscribe annotation = method.getAnnotation(Subscribe.class);
                if (annotation != null) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    //TODO figure out what the 2nd argument does
                    if (paramTypes.length != 2) {
                        throw new EventException("Invalid @Subscribe method: " + method + ".");
                    } else {
                        Class<?> targetType = paramTypes[0];
                        subs.addMethod(method, targetType, annotation.value());
                    }
                }
            }
        }
        // This list stores all the methods that must be called for this object (could be more than one!)
        List<MethodProfile> methods = subs.getMethodProfiles();
        if (methods.isEmpty()) {
            throw new EventException("Subscriber could not receive object of type: " + object.getClass());
        } else {
            for (final MethodProfile profile : methods) {
                Runnable task = new Runnable() {
                    public void run() {
                        try {
                            profile.getMethod().invoke(subscriber, object, publisher);
                        } catch (Exception e) {
                            log.error("--- Error processing event --- ");
                            log.error("- Publisher: " + publisher);
                            log.error("- Object: " + object);
                            log.error("- Called: " + profile);
                            log.error("-------------------------------");
							if (e instanceof InvocationTargetException) {
								InvocationTargetException ite = (InvocationTargetException) e;
								ite.getTargetException().printStackTrace();
							} else {
								e.printStackTrace();
							}
						}
                    }
                };
                SynchModel synchModel = getSynchModel(profile.getSynchModelType());
                synchModel.addTask(task, subscriber, object, publisher);
            }
        }
    }

    /**
     * Publishes the object to all valid subscribers.
     */
    private void publish(Object object, Object publisher) {
        Set<Object> listeners = getListeners(object.getClass());
        if (listeners != null) {
            for (Object subscriber : listeners) {
                // Do not allow publishing to oneself
                if (subscriber != publisher) {
                    publishToSubscriberHelper(object, publisher, subscriber);
                }
            }
        }
    }

    /**
     * Shuts down the componentRegistry (and terminates any pending aysnchronous dispatches).
     */
    public void shutdown() {
        // Iterate through all active synch models
        Collection<SynchModel> models = synchModels.values();
        for (SynchModel synchModel : models) {
            // Shut down the synch model
            synchModel.shutdown();
        }
    }


    /**
     * Creates the component, registering its @Subscribe and @Publish methods.
     *
     * @param type       the component type to create.
     * @param descriptor the plugin descriptor for the component
     * @return an object of the the given type that has been added to the ComponentRegistry.
     * @throws EventException if the given type does not have a no-arg constructor or has a problem with its @Subscribe
     *                        or @Publish methods.
     */
    public <T> T createComponent(Class<T> type, PluginDescriptor descriptor) throws EventException {
        // Create the extension
        ComponentExtension<T> componentExtension = new ComponentExtension<T>(type, descriptor);
        T component = componentExtension.getExtension();
        // Add component to list of components in registry
        components.add(component);
        // Map the id to the component
        if (descriptor.getID() != null) {
            idToDescriptor.put(descriptor.getID(), descriptor);
        }
        // Map the types this component accepts to the component
        if (VisualPlugin.class.isAssignableFrom(type)) {
            registerTypeAcceptance(type);
        }
        return component;
    }

    private <T> void registerTypeAcceptance(Class<T> type) {
        log.debug("Checking acceptance of types for " + type);
        AcceptTypes at = type.getAnnotation(AcceptTypes.class);
        if (at != null) {
            Class<?>[] accepts = at.value();
            for (int i = 0; i < accepts.length; i++) {
                Class<?> accept = accepts[i];
                log.debug(type + " registering type " + accept);
                addAcceptor(accept, type);
            }
        } else {
            log.debug("No acceptance types-- component will always be on.");
            addAcceptor(null, type);
        }
    }

    public void registerSubscriptions(Object component, PluginDescriptor descriptor) {
        Set<Class<?>> subscribeTypes = new HashSet<Class<?>>();
        Class<?> type = descriptor.getPluginClass();
        // Register @Subscribe methods (including those residing in super-classes).
        Method[] methods = type.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getAnnotation(Subscribe.class) != null) {
                Class<?>[] paramTypes = method.getParameterTypes();
                // todo: More careful checking of method signature would happen at compile-time
                if (paramTypes.length != 2) {
                    throw new EventException("Invalid @Subscribe method: " + method + ".");
                } else {
                    Class<?> targetType = paramTypes[0];
                    // Ignore if descriptor indicates that subscription has been disabled
                    if (!descriptor.isInSubscriptionIgnoreSet(targetType)) {
                        if (subscribeTypes.contains(targetType)) {
                            throw new EventException("More than one method subscribes to '" + targetType + "' in '" + type + "'.");
                        } else {
                            subscribeTypes.add(targetType);
                        }
                    } else {
                    	log.info("  - Subscription disabled: " + targetType);
                    }
                }
            }
        }
        // Add the appropriate listeners
        for (Class<?> subscribeType : subscribeTypes) {
            addListener(subscribeType, component);
        }
    }

    public <T> T[] getModules(Class<T> moduleType) {
        List<T> modules = new ArrayList<T>();
        for (int i = 0; i < components.size(); i++) {
            Object component = components.get(i);
            if (moduleType.isAssignableFrom(component.getClass())) {
                modules.add(moduleType.cast(component));
            }
        }
        @SuppressWarnings("unchecked")
		T[] template = (T[]) Array.newInstance(moduleType, 0);
        return modules.toArray(template);
    }

    private <T> T getModuleByID(Class<T> moduleType, String id) {
        PluginDescriptor descriptor = idToDescriptor.get(id);
        Object component = descriptor.getPlugin();
        if (component != null) {
            if (moduleType.isAssignableFrom(component.getClass())) {
                return moduleType.cast(component);
            }
        }
        return null;
    }

    public PluginDescriptor getDescriptorForPlugin(Object plugin) {
        Set<Map.Entry<String, PluginDescriptor>> entries = idToDescriptor.entrySet();
        for (Iterator<Map.Entry<String, PluginDescriptor>> iterator = entries.iterator(); iterator.hasNext();) {
            Map.Entry<String, PluginDescriptor> entry = iterator.next();
            if (entry.getValue().getPlugin() == plugin) {
                return entry.getValue();
            }
        }
        return null;
    }

    public PluginDescriptor getDescriptorForPluginClass(Class<?> mainPluginClass) {
        Set<Map.Entry<String, PluginDescriptor>> entries = idToDescriptor.entrySet();
        for (Iterator<Map.Entry<String, PluginDescriptor>> iterator = entries.iterator(); iterator.hasNext();) {
            Map.Entry<String, PluginDescriptor> entry = iterator.next();
            if (entry.getValue().getPluginClass() == mainPluginClass) {
                return entry.getValue();
            }
        }
        return null;
    }

    public ComponentResource getComponentResourceByName(String name) {
        return nameToComponentResource.get(name);
    }

    /*
	 * The following methods have been exposed mainly for 
	 * ComponentConfigurationManager
	 */
    
    public void addComponentResource(String name, ComponentResource resource) {
    	ComponentResource existingComponentResource = nameToComponentResource.get(name);
		if (existingComponentResource==null){
			nameToComponentResource.put(name, resource);
		}
    }

    public void removeComponentResource(String name) {
    	nameToComponentResource.remove(name);
    }

	/**
	 * Remove an acceptor from a given type.
	 * @param type
	 * @param acceptor
	 */
	public synchronized void removeAcceptor(Class<?> type, String acceptorName) {
		List<Class<?>> acceptorList = acceptors.get(type);
		List<Class<?>> newList = new ArrayList<Class<?>>();
		Iterator<Class<?>> iter = acceptorList.iterator();
		while(iter.hasNext()) {
			Class<?> componentClass = iter.next();
			if ( componentClass.getName().equals(acceptorName) ) {
				continue;
			}
			newList.add(componentClass);
		}
		acceptors.put(type, newList);
	}

	/**
	 * 
	 * @return a list of all components in the registry.
	 */
	public List<Object> getComponentsList() {
		return components;
	}

	/**
	 * Remove a component from the registry.
	 * @param localComponents
	 */
	public void removeComponent(String componentClassName) {
		/* First, remove from the listeners */
		for (List<?> listenersForOneEvent : listeners.values()) {

			Iterator<?> iter = listenersForOneEvent.iterator();
			while (iter.hasNext()) {
				Object proxiedListener = iter.next();
				String proxiedClazzName = proxiedListener.getClass().getName();
				String[] temp = StringUtils.split(proxiedClazzName, "$$");
				String clazzName = temp[0];

				if (StringUtils.equals(componentClassName, clazzName)) {
					iter.remove();
					break;
				}
			}
		}
		
		/* Second, remove ad acceptor from all types.*/
		for (List<Class<?>> acceptorList : acceptors.values()) {
			Iterator<Class<?>> iter = acceptorList.iterator();
			while (iter.hasNext()) {
				Class<?> acceptor = iter.next();
				if (componentClassName.equals(acceptor.getName())) {
					iter.remove();
				}
			}
		}

		/* Third */
		Iterator<Object> iter = components.iterator();
		while (iter.hasNext()) {
			Object proxiedComponent = iter.next();
			// FIXME use a "deproxy" (see cglib or HibernateProxy)
			String proxiedClazzName = proxiedComponent.getClass().getName();
			String[] temp = StringUtils.split(proxiedClazzName, "$$");
			String clazzName = temp[0];

			if (StringUtils.equals(componentClassName, clazzName)) {
				iter.remove();
			}
		}
	}

	/**
	 * Remove plugin for a given name from the registry.
	 */
	public void removePlugin(String plugin) {
		idToDescriptor.remove(plugin);
	}
		
}
