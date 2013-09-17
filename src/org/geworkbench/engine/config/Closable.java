package org.geworkbench.engine.config;
 

/**  
 * @author Min You
 * @version 1.0
 */

/**
 * Interface to be implemented by all components that need to do something when application is closing. .
 *  
 */
public interface Closable {
    /**
     * save preference data
     *  
     */
    public void closing();
    
}