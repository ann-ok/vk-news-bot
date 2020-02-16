package org.example.newsbot.utils;

import com.vk.api.sdk.objects.messages.Message;

import java.util.List;

public class MessageHandler implements Runnable {

    private final List<Message> messages;

    public MessageHandler(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public void run() {
        for (var message : messages) {
            CommandManager.execute(message);
        }
    }
}