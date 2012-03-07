package org.geworkbench.analysis;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.model.analysis.Analysis;
import org.geworkbench.bison.model.analysis.ParamValidationResults;
import org.geworkbench.bison.model.analysis.ParameterPanel;
import org.geworkbench.engine.config.PluginRegistry;
import org.geworkbench.engine.management.ComponentClassLoader;
import org.geworkbench.util.FilePathnameUtils;
import org.ginkgo.labs.util.FileTools;

/**
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: First Genetic Trust Inc.
 * </p>
 * <p/>
 * Implementation of <code>Analysis</code> customized for use within the
 * applications. It handles the saving of all named parameters sets (from within
 * the saveParametersUnderName method). It also provides a default
 * implementation for the validateParameters method by calling the corresponding
 * method in the <code>AbstractSaveableParameterPanel</code>.
 * 
 * @author First Genetic Trust Inc.
 * @author keshav
 * @author yc2480
 * @author os2201
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public abstract class AbstractAnalysis implements Analysis, Serializable,
		java.util.Observer {
	private static final long serialVersionUID = 7028809841554763107L;

	private static final String XML = "xml";
	private static final String FILE_EXTENSION_SEPARATOR = ".";

	private static final String ERR_TXT = FilePathnameUtils
			.getUserSettingDirectoryPath() + "err.txt";

	private Log log = LogFactory.getLog(this.getClass());

	// Analysis types

	public static final int AFFY_DETECTION_CALL_FILTER = 0;

	public static final int MISSING_VALUES_FILTER_TYPE = 1;

	public static final int DEVIATION_BASED_FILTER_TYPE = 2;

	public static final int EXPRESSION_THRESHOLD_FILTER_TYPE = 3;

	public static final int LOG_TRANSFORMATION_NORMALIZER_TYPE = 4;

	public static final int THRESHOLD_NORMALIZER_TYPE = 5;

	public static final int MARKER_MEAN_MEDIAN_CENTERING_NORMALIZER_TYPE = 6;

	public static final int MICROARRAY_MEAN_MEDIAN_CENTERING_NORMALIZER_TYPE = 7;

	public static final int MARKER_MEAN_VARIANCE_NORMALIZER_TYPE = 8;

	public static final int MISSING_VALUE_NORMALIZER_TYPE = 9;

	public static final int SOM_CLUSTERING_TYPE = 10;

	public static final int HIERARCHICAL_CLUSTERING_TYPE = 11;

	public static final int IGNORE_TYPE = 12;

	public static final int REPLACE_TYPE = 13;

	public static final int MIN_TYPE = 14;

	public static final int MAX_TYPE = 15;

	public static final int ZERO_TYPE = 16;

	public static final int MEAN_TYPE = 17;

	public static final int MEDIAN_TYPE = 18;

	public static final int TWO_CHANNEL_THRESHOLD_FILTER_TYPE = 19;

	public static final int TTEST_TYPE = 20;

	public static final int HOUSEKEEPINGGENES_VALUE_NORMALIZER_TYPE = 21;

	public static final int GENEPIX_FlAGS_FILTER_TYPE = 22;

	public static final int QUANTILE_NORMALIZER_TYPE = 23;

	public static final int ALLELIC_FREQUENCY_TYPE = 24;

	public static final int NETBOOST_TYPE = 25;

	public static final int SKYLINE_TYPE = 26;

	public static final int MARKUS_TYPE = 27;

	public static final int MRA_TYPE = 28;

	public static final int SKYBASE_TYPE = 29;

	public static final int PUDGE_TYPE = 30;

	public static final int MEDUSA_TYPE = 31;

	public static final int FOLD_CHANGE_TYPE = 32;

	public static final int BLAST_TYPE = 33;

	/**
	 * Parameters will be saved as XML files in "savedParams" directory under
	 * each component directory.
	 */
	private static final String paramsDir = "savedParams";

	/**
	 * The parameters panel to be use from within the AnalysisPane in order to
	 * collect the analysis parameters from the user.
	 */
	protected AbstractSaveableParameterPanel aspp = null;
	
	private boolean useMarkersFromSelector = true;


	/**
	 * Contains indices that are used in order to recover the set of named
	 * parameter settings that have been saved for a particular analysis. The
	 * indices are (key, value) tuples, where 'key', 'value' are defined as:
	 * <UL>
	 * <LI>key = (this.getIndex(), parameterSetName),</LI>
	 * <LI>value = parameterSet</LI>
	 * This is a static variable, used by all classes that extend
	 * <code>AbstractAnaysis</code>
	 * </UL>
	 */
	protected Map<ParameterKey, Map<Serializable, Serializable>> parameterHash = null;

	/*
	 * Temporary directory name that is obtained from each Component. This is
	 * the place where the named parameter files will be stored. ex: tmpDir will
	 * be "hierarchicalclustering/savedParams/" for hierarchicalclustering
	 */
	protected String tmpDir = null;

	/**
	 * Used in the implementation of the <code>Describable</code> interface.
	 */
	private String description = null;

	/**
	 * Set <code>stopAlgorithm</code> to true to stop the Algorithm, in the
	 * Algorithm, you'll need to check this variable periodically.
	 */
	public boolean stopAlgorithm;

	/**
	 *
	 */
	public AbstractAnalysis() {
		parameterHash = Collections
				.synchronizedMap(new LinkedHashMap<ParameterKey, Map<Serializable, Serializable>>());
		className = this.getClass().getSuperclass().getName();
		String pluginName = PluginRegistry.getNameMap(className);
		setLabel(pluginName);
	}

	/*
	 * This variable to used to store the last saved parameter set's name, so we
	 * can high light it on start up.
	 */
	private String lastParameterSetName = "";

	/*
	 * This variable is used to store it's className
	 */
	private String className;

	/**
	 * load all saved parameter sets in tmpDir
	 */
	protected void loadSavedParameterSets() {
		// FIXME: It assume all xml files are parameter files, we should check
		// if it's parameter files or not
		// FIXME: we should check parameter versions, see if we know how to load
		// that version.
		String path = tmpDir;
		String files;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles.length > 0) {
			java.util.Arrays.sort(listOfFiles, new Comparator<File>() {
				public int compare(File a, File b) {
					return (int) (a.lastModified() - b.lastModified());
				}
			});
			lastParameterSetName = unscrubFilename(listOfFiles[listOfFiles.length - 1]
					.getName());
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					files = listOfFiles[i].getName();
					if (files.endsWith(".xml") || files.endsWith(".XML")) {
						String setName = unscrubFilename(files);
						Map<Serializable, Serializable> parameters = deserializeNamedParameterSet(setName);
						ParameterKey key = new ParameterKey(getIndex(), setName);
						log.debug("Try loading saved parameter file " + files);
						log.debug("We are looking for " + key.toString());
						log.debug("This one looks like it's for "
								+ parameters.get(ParameterKey.class
										.getSimpleName()));
						/*
						 * Since one package can contain multiple components
						 * which have different panels, we need to make sure we
						 * got the right one.
						 */
						if (parameters != null)
							if (parameters.get(
									ParameterKey.class.getSimpleName()).equals(
									key.toString())) {
								log.debug("put it in to parameterHash");
								parameterHash.put(key, parameters);
								lastParameterSetName = setName;
							}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @return Return the name of last saved parameter set.
	 */
	public String getLastSavedParameterSetName() {
		// FIXME: currently it return the name of last saved parameter set from
		// last launch of geworkbench, it didn't reflect the name of last saved
		// parameter set from this launch.
		return lastParameterSetName;
	}
	
	
	 
	public void useMarkersFromSelector(boolean status) {
		useMarkersFromSelector = status;
	}

	 
	public boolean useMarkersFromSelector() {
		return useMarkersFromSelector;
	}
	

	/*
	 * Translate filename back to set name
	 */
	private String unscrubFilename(String filename) {
		if (StringUtils.contains(filename, File.separatorChar))
			filename = StringUtils.substringAfterLast(filename,
					System.getProperty("file.separator"));
		if (StringUtils.contains(filename, FILE_EXTENSION_SEPARATOR + XML))
			filename = StringUtils.substringBeforeLast(filename,
					FILE_EXTENSION_SEPARATOR + XML);
		return filename;
	}

	/**
	 * Deletes a saved setting based on the saved parameter name.
	 * 
	 * @param name
	 *            - name of the saved parameter
	 */
	public void removeNamedParameter(String name) {
		// remove from memory
		parameterHash.remove(new ParameterKey(getIndex(), name));
		// remove from file
		deleteParameters(name);
	}

	/**
	 * Returns the names of the parameter sets that were saved through a call to
	 * saveParameters(String filename). Names can be removed using
	 * <code>removeNamedParameter()</code>
	 * 
	 * @return Names of parameterSets as an array of Strings.
	 */
	public String[] getNamesOfStoredParameterSets() {
		Vector<String> paramNames = new Vector<String>();
		for (ParameterKey key : parameterHash.keySet()) {
			if (key.getClassName().equals(getIndex())) {
				paramNames.add(key.getParameterName());
			}
		}

		String[] parameterGroups = new String[paramNames.size()];
		paramNames.toArray(parameterGroups);
		return parameterGroups;
	}

	/**
	 * Similar to getNamedParameterSetPanel, but instead of return the panel, it
	 * directly set (reuse) the current panel.
	 * 
	 * @param name
	 */
	public void setNamedParameterSetPanel(String name) {
		Map<Serializable, Serializable> returnedParams = parameterHash
				.get(new ParameterKey(getIndex(), name));
		aspp.fillDefaultValues(returnedParams);
		aspp.setParameters(returnedParams);
	}

	/**
	 * Returns the parameter values that were stored (in parameterHash in
	 * memory) under the designated name.
	 * 
	 * @param name
	 * @return Return a Map<Serializable, Serializable>, which use parameter
	 *         name as the key and parameter value as the value.
	 */
	public Map<Serializable, Serializable> getNamedParameterSet(String name) {
		Map<Serializable, Serializable> returnedParams = parameterHash
				.get(new ParameterKey(getIndex(), name));
		return returnedParams;
	}

	/**
	 * Returns the parameters panel populated with the parameter values that
	 * where stored under the designated 'name'.
	 * 
	 * @param name
	 * @return
	 */
	public ParameterPanel deserializeNamedParameterSetPanel(String name) {
		try {
			FileInputStream fis = new FileInputStream(new File(
					scrubFilename(name)));
			XMLDecoder ois = new XMLDecoder(fis);
			Thread.currentThread().setContextClassLoader(
					this.getClass().getClassLoader()); // to avoid java bug
			// #6329581
			// newaspp = (AbstractSaveableParameterPanel) ois.readObject();
			HashMap<Serializable, Serializable> parameters = (HashMap<Serializable, Serializable>) ois
					.readObject();
			this.setParameters(parameters);
		} catch (Exception e) {
			log.error(e, e);
		}

		return this.getParameterPanel();
	}

	/**
	 * Returns the parameters panel populated with the parameter values that
	 * where stored under the designated 'name'.
	 * 
	 * @param name
	 * @return
	 */
	public Map<Serializable, Serializable> deserializeNamedParameterSet(
			String name) {
		HashMap<Serializable, Serializable> parameters = null;
		try {
			FileInputStream fis = new FileInputStream(new File(
					scrubFilename(name)));
			XMLDecoder ois = new XMLDecoder(fis);
			Thread.currentThread().setContextClassLoader(
					this.getClass().getClassLoader()); // to avoid java bug
			// #6329581
			parameters = (HashMap<Serializable, Serializable>) ois.readObject();
		} catch (Exception e) {
			log.error(e, e);
		}

		return parameters;
	}

	/**
	 * Check if the inputed parameterSet already exist in memory or not.
	 * 
	 * @param parameterSet
	 * @return
	 */
	public boolean parameterSetExist(
			Map<Serializable, Serializable> parameterSet) {
		boolean result = false;
		if (parameterSet.get(ParameterKey.class.getSimpleName()) != null)
			result = parameterHash.values().contains(parameterSet);
		else {// I'll need to loop through all the records. disregard the
				// ParameterKey and compare others.
			for (Map<Serializable, Serializable> property : parameterHash
					.values()) {
				Map<Serializable, Serializable> pureParameter = new HashMap<Serializable, Serializable>();
				pureParameter.putAll(property);
				pureParameter.remove(ParameterKey.class.getSimpleName());
				if (pureParameter.equals(parameterSet))
					result = true;
			}
		}
		return result;
	}

	/**
	 * Convenience method - returns a string which should be unique for each
	 * subclass of <code>AbstractAnalysis</code>. This string is used as part of
	 * the hash that is used to store/recover named parameter sets.
	 * 
	 * @return String Unique "tagging" string for an Analysis type.
	 */
	private String getIndex() {
		// Using the display name of the analysis as its index is not entirely
		// appropriate. Ideally we would like to use some sorts of a hash based
		// on its corresponding .class file.
		return className;
	}

	/**
	 * Set the panel for this analysis to the specific component's 'panel'. This
	 * method also set the tmpDir to the tmpDir under that parameter panel's
	 * directory, and load all saved parameter files under that directory.
	 * 
	 * @param panel
	 */
	public void setDefaultPanel(AbstractSaveableParameterPanel panel) {
		aspp = panel;
		if (aspp != null) {
			aspp.setVisible(true);
			setParameterFilesPath(aspp);
		}
		loadSavedParameterSets();
	}

	/**
	 * Set the path to the parameter's temporary directory for the component. If
	 * the directory does not exist, create it.
	 * 
	 * @param aspp
	 */
	private void setParameterFilesPath(AbstractSaveableParameterPanel aspp) {
		String directoryName = "";
		ClassLoader classLoader = aspp.getClass().getClassLoader();
		if (classLoader instanceof ComponentClassLoader) {
			directoryName = ((ComponentClassLoader) classLoader)
					.getComponentResource().getName();
		}
		String userSettingDirectory = FilePathnameUtils
				.getUserSettingDirectoryPath() + directoryName;

		File parentDir = new File(userSettingDirectory, paramsDir);
		tmpDir = parentDir.getPath() + File.separatorChar;
		File pFile = new File(tmpDir);
		if (!pFile.exists()) {
			pFile.mkdirs();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.model.analysis.Analysis#getParameterPanel()
	 */
	public ParameterPanel getParameterPanel() {
		return aspp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.model.analysis.Analysis#validateParameters()
	 */
	public ParamValidationResults validateParameters() {
		// Delegates the validation to the panel.
		if (aspp == null)
			return new ParamValidationResults(true, null);
		else
			return aspp.validateParameters();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.geworkbench.bison.datastructure.properties.DSDescribable#addDescription
	 * (java.lang.String)
	 */
	public void setDescription(String desc) {
		description = desc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.geworkbench.bison.datastructure.properties.DSDescribable#getDescriptions
	 * ()
	 */
	public String getDescription() {
		return description;
	}

	private String label = null;

	/**
	 * 
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * 
	 * @param name
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(java.util.Observable ob, Object o) {

		log.debug("initiated close");

		stopAlgorithm = true;
	}

	/**
	 * 
	 * @return
	 */
	public String createHistory() {
		return aspp.getDataSetHistory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.model.analysis.Analysis#getParameters()
	 */
	public Map<Serializable, Serializable> getParameters() {
		return aspp.getParameters();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.geworkbench.bison.model.analysis.Analysis#setParameters(java.util
	 * .Map)
	 */
	public void setParameters(Map<Serializable, Serializable> parameters) {
		this.aspp.fillDefaultValues(parameters);
		this.aspp.setParameters(parameters);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.geworkbench.bison.model.analysis.Analysis#saveParameters(java.lang
	 * .String)
	 * 
	 * Current parameters will be saved in both memory and files under given
	 * name.
	 */
	public void saveParameters(String setName) {

		/*
		 * Cache the parameters stored under filename in a hash table. The key
		 * is the ParameterKey, and the value is the parameters.
		 */
		ParameterKey key = new ParameterKey(getIndex(), setName);

		parameterHash.put(key, aspp.getParameters());

		writeParametersAsXml(setName);
	}

	/**
	 * Saves current parameters this analysis using the {@link XMLEncoder}.
	 */
	private void writeParametersAsXml(String name) {
		FileOutputStream fos = null;
		XMLEncoder oos = null;
		ClassLoader currentClassLoader = null;
		PrintStream orgErrStream = null;
		try {
			fos = new FileOutputStream(new File(scrubFilename(name)));
			oos = new XMLEncoder(fos);

			/*
			 * Swap the loader to the loader that loads the actual panel, and
			 * redirect the System.err printed by XMLEncoder when writing the
			 * xml object.
			 * 
			 * See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6329581.
			 */
			currentClassLoader = Thread.currentThread().getContextClassLoader();
			ClassLoader cl = aspp.getClass().getClassLoader();
			Thread.currentThread().setContextClassLoader(cl);

			orgErrStream = System.err;

			PrintStream fileErrStream = new PrintStream(new FileOutputStream(
					ERR_TXT, true));
			System.setErr(fileErrStream);
			ParameterKey key = new ParameterKey(getIndex(), name);
			Map<Serializable, Serializable> pMap = aspp.getParameters();
			pMap.put(ParameterKey.class.getSimpleName(), key.toString());
			oos.writeObject(pMap);

			oos.flush();
			oos.close();
			fos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			Thread.currentThread().setContextClassLoader(currentClassLoader);
			System.setErr(orgErrStream);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.geworkbench.bison.model.analysis.Analysis#deleteParameters(java.lang
	 * .String)
	 * 
	 * We generate the filename from the set's name, and delete the file
	 */
	public void deleteParameters(String name) {
		File pFile = new File(scrubFilename(name));
		pFile.delete();
		log.debug("\tFile deleted.  File still exists? " + pFile.exists());
	}

	/*
	 * Add path and extension to the file name if needed.
	 */
	public String scrubFilename(String filename) {
		if (!StringUtils.startsWith(filename, tmpDir)) {
			filename = tmpDir + filename;
		}
		if (!StringUtils.endsWith(filename, FILE_EXTENSION_SEPARATOR + XML)) {
			filename = filename + FILE_EXTENSION_SEPARATOR + XML;
		}
		return filename;
	}

	/**
	 * 
	 * @param maSetView
	 * @return
	 */
	public String generateHistoryForMaSetView(
			DSMicroarraySetView<DSGeneMarker, DSMicroarray> maSetView, boolean useMarkersFromSelector) {
		StringBuilder ans = new StringBuilder(
				"=The MicroarraySetView used for analysis: ");
		/* Generate text for microarrays/groups */
		ans.append(FileTools.NEWLINE);
		try {
			log.debug("We got a " + maSetView.items().getClass().toString());
			if (maSetView.items().getClass() == CSPanel.class) {
				log.debug("situation 1: microarraySets selected");
				DSItemList<DSPanel<DSMicroarray>> paneltest = ((DSPanel<DSMicroarray>) maSetView
						.items()).panels();

				ans.append("==Used Microarray Sets [")
						.append(paneltest.size() + "]==")
						.append(FileTools.NEWLINE);
				for (DSPanel<DSMicroarray> temp : paneltest) {
					ans.append(FileTools.TAB + temp.toString()).append(
							FileTools.NEWLINE);
					for (DSMicroarray temp2 : temp) {
						ans.append(FileTools.TAB).append(FileTools.TAB)
								.append(temp2.toString())
								.append(FileTools.NEWLINE);
					}
				}
			} else if (maSetView.items().getClass() == CSMicroarraySet.class) {
				log.debug("situation 2: microarraySets not selected");
				CSMicroarraySet exprSet = (CSMicroarraySet) maSetView.items();
				ans.append("==Used Microarrays [").append(exprSet.size())
						.append("]==").append(FileTools.NEWLINE);
				for (Iterator<DSMicroarray> iterator = exprSet.iterator(); iterator
						.hasNext();) {
					DSMicroarray array = iterator.next();
					ans.append(FileTools.TAB).append(array.getLabel())
							.append(FileTools.NEWLINE);
				}
			}
			ans.append("==End of Microarray Sets==").append(FileTools.NEWLINE);
			/* Generate text for markers */

			if (useMarkersFromSelector) {
				DSPanel<DSGeneMarker> paneltest = maSetView.getMarkerPanel();
				if (maSetView.useMarkerPanel()) {
					if ((paneltest!=null) && (paneltest.size()>0)){
						log.debug("situation 3: markers selected");

						ans .append( "==Used Markers [" ).append( paneltest.size() ).append( "]==\n" );
						for (Object obj : paneltest) {
							CSExpressionMarker temp = (CSExpressionMarker) obj;
							ans .append( "\t" ).append( temp.getLabel() ).append( "\n" );
						}
					}else{
						log.debug("situation 4: no markers selected.");
						DSItemList<DSGeneMarker> markers = maSetView.markers();
						ans .append( "==Used Markers [" ).append( markers.size() ).append( "]==\n" );
						for (DSGeneMarker marker : markers) {
							ans .append( "\t" ).append( marker.getLabel() ).append( "\n" );
						}
					}
				} else {
					log.debug("situation 5: All Markers selected.");
					DSItemList<DSGeneMarker> markers = maSetView.allMarkers();
					ans .append( "==Used Markers [" ).append( markers.size() ).append( "]==" )
							.append( FileTools.NEWLINE );
					for (DSGeneMarker marker : markers) {
						ans .append( FileTools.TAB ).append( marker.getLabel()
						).append( FileTools.NEWLINE );
					}
				}


				ans.append("==End of Used Markers==").append(FileTools.NEWLINE);
			}
		} catch (ClassCastException cce) {
			/* it's not a DSPanel, we generate nothing for panel part */
			log.error(cce);
		}
		ans.append("=End of MicroarraySetView data=");
		return ans.toString();
	}

	@SuppressWarnings("rawtypes")
	public String generateHistoryStringForGeneralDataSet(DSDataSet dataset) {
		if (dataset == null) {
			return "No information on the data set." + FileTools.NEWLINE;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("The data set used for analysis is [ ");
			sb.append(dataset.getDataSetName());
			sb.append(" ] from file [ ");
			sb.append(dataset.getFile());
			sb.append(" ]." + FileTools.NEWLINE);
			return sb.toString();
		}
	}

}
