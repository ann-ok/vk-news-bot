package org.example.newsbot.chat.commands;

import com.vk.api.sdk.objects.messages.Message;
import org.example.newsbot.App;
import org.example.newsbot.chat.notifications.TagsNotification;
import org.example.newsbot.utils.Messenger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SubscribeCommand extends Command {

    public SubscribeCommand(String name) {
        super(name);
    }

    public static ArrayList<String> getTags(String message) {
        var result = new ArrayList<String>();
        var words = message.split("[\"']");
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i % 2 == 0) messageBuilder.append(words[i].trim());
            else result.add(words[i].trim());
        }
        message = messageBuilder.toString().trim();
        result.addAll(Arrays.stream(message.split(" "))
                .filter(x -> x.trim().length() != 0)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public boolean check(String message) {
        return message.trim()
                .split(" ")[0]
                .toLowerCase()
                .equals("подписаться");
    }

    @Override
    public void exec(Message message) {
        var user = App.userService.getUser(message.getFromId());
        if (user.isAllNews()) {
            new TagsNotification().exec(user.getId());
            return;
        }
        var tags = getTags(message.getText()
                .toLowerCase()
                .replace("подписаться", "")
                .trim());
        if (tags.size() == 0) user.setAllNews(true);
        for (String s : tags) {
            var tag = App.tagService.findTagInsensitive(s);
            if (tag != null) user.getTags().add(tag);
        }
        App.userService.updateUser(user);
        Messenger.sendMessage("Ваши подписки успешно обновлены", message.getFromId());
    }
}