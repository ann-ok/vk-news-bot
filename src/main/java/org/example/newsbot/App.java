package org.example.newsbot;

import org.example.newsbot.servers.NewsServer;
import org.example.newsbot.servers.RemindServer;
import org.example.newsbot.servers.ScheduleServer;
import org.example.newsbot.servers.VkServer;
import org.example.newsbot.services.NewsService;
import org.example.newsbot.services.ScheduleService;
import org.example.newsbot.services.TagService;
import org.example.newsbot.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    public static NewsService newsService;
    public static UserService userService;
    public static TagService tagService;
    public static ScheduleService scheduleService;

    public static void main(String[] args) {
        LOG.error("Запуск сервисов...");
        newsService = new NewsService();
        userService = new UserService();
        tagService = new TagService();
        scheduleService = new ScheduleService();
        LOG.error("Готово");

        LOG.error("Запуск сервера обработки расписаний...");
        Executors.newCachedThreadPool().execute(new ScheduleServer());
        LOG.error("Готово");

        LOG.error("Запуск сервера обработки новостей...");
        Executors.newCachedThreadPool().execute(new NewsServer());
        LOG.error("Готово");

        LOG.error("Запуск сервера работы с ВКонтакте...");
        Executors.newCachedThreadPool().execute(new VkServer());
        LOG.error("Готово");

        LOG.error("Запуск сервера обработки напоминаний...");
        Executors.newCachedThreadPool().execute(new RemindServer());
        LOG.error("Готово");
    }
}