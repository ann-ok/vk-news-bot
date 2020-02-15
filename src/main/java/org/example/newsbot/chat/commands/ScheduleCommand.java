package org.example.newsbot.chat.commands;

import com.vk.api.sdk.objects.messages.Message;
import org.example.newsbot.chat.notifications.ScheduleNotification;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleCommand extends Command {

    public ScheduleCommand(String name) {
        super(name);
    }

    @Override
    public boolean check(String message) {
        var acceptedWords = new String[]{
                "сегодня",
                "завтра",
                "послезавтра",
                "расписание"
        };
        for (var word : message.toLowerCase()
                .replaceAll("[,.]", "")
                .split(" ")) {
            for (var acceptedWord : acceptedWords)
                if (word.equals(acceptedWord)) return true;
        }
        return false;
    }

    @Override
    public void exec(Message message) {
        var msg = message.getBody().toLowerCase();
        var df = new SimpleDateFormat("'2020'-MM-dd '12:00:00'");
        String date = null;
        if (msg.contains("сегодня")) {
            date = df.format(new Timestamp(System.currentTimeMillis()));
        } else if (msg.contains("послезавтра")) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DATE, 2);
            date = df.format(c.getTime());
        } else if (msg.contains("завтра")) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DATE, 1);
            date = df.format(c.getTime());
        }
        if (date == null) {
            new ScheduleNotification(null).exec(message.getUserId());
        } else {
            new ScheduleNotification(Timestamp.valueOf(date)).exec(message.getUserId());
        }
    }
}
