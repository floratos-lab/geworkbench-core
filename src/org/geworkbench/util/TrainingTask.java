package org.geworkbench.util;

/**
 * A training task is a cancellable task that shows training progress.
 *
 * @author John Watkinson
 */
public interface TrainingTask {
    TrainingProgressListener getTrainingProgressListener();

    void setTrainingProgressListener(TrainingProgressListener trainingProgressListener);

    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
