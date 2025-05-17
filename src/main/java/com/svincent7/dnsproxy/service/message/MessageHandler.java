package com.svincent7.dnsproxy.service.message;

import com.svincent7.dnsproxy.model.Message;

public interface MessageHandler {
    Message handleMessage(Message msg);
}
