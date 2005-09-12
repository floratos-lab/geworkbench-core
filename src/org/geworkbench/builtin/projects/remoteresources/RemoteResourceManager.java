package org.geworkbench.builtin.projects.remoteresources;

import java.io.File;
import java.util.ArrayList;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class RemoteResourceManager {
    private ArrayList<RemoteResource> existedResources;

    public RemoteResourceManager() {
        existedResources = new ArrayList<RemoteResource>();
        String propertyfilename = System.getProperty("remotepropertyfile");
        if (propertyfilename != null && new File(propertyfilename).canRead()) {

            init(new File(propertyfilename));
        }else{
            init();
        }

    }

    /**
     * A default setup when no property file is found.
     * init
     */
    protected void init() {
        RemoteResource rr = new RemoteResource("NCI_CaArray", "nci.cabio.nih.gov", "http:", "manju", "test");
        existedResources.add(rr);
    }

    /**
     * Init the existed resources from a file.
     * @param propertyfilename File
     */
    private void init(File propertyfilename) {

    }

    public String getItems(){
       return existedResources.get(0).getShortname();
    }

   public RemoteResource getSelectedResouceByName(String name){
       for(RemoteResource rr: existedResources){
           if(rr.getShortname().equals(name)){
               return rr;
           }
       }
       return null;

   }
    /**
     * Edit the properties of a romoteResource
     */
    public void editRemoteResource(int i, RemoteResource rResource) {
        //existedResources.remove(i);
        RemoteResource rr = existedResources.get(i);
        rr.update(rResource);
    }

    /**
     * Delete  one resource object
     * @param rResource RemoteResource
     * @return boolean
     */
    public boolean deleteRemoteResource(RemoteResource rResource) {
        return existedResources.remove(rResource);
    }

    /**
     * Delete one resource based on its index position.
     * @param rResourceIndex int
     * @return boolean
     */
    public boolean deleteRemoteResource(int rResourceIndex) {
        if (existedResources.remove(rResourceIndex) != null) {
            return true;
        }
        return false;
    }

    /**
     * Add one new resource.
     * @param newResource RemoteResource
     * @return boolean
     */
    public boolean addRemoteResource(RemoteResource newResource) {
        if (!existedResources.contains(newResource)) {
            return existedResources.add(newResource);
        } else {
            return false;
        }
    }


}
