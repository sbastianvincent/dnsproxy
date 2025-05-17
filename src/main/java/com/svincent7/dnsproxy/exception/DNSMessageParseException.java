package com.svincent7.dnsproxy.exception;

import java.io.IOException;

public class DNSMessageParseException extends IOException {

    public DNSMessageParseException() {
        super();
    }

    public DNSMessageParseException(final String message) {
        super(message);
    }

    public DNSMessageParseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
