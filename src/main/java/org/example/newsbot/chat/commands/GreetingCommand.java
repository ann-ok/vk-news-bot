package org.example.newsbot.chat.commands;

import com.vk.api.sdk.objects.messages.Message;
import org.example.newsbot.utils.Messenger;

public class GreetingCommand extends Command {

    public GreetingCommand(String name) {
        super(name);
    }

    @Override
    public boolean check(String message) {
        var greetWords = new String[]{
                "привет",
                "здравствуй",
                "приветствую",
                "здравствуйте",
                "салют",
                "hello",
                "hi",
                "здарова",
                "ку",
                "qq",
                "q",
                "приветики",
                "приветули"
        };
        for (var word : message.toLowerCase()
                .replaceAll("[,.]", "")
                .split(" ")) {
            for (var greet : greetWords)
                if (word.equals(greet)) return true;
        }
        return false;
    }

    @Override
    public void exec(Message message) {
        Messenger.sendMessage("Привет, " + message.getFromId(), message.getFromId());
    }
}
