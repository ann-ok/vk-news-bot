package org.example.newsbot.chat.notifications;

import org.example.newsbot.App;
import org.example.newsbot.utils.Messenger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleNowNotification implements Notification {

    private Timestamp date;

    public ScheduleNowNotification(Timestamp date) {
        this.date = date;
    }

    @Override
    public void exec(int userId) {
        var user = App.userService.getUser(userId);
        if (user == null) return;

        var schedule = App.scheduleService.findByDate(date);
        if (schedule == null) {
            Messenger.sendMessage("Расписание не найдено.", userId);
            return;
        }

        var sb = new StringBuilder();
        getSchedule(sb, schedule.getSchedule());
        Messenger.sendMessage(sb.toString(), userId);
    }

    private void getSchedule(StringBuilder sb, String schedule) {
        Pattern patternTime = Pattern.compile("\\d{2}:\\d{2} - \\d{2}:\\d{2}");
        Matcher matcherTime = patternTime.matcher(schedule);
        Pattern patternCabinet = Pattern.compile("\\d{2} - \\d{3}");
        Matcher matcherCabinet = patternCabinet.matcher(schedule);
        var lessons = new ArrayList<Lesson>();
        while (matcherTime.find()) {
            var time = matcherTime.group();
            if (matcherCabinet.find(matcherTime.end())) {
                var rest = schedule.substring(matcherTime.end() + 1, matcherCabinet.end() + 1);
                lessons.add(new Lesson(time, rest));
            }
        }
        boolean exist = false;
        for (var lesson : lessons) {
            if (lesson.isAfter) {
                sb.append("\nСледующая пара начнётся в ")
                        .append(lesson.getTime())
                        .append(":")
                        .append(lesson.rest);
                return;
            } else if (lesson.isNow) {
                sb.append("Сейчас идёт пара:")
                        .append(lesson.rest);
                exist = true;
            }
        }
        if (!exist) {
            sb.append("На сегодня пар больше нет.");
        }
    }

    class Lesson {
        public boolean isNow;
        public boolean isAfter;
        String time;
        String rest;
        Timestamp begin;
        Timestamp end;

        Lesson(String time, String rest) {
            this.time = time;
            this.rest = rest;

            setTime();
            var now = new Timestamp(System.currentTimeMillis());
            isNow = (begin.before(now) || begin.equals(now))
                    && (end.after(now) || end.equals(now));
            isAfter = begin.after(now);
        }

        public String getTime() {
            var start = time.split(" - ")[0];
            var words = start.split(":");
            return Integer.parseInt(words[0]) + ":" + words[1];
        }

        private void setTime() {
            var intervals = time.split(" - ");

            for (int i = 0; i < 2; i++) {
                var words = intervals[i].split(":");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(words[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(words[1]));
                cal.set(Calendar.SECOND, 0);
                if (i == 0) begin = new Timestamp(cal.getTimeInMillis());
                else end = new Timestamp(cal.getTimeInMillis());
            }
        }
    }
}