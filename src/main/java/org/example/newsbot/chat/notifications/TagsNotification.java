package org.example.newsbot.chat.notifications;

import org.example.newsbot.App;

public class TagsNotification implements Notification {
    @Override
    public void exec(int userId) {
        var user = App.userService.getUser(userId);
        if (user == null) return;
        var sb = new StringBuilder();
        if (user.isAllNews()) sb.append("Вы подписаны на все теги\n");
        else if (user.getTags().size() == 0) sb.append("Вы не подписаны ни на один тег");
        else {
            sb.append("Вы подписаны на теги: ");
            for (var tag : user.getTags())
                sb.append(tag.getName()).append(", ");
            sb.replace(sb.lastIndexOf(","), sb.length(), "");
        }
        App.vkCore.sendMessage(sb.toString(), userId);
    }
}
