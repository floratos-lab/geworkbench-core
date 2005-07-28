package org.geworkbench.engine.management;


/**
 * SynchModel types define how an event synchronization model.
 * Implementing classes can specify which SynchModel they want to use.
 *
 * @author John Watkinson
 */
public interface SynchModel {
    public void initialize();

    public void addTask(Runnable task);

    public void shutdown();
}
