package com.conradmaz.messagehub.messagehubcore;

public class MessageHubException extends Exception {

    private static final long serialVersionUID = 1L;

    public MessageHubException(String msg) {
        super(msg);
    }

    public MessageHubException(String string, Exception e) {
        super(string, e);
    }
    
    
    
}
