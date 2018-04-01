package com.conradmaz.messagehub.messagehubcore.message;

public final class DefaultMessage implements Message {

    private String message;

    public DefaultMessage(String message) {
        this.message = message;
    }

    @Override
    public String getBody() {
        return message;
    }

    @Override
    public String toString() {
        return "DefaultMessage [message=" + message + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DefaultMessage other = (DefaultMessage) obj;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        return true;
    }
}
