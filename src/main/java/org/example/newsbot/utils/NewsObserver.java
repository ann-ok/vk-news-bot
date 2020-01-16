package org.example.newsbot.utils;

import org.example.newsbot.App;
import org.example.newsbot.chat.notifications.NewsNotification;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class NewsObserver {
    private Timestamp lastUpdate;

    public NewsObserver() {
        lastUpdate = Timestamp.valueOf(LocalDateTime.now());
        for (var user : App.userService.findAllUsers()) {
            if (user.getLastUpdate().before(lastUpdate)) lastUpdate = user.getLastUpdate();
        }
    }

    private void ClearPastNews() {
        for (var news : App.newsService.findAllNews()) {
            if (news.getDate().before(lastUpdate)) App.newsService.deleteNews(news);
        }
    }

    private void UsersNewsletter() {
        for (var user : App.userService.findAllUsers()) {
            App.LOG.info("Рассылка новостей пользователю " + user.getId());
            if (user.isAllNews()) {
                int count = 0;
                for (var news : App.newsService.findAllNews()) {
                    if (user.getLastUpdate().before(news.getDate())) {
                        new NewsNotification(news).exec(user.getId());
                        count++;
                    }
                }
                App.LOG.info("Отправлено новостей: " + count);
            } else {
                for (var tag : user.getTags()) {
                    for (var news : tag.getNews()) {
                        if (user.getLastUpdate().before(news.getDate()))
                            new NewsNotification(news).exec(user.getId());
                    }
                }
            }
        }
    }

    private void setLastUpdate() {
        lastUpdate = Timestamp.valueOf(LocalDateTime.now());
        for (var user : App.userService.findAllUsers()) {
            user.setLastUpdate(lastUpdate);
            App.userService.updateUser(user);
        }
    }

    public void exec() {
        App.LOG.info("--- Обновление новостей ---");

        App.LOG.info("Удаление старых новостей...");
        ClearPastNews();
        App.LOG.info("Готово");

        App.LOG.info("Рассылка новых новостей...");
        UsersNewsletter();
        App.LOG.info("Готово");

        setLastUpdate();
        App.LOG.info("--- Завершено ---");
    }
}
