package org.example.newsbot.services;

import org.example.newsbot.dao.ScheduleDao;
import org.example.newsbot.models.Schedule;

import java.sql.Timestamp;
import java.util.List;

public class ScheduleService {
    private final ScheduleDao scheduleDao = new ScheduleDao();

    public ScheduleService() {
    }

    public Schedule getSchedule(int id) {
        return scheduleDao.findById(id);
    }

    public Schedule findByDate(Timestamp date) {
        return scheduleDao.findByDate(date);
    }

    public void saveSchedule(Schedule schedule) {
        scheduleDao.save(schedule);
    }

    public void deleteSchedule(Schedule schedule) {
        scheduleDao.delete(schedule);
    }

    public void updateSchedule(Schedule schedule) {
        scheduleDao.update(schedule);
    }

    public List<Schedule> findAllSchedules() {
        return scheduleDao.findAll();
    }

    public void truncate() {
        scheduleDao.hqlTruncate();
    }
}
