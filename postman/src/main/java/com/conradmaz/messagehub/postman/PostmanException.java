package com.conradmaz.messagehub.postman;

public class PostmanException extends Exception {
	public static final String MESSAGE_SOURCE_DOES_NOT_EXIST = "Postman repository not set. Please set the repository to collect the messages from!";
	public static final String NO_MESSAGES_TO_SEND = "No messages to send!";

	private static final long serialVersionUID = -3915386699659555030L;
	public static final String CONNECTION_ERROR = "Connection Error";
	public static final String COFIGURATION_ERROR = "Configuration Error";
	public static final String APPLICATION_ERROR = "Application Error";
	private final String errorCode;

	public PostmanException(String errorCode, String message, Throwable e) {
		super(message, e);
		this.errorCode = errorCode;
	}

	public PostmanException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	@Override
	public String toString() {
		return errorCode + ":" + super.toString();
	}

}
