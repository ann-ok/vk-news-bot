package org.example.newsbot.utils.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.ArrayList;

public class ParserSchedules {

    public static final ArrayList<String> pdfLinks = new ArrayList<>();
    private static final Logger LOG = LoggerFactory.getLogger(ParserSchedules.class);
    public static Timestamp lastUpdate;

    public static void parse() {
        LOG.info("-= Парсинг расписаний =-");

        LOG.info("Подключение к сайту...");
        Document doc = null;
        try {
            var response = Jsoup.connect("https://www.vyatsu.ru/studentu-1/spravochnaya-informatsiya/raspisanie-zanyatiy-dlya-studentov.html")
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .timeout(100000)
                    .maxBodySize(1024 * 1024 * 20)
                    .execute();
            doc = response.parse();
        } catch (IOException e) {
            LOG.error("Страница с расписанием недоступна");
            LOG.error(e.getMessage());
        }
        LOG.info("Страница получена");

        LOG.info("Выбор элементов...");
        var weeks = doc.select("#listPeriod_131732").select("a");

        LOG.info("Обработка...");
        for (var week : weeks) {
            LOG.info("Получение ссылки...");
            var link = "https://www.vyatsu.ru" + week.attr("href");


            LOG.info("Скачивание файла...");
            var tempPdf = getPdf(link);

            if (pdfLinks.contains(link)) {
                LOG.info("Файл уже обработан");
            } else {
                LOG.info("Парсинг расписания...");
                try {
                    ParserPDF.parse(tempPdf.getAbsolutePath());
                } catch (IOException e) {
                    LOG.error("Ошибка при парсинге расписания");
                    LOG.error(e.getMessage());
                }
                pdfLinks.add(link);
            }
            tempPdf.deleteOnExit();
        }

        lastUpdate = new Timestamp(System.currentTimeMillis());
        LOG.info("-= Готово =-");
    }

    private static File getPdf(String link) {
        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            LOG.error("Файл по ссылке недоступен");
            LOG.error(e.getMessage());
        }

        File tempPdf = null;
        try (InputStream in = url.openStream()) {
            tempPdf = File.createTempFile("temp-schedule-", ".pdf");
            Files.copy(in, Paths.get(tempPdf.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOG.error("Ошибка при загрузке pdf");
            LOG.error(e.getMessage());
        }
        return tempPdf;
    }

}
