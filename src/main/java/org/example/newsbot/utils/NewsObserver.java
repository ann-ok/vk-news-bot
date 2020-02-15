package org.example.newsbot.utils;

import org.example.newsbot.App;
import org.example.newsbot.chat.notifications.NewsNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class NewsObserver {

    private static final Logger LOG = LoggerFactory.getLogger(NewsObserver.class);
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
            LOG.info("Рассылка новостей пользователю " + user.getId());
            if (user.isAllNews()) {
                int count = 0;
                for (var news : App.newsService.findAllNews()) {
                    if (user.getLastUpdate().before(news.getDate())) {
                        new NewsNotification(news).exec(user.getId());
                        count++;
                    }
                }
                LOG.info("Отправлено новостей: " + count);
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
        LOG.info("--- Обновление новостей ---");

        LOG.info("Удаление старых новостей...");
        ClearPastNews();
        LOG.info("Готово");

        LOG.info("Рассылка новых новостей...");
        UsersNewsletter();
        LOG.info("Готово");

        setLastUpdate();
        LOG.info("--- Завершено ---");
    }
}
