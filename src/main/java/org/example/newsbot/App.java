package org.example.newsbot;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.example.newsbot.services.NewsService;
import org.example.newsbot.services.ScheduleService;
import org.example.newsbot.services.TagService;
import org.example.newsbot.services.UserService;
import org.example.newsbot.utils.VKCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    public static NewsService newsService;
    public static UserService userService;
    public static TagService tagService;
    public static ScheduleService scheduleService;
    public static VKCore vkCore;

    static {
        LOG.error("Запуск сервисов...");
        newsService = new NewsService();
        userService = new UserService();
        tagService = new TagService();
        scheduleService = new ScheduleService();
        LOG.error("Готово");

        try {
            LOG.error("Инициализация клиента для работы с ВКонтакте...");
            vkCore = new VKCore();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
        LOG.error("Готово");
    }

    public static void main(String[] args) {
        LOG.error("Запуск сервера обработки расписаний...");
        Executors.newCachedThreadPool().execute(new ScheduleServer());
        LOG.error("Готово");

        LOG.error("Запуск сервера обработки новостей...");
        Executors.newCachedThreadPool().execute(new NewsServer());
        LOG.error("Готово");

        LOG.error("Запуск сервера работы с ВКонтакте...");
        Executors.newCachedThreadPool().execute(new VKServer(vkCore));
        LOG.error("Готово");
    }
}