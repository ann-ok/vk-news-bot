package org.example.newsbot.chat.commands;

import com.vk.api.sdk.objects.messages.Message;
import org.example.newsbot.App;

public class UnsubscribeCommand extends Command {

    public UnsubscribeCommand(String name) {
        super(name);
    }

    @Override
    public boolean check(String message) {
        return message.trim().split(" ")[0].toLowerCase().equals("отписаться");
    }

    @Override
    public void exec(Message message) {
        var user = App.userService.getUser(message.getUserId());
        user.setAllNews(false);
        var tags = SubscribeCommand.getTags(message.getBody().replace("отписаться", "").trim());
        if (tags.size() == 0) user.getTags().clear();
        for (String s : tags) {
            var tag = App.tagService.findTagInsensitive(s);
            if (tag != null) user.getTags().remove(tag);
        }
        App.userService.updateUser(user);
        App.vkCore.sendMessage("Ваши подписки успешно обновлены", message.getUserId());
    }
}