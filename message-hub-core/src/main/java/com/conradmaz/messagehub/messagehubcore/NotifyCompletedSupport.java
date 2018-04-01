package com.conradmaz.messagehub.messagehubcore;

/**
 * {@link NotifyCompletedSupport} provides support to notify when it has
 * completed a task. The {@link NotifyCompletedSupport} will register
 * {@link CompletedListener} to which the notification of completion will be
 * sent to.
 * 
 * @author conrad
 *
 */
public interface NotifyCompletedSupport {

    /**
     * Registers a {@link CompletedListener} to which the notification of
     * completion will be sent to.
     * 
     * @param observer
     */
    void register(CompletedListener observer);

    /**
     * This is used to notify the {@link CompletedListener} of the completion of a task
     */
    void notifyCompleted();

}
