package org.example.newsbot.servers;

import org.example.newsbot.App;
import org.example.newsbot.utils.Messenger;
import org.example.newsbot.utils.parsers.ParserSchedules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemindServer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(RemindServer.class);
    private static final int PAUSE_SECONDS = 60;

    private static void check() {
        var now = new Timestamp(System.currentTimeMillis());
        var df = new SimpleDateFormat("'2020'-MM-dd '12:00:00'");
        var scheduleDate = Timestamp.valueOf(df.format(now));
        var schedule = App.scheduleService.findByDate(scheduleDate).getSchedule();
        Pattern patternTime = Pattern.compile("\\d{2}:\\d{2} - \\d{2}:\\d{2}");
        Matcher matcherTime = patternTime.matcher(schedule);
        if (matcherTime.find()) {
            var time = matcherTime.group().split(" ")[0].split(":");
            var hours = Integer.parseInt(time[0]);
            var minutes = Integer.parseInt(time[1]);
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.HOUR_OF_DAY, hours);
            cal.set(Calendar.MINUTE, minutes);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            var lessonTime = new Timestamp(cal.getTimeInMillis());
            Calendar calNow = Calendar.getInstance();
            calNow.setTime(new Date());
            calNow.set(Calendar.SECOND, 0);
            calNow.set(Calendar.MILLISECOND, 0);
            for (var user : App.userService.findAllUsers()) {
                var nowTime = new Timestamp(calNow.getTimeInMillis() + TimeUnit.MINUTES.toMillis(user.getRemindInterval()));
                if (nowTime.equals(lessonTime)) {
                    Messenger.sendMessage("Пора на пары!", user.getId());
                }
                System.out.println(nowTime);
            }
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (true) {
            ParserSchedules.parse();
            try {
                Thread.sleep(1000 * PAUSE_SECONDS);
                check();
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            }
        }
    }
}
