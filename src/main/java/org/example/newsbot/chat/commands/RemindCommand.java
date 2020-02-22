package org.example.newsbot.chat.commands;

import com.vk.api.sdk.objects.messages.Message;
import org.example.newsbot.App;
import org.example.newsbot.utils.Messenger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemindCommand extends Command {

    public RemindCommand(String name) {
        super(name);
    }

    @Override
    public boolean check(String message) {
        return message.toLowerCase().contains("напоминать");
    }

    @Override
    public void exec(Message message) {
        var text = message.getText();
        var user = App.userService.getUser(message.getFromId());
        if (text.split(" ")[0].toLowerCase().equals("не")) {
            user.setRemind(false);
            Messenger.sendMessage("Напоминания о парах приходить не будут.", message.getFromId());
        } else {
            Pattern pattern = Pattern.compile(" [0-9]+");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                user.setRemind(true);
                var interval = Integer.parseInt(matcher.group().trim());
                user.setRemindInterval(interval);
                Messenger.sendMessage("Напоминания о парах будут приходить за "
                        + interval + " минут.", message.getFromId());
            }
        }
        App.userService.saveUser(user);
    }
}
