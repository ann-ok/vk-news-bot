package org.example.newsbot.utils.parsers;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import org.example.newsbot.App;
import org.example.newsbot.models.Schedule;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class ParserPDF {
    public static void parse(String pdf) throws IOException {
        PdfReader reader = new PdfReader(new FileInputStream(pdf));
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        TextExtractionStrategy strategy;
        StringBuilder textSB = new StringBuilder();
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
            textSB.append(strategy.getResultantText());
        }
        reader.close();

        String totalText = textSB.toString();
        totalText = totalText.substring(totalText.indexOf("понедельник"));
        int k = totalText.indexOf("Министерство");
        int k1 = totalText.indexOf("тет\"", k) + 4;
        totalText = totalText.substring(0, k) + totalText.substring(k1);

        var days = new String[]{
                "понедельник",
                "вторник",
                "среда",
                "четверг",
                "пятница",
                "суббота",
                "воскресенье"
        };

        var daySchedules = new ArrayList<String>();
        k = 0;
        for (int i = 1; i <= 14; i++) {
            int l = totalText.indexOf(days[i % 7], k);
            if (l == -1) daySchedules.add(totalText.substring(k));
            else daySchedules.add(totalText.substring(k, l));
            k = l;
        }
        for (var day : daySchedules) {
            int l = day.indexOf('\n');

            var groupName = "ФИб-3301-51-00";
            var head = day.substring(0, l);
            var date = getDateFromHead(head);

            day = day.substring(l + 1);
            var sb = new StringBuilder();
            getDayClasses(sb, day);
            App.scheduleService.saveSchedule(new Schedule(date, groupName, sb.toString()));
        }
    }

    private static Timestamp getDateFromHead(String head) {
        var nums = head.split(" ")[1].split("\\.");
        return Timestamp.valueOf(String.format("20%s-%s-%s 12:00:00", nums[2], nums[1], nums[0]));
    }

    private static void getDayClasses(StringBuilder sb, String schedule) {
        var pairs = schedule
                .replaceAll("[_*]", "")
                .replaceAll("ФИб-3301-51-00, 0. подгруппа ", "")
                .split("\n \n");
        var emptyDay = true;
        for (var pair : pairs) {
            if (!pair
                    .substring(11)
                    .trim()
                    .replaceAll(" ", "")
                    .equals("")) {
                emptyDay = false;

                pair = pair.replaceAll("\n", " ").replaceAll(" {2}", " ");

                var icon = "";
                if (pair.contains("Лабораторная работа")) {
                    pair = pair.replaceAll("Лабораторная работа ", "\n&#128104; ");
                    icon = "&#128736;";
                } else if (pair.contains("Лекция")) {
                    pair = pair.replaceAll("Лекция ", "\n&#128104; ");
                    icon = "&#128221;";
                } else if (pair.contains("Практическое занятие")) {
                    pair = pair.replaceAll("Практическое занятие ", "\n&#128104; ");
                    icon = "&#128736;";
                }
                pair = icon + " " + pair.substring(0, 12) + "\n" + pair.substring(11);

                if (pair.charAt(11) == '\n') pair = pair.replaceFirst("\n", " ");

                var index = pair.indexOf("Физическая культура и спорт");
                if (index != -1) pair = pair.substring(0, index + 1) + pair.substring(index + 1)
                        .replaceAll("Физическая культура и спорт.", "");
                index = pair.lastIndexOf('.');
                if (index != -1) pair = pair.substring(0, index + 1) + "\n&#127979;" + pair.substring(index + 1);
                index = pair.indexOf("&#128104;");
                if (index != -1) {
                    pair = pair.substring(0, index).toUpperCase() + pair.substring(index);
                } else {
                    pair = pair.toUpperCase();
                }

                sb.append(pair.replaceAll("\n\n", "\n").replaceAll("-", " - "))
                        .append("\n\n");
            }
        }
        if (emptyDay) {
            sb.append("В этот день пар нет.\n");
        } else {
            sb.replace(sb.length() - 1, sb.length(), "");
        }
    }
}
