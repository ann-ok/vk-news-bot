package org.example.newsbot.utils;

import org.example.newsbot.App;
import org.example.newsbot.models.News;
import org.example.newsbot.models.Tag;
import org.example.newsbot.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.concurrent.locks.ReentrantLock;

public class HibernateSessionFactoryUtil {
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
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());

            } catch (Exception e) {
                App.LOG.error("Исключение: " + e.getMessage());
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
            App.LOG.debug("Транзакция заверешна");
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            locker.unlock();
        }
        return session;
    }
}
