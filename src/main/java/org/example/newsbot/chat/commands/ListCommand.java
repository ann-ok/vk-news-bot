package org.example.newsbot.chat.commands;

import com.vk.api.sdk.objects.messages.Message;
import org.example.newsbot.App;
import org.example.newsbot.chat.notifications.TagsNotification;
import org.example.newsbot.utils.Messenger;

public class ListCommand extends Command {

    public ListCommand(String name) {
        super(name);
    }

    @Override
    public boolean check(String message) {
        return message.trim().split(" ")[0].toLowerCase().equals("список");
    }

    @Override
    public void exec(Message message) {
        var user = App.userService.getUser(message.getFromId());
        new TagsNotification().exec(message.getFromId());
        var sb = new StringBuilder();
        var tags = App.tagService.findAllTags();
        for (var tag : tags) {
            if (user.isAllNews() || !user.getTags().contains(tag)) {
                sb.append(tag.getName()).append(", ");
            }
        }
        sb.replace(sb.lastIndexOf(","), sb.length(), "");
        Messenger.sendMessage("Доступные для подписки теги:\n" + sb.toString(), message.getFromId());
    }
}
