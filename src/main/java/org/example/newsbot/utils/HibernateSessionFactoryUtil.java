package org.example.newsbot.utils;

import org.example.newsbot.models.News;
import org.example.newsbot.models.Schedule;
import org.example.newsbot.models.Tag;
import org.example.newsbot.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;

public class HibernateSessionFactoryUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateSessionFactoryUtil.class);
    private static SessionFactory sessionFactory;
    private static Session session;

    private HibernateSessionFactoryUtil() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(News.class);
                configuration.addAnnotatedClass(Tag.class);
                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(Schedule.class);
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
            } catch (Exception e) {
                LOG.error("Исключение: " + e.getMessage());
            }
        }
        return sessionFactory;
    }

    public static Session getSession() {
        if (session == null || !session.isOpen())
            session = getSessionFactory().openSession();

        var locker = new ReentrantLock();
        var condition = locker.newCondition();
        locker.lock();
        try {
            while (session.getTransaction().isActive())
                condition.await();
            LOG.debug("Транзакция заверешна");
            condition.signalAll();
        } catch (InterruptedException e) {
            LOG.error("Прервано ожидание сессии: ");
            LOG.error(e.getMessage());
        } finally {
            locker.unlock();
        }
        return session;
    }
}
