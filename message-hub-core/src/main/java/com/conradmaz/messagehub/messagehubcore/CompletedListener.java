package com.conradmaz.messagehub.messagehubcore;

/**
 * The {@link CompletedListener} provides support to any component that needs to
 * be notified when a task has been completed. This will require the
 * {@link NotifyCompletedSupport}
 * 
 * @author conrad
 *
 */
public interface CompletedListener {

    /**
     * This method will allow the {@link NotifyCompletedSupport} to callback
     * when it has completed a task
     * 
     * @param notifyCompletedSupport
     *            -object that notifies when it has completed a task
     */
    void completed(NotifyCompletedSupport notifyCompletedSupport);

}
