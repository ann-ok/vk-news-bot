package org.example.newsbot.chat.notifications;

import org.example.newsbot.App;
import org.example.newsbot.models.News;
import org.example.newsbot.utils.Messenger;

import java.text.SimpleDateFormat;

public class NewsNotification implements Notification {

    private final News news;

    public NewsNotification(News news) {
        this.news = news;
    }

    @Override
    public void exec(int userId) {
        var user = App.userService.getUser(userId);
        if (user == null) return;
        String msg = new SimpleDateFormat("'&#128197; 'dd.MM.yyyy HH:mm'\n'")
                .format(news.getDate())
                + "&#10071;"
                + news.getHead().toUpperCase()
                + "\n"
                + news.getContent();
        Messenger.sendMessage(msg, userId);
    }
}
