package org.example.newsbot.chat.notifications;

import org.example.newsbot.App;
import org.example.newsbot.models.Schedule;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Comparator;

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
        sb.append("&#128203; ")
                .append(getWeekDay(this.date))
                .append(" ")
                .append(new SimpleDateFormat("dd.MM").format(date))
                .append("\n------------------------------------------\n");
        var schedule = App.scheduleService.findByDate(this.date);
        sb.append(schedule.getSchedule());
        sb.append("------------------------------------------\n");
    }

    private void getScheduleAll(StringBuilder sb) {
        var allSchedules = App.scheduleService.findAllSchedules();
        allSchedules.sort(Comparator.comparing(Schedule::getDate));
        var schedules = allSchedules.subList(allSchedules.size() - 14, allSchedules.size());
        for (var schedule : schedules) {
            var sbDay = new StringBuilder();
            sbDay.append("\n&#128203; ")
                    .append(getWeekDay(schedule.getDate()))
                    .append(" ")
                    .append(new SimpleDateFormat("dd.MM").format(schedule.getDate()))
                    .append("\n------------------------------------------\n");
            var data = schedule.getSchedule();
            if (data.contains("В этот день пар нет.") || data.contains("УЧЕБНАЯ ПРАКТИКА")) continue;
            sbDay.append(data);
            sb.append(sbDay).append("------------------------------------------\n");
        }
    }

    private String getWeekDay(Timestamp date) {
        var dayName = new SimpleDateFormat("EEEE").format(date);
        switch (dayName) {
            case "Monday":
                return "Понедельник";
            case "Tuesday":
                return "Вторник";
            case "Wednesday":
                return "Среда";
            case "Thursday":
                return "Четверг";
            case "Friday":
                return "Пятница";
            case "Saturday":
                return "Суббота";
            case "Sunday":
                return "Воскресенье";
            default:
                return dayName;
        }
    }
}