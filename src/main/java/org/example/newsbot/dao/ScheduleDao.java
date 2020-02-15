package org.example.newsbot.dao;

import org.example.newsbot.models.Schedule;
import org.example.newsbot.utils.HibernateSessionFactoryUtil;

import java.sql.Timestamp;
import java.util.List;

public class ScheduleDao {
    public Schedule findById(int id) {
        return HibernateSessionFactoryUtil
                .getSession()
                .get(Schedule.class, id);
    }

    public Schedule findByDate(Timestamp date) {
        var session = HibernateSessionFactoryUtil.getSession();
        return (Schedule) session.createQuery("FROM Schedule WHERE date=:date")
                .setParameter("date", date)
                .uniqueResult();
    }

    public void save(Schedule schedule) {
        if (findByDate(schedule.getDate()) != null) return;
        var session = HibernateSessionFactoryUtil.getSession();
        var transaction = session.beginTransaction();
        session.save(schedule);
        transaction.commit();
        session.close();
    }

    public void update(Schedule schedule) {
        var session = HibernateSessionFactoryUtil.getSession();
        var transaction = session.beginTransaction();
        session.update(schedule);
        transaction.commit();
        session.close();
    }

    public void delete(Schedule schedule) {
        var session = HibernateSessionFactoryUtil.getSession();
        var transaction = session.beginTransaction();
        session.delete(schedule);
        transaction.commit();
        session.close();
    }

    @SuppressWarnings("unchecked")
    public List<Schedule> findAll() {
        return (List<Schedule>) HibernateSessionFactoryUtil
                .getSession()
                .createQuery("from Schedule")
                .list();
    }

    public void hqlTruncate() {
        var session = HibernateSessionFactoryUtil.getSession();
        var transaction = session.beginTransaction();
        session.createQuery("delete from Schedule")
                .executeUpdate();
        transaction.commit();
        session.close();
    }
}
