package org.example.newsbot.utils;

import org.example.newsbot.App;
import org.example.newsbot.models.News;
import org.example.newsbot.models.Tag;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Parser {
    private static final int MAX_DAYS_TERM = 1;

    public static void initiate() throws IOException {
        var minDate = Timestamp.valueOf(LocalDateTime.now().minusDays(MAX_DAYS_TERM));
        App.LOG.info("-= Парсинг новостей =-");
        App.LOG.info("Подключение к сайту...");
        mainLoop:
        for (int i = 1; i < 100; i++) {
            App.LOG.info("Парсинг страницы " + i + "...");
            Document page = Jsoup.connect("https://progorod43.ru/articles?page=" + i)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();
            App.LOG.info("Страница получена");
            App.LOG.info("Выбор элементов...");
            Elements listNews = page.select("div.article-list__item");
            for (Element element : listNews.select(".article-list__item-right")) {
                // Дата публикации
                var date = element.select(".article-list__item-date").text();
                var timestamp = ParseTimestamp(date);
                if (timestamp.before(minDate)) {
                    App.LOG.info("Все новости за последние " + MAX_DAYS_TERM + " дней добавлены");
                    break mainLoop;
                }
                var title = element.select(".article-list__item-title");
                // Заголовок новости
                var head = title.select("a.link_nodecor").text();
                if (App.newsService.findNews(head) != null) break mainLoop;
                // Ссылка на полную новость
                var link = title.select("a.link_nodecor").attr("abs:href");
                Document subPage = Jsoup.connect(link)
                        .userAgent("Chrome/4.0.249.0 Safari/532.5")
                        .referrer("http://www.google.com")
                        .get();
                Element pageContent = subPage.selectFirst("div#data-io-article-url.article__main-content");
                if (pageContent == null) continue;
                HtmlToPlainText formatter = new HtmlToPlainText();
                // Содержание новости
                var content = formatter.getPlainText(pageContent);
                var news = new News(timestamp, head, content);
                App.newsService.saveNews(news);
                var tags = element.select(".article-list__item-tag");
                for (var tag : tags) {
                    var found = App.tagService.findTag(tag.text());
                    if (found == null) {
                        var newTag = new Tag(tag.text());
                        App.tagService.saveTag(newTag);
                        found = newTag;
                    }
                    found.getNews().add(news);
                    news.getTags().add(found);
                }
            }
            App.LOG.info("-= Готово =-");
        }
    }

    private static Timestamp ParseTimestamp(String date) {
        var months = new String[]{
                "января",
                "февраля",
                "марта",
                "апреля",
                "мая",
                "июня",
                "июля",
                "августа",
                "сентября",
                "октября",
                "ноября",
                "декабря"
        };
        date = date.trim();
        date = date.replace(",", "");
        for (int i = 0; i < 12; i++) {
            date = date.replace(months[i], String.valueOf(i + 1));
        }
        var words = date.split(" ");
        var dateFormatted = String.format("%s-%s-%s %s:00", words[2], words[1], words[0], words[3]);
        return Timestamp.valueOf(dateFormatted);
    }
}
