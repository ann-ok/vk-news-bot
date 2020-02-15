package org.example.newsbot.chat.notifications;

import org.example.newsbot.App;
import org.example.newsbot.models.Schedule;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ScheduleNotification implements Notification {

    private Timestamp date;

    public ScheduleNotification(Timestamp date) {
        this.date = date;
    }

    @Override
    public void exec(int userId) {
        var user = App.userService.getUser(userId);
        if (user == null) return;
        var sb = new StringBuilder();
        if (date == null) getScheduleAll(sb);
        else getScheduleDay(sb);
        App.vkCore.sendMessage(sb.toString(), userId);
    }

    private void getScheduleDay(StringBuilder sb) {
        sb.append("Расписание на ").append(new SimpleDateFormat("dd.MM").format(date)).append(":\n");
        var schedule = App.scheduleService.findByDate(this.date);
        getDayClasses(sb, schedule);
    }

    private void getScheduleAll(StringBuilder sb) {
        var schedules = App.scheduleService.findAllSchedules();
        for (var schedule : schedules) {
            var sbDay = new StringBuilder();
            sbDay
                    .append(new SimpleDateFormat("EEEE").format(schedule.getDate()))
                    .append(" ")
                    .append(new SimpleDateFormat("dd.MM").format(schedule.getDate())).append(":\n");
            if (!getDayClasses(sbDay, schedule)) sb.append(sbDay).append("\n");
        }
    }

    private boolean getDayClasses(StringBuilder sb, Schedule schedule) {
        var pairs = schedule.getSchedule().split("\n \n");
        var emptyDay = true;
        for (var pair : pairs) {
            if (!pair
                    .substring(11)
                    .trim()
                    .replaceAll(" ", "")
                    .equals("")) {
                emptyDay = false;
                if (pair.charAt(11) == '\n') pair = pair.replaceFirst("\n", " ");
                sb.append("&#9200; ").append(pair).append('\n');
            }
        }
        if (emptyDay) {
            sb.append("В этот день пар нет.");
        }
        return emptyDay;
    }
}