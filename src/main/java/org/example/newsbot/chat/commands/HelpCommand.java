package org.example.newsbot.chat.commands;

import com.vk.api.sdk.objects.messages.Message;
import org.example.newsbot.chat.notifications.HelpNotification;

public class HelpCommand extends Command {

    public HelpCommand(String name) {
        super(name);
    }

    @Override
    public boolean check(String message) {
        var helpWords = new String[]{
                "помоги",
                "помощь",
                "help",
                "h",
                "?"
        };
        for (var word : message.toLowerCase()
                .replaceAll("[,.]", "")
                .split(" ")) {
            for (var help : helpWords)
                if (word.equals(help)) return true;
        }
        return false;
    }

    @Override
    public void exec(Message message) {
        new HelpNotification().exec(message.getUserId());
    }
}
