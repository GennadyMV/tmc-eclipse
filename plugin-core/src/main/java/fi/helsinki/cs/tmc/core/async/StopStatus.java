package fi.helsinki.cs.tmc.core.async;

/**
 * Helper interface; sometimes stop status needs to be polled from inside the
 * methods the background task is calling. (Example: the submission feedback
 * task polls this while it waits server to respond so that the task can be
 * stopped during the wait). It should provide the information through this
 * interface.
 */
public interface StopStatus {

    boolean mustStop();

}
