package org.example.newsbot.chat.commands;

import com.vk.api.sdk.objects.messages.Message;
import org.example.newsbot.App;
import org.example.newsbot.chat.notifications.TagsNotification;

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
        var user = App.userService.getUser(message.getUserId());
        new TagsNotification().exec(message.getUserId());
        var sb = new StringBuilder();
        var tags = App.tagService.findAllTags();
        for (var tag : tags) {
            if (user.isAllNews() || !user.getTags().contains(tag)) {
                sb.append(tag.getName()).append(", ");
            }
        }
        sb.replace(sb.lastIndexOf(","), sb.length(), "");
        App.vkCore.sendMessage("Доступные для подписки теги:\n" + sb.toString(), message.getUserId());
    }
}

/*
* String keyboard = "{\"inline\": true , \"buttons\": [[";
        var count = 0;
        for (var tag : tags) {
            count++;
            if (count > 10) break;
            keyboard += "{\"action\": {\"type\": \"text\", \"payload\": \"{\\\"button\\\": \\\""
                    + count + "\\\"}\",\"label\": \""
                    + tag.getName() + "\"},\"color\": \"primary\"}";
            if (count % 5 == 0) {
                keyboard += "],[";
            } else {
                keyboard += ",";
            }
//            if (user.isAllNews() || !user.getTags().contains(tag))
//                sb.append(tag.getName()).append(", ");
        }
        keyboard = keyboard.substring(0, keyboard.length() - 4);
        keyboard += "}]]}";
        //sb.replace(sb.lastIndexOf(","), sb.length(), "");
        App.vkCore.sendMessage("Доступные для подписки теги:\n" + sb.toString(), message.getUserId(), keyboard);
*/
