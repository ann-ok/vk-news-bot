package org.example.newsbot.chat.commands;

import com.vk.api.sdk.objects.messages.Message;
import org.example.newsbot.App;

public class UnknownCommand extends Command {

    public UnknownCommand(String name) {
        super(name);
    }

    private boolean emptyBody(String body) {
        return body == null || body.isEmpty();
    }

    @Override
    public void exec(Message message) {
        var msg = emptyBody(message.getBody()) ? "Неизвестная команда" : message.getBody();
        msg += "\n\n(Для справки напишите \"Помощь\")";
        App.vkCore.sendMessage(msg, message.getUserId());
    }
}