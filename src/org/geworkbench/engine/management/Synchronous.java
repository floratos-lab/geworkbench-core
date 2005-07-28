package org.geworkbench.engine.management;


/**
 * Synchronous event synch model.
 *
 * @author John Watkinson
 */
public class Synchronous implements SynchModel {
    public void initialize() {
        // no-op
    }

    public void shutdown() {
        // no-op
    }

    public void addTask(Runnable task) {
        task.run();
    }
}
