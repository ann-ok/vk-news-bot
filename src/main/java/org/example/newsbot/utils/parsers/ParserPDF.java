package org.example.newsbot.utils.parsers;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import org.example.newsbot.App;
import org.example.newsbot.models.Schedule;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class ParserPDF {
    public static void parse(String pdf) throws IOException {
        PdfReader reader = new PdfReader(pdf);
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
            App.scheduleService.saveSchedule(new Schedule(date, groupName, day));
        }
    }

    private static Timestamp getDateFromHead(String head) {
        var nums = head.split(" ")[1].split("\\.");
        return Timestamp.valueOf(String.format("20%s-%s-%s 12:00:00", nums[2], nums[1], nums[0]));
    }
}
