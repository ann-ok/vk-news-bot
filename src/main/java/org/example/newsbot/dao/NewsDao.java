package org.example.newsbot.dao;

import org.example.newsbot.models.News;
import org.example.newsbot.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class NewsDao {
    public News findById(int id) {
        return HibernateSessionFactoryUtil.getSession().get(News.class, id);
    }

    public News findByHead(String head) {
        var session = HibernateSessionFactoryUtil.getSession();
        return (News) session.createQuery("FROM News WHERE head=:head")
                .setParameter("head", head)
                .uniqueResult();
    }

    public void save(News news) {
        Session session = HibernateSessionFactoryUtil.getSession();
        Transaction tx1 = session.beginTransaction();
        session.save(news);
        tx1.commit();
        session.close();
    }

    public void update(News news) {
        Session session = HibernateSessionFactoryUtil.getSession();
        Transaction tx1 = session.beginTransaction();
        session.update(news);
        tx1.commit();
        session.close();
    }

    public void delete(News news) {
        Session session = HibernateSessionFactoryUtil.getSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(news);
        tx1.commit();
        session.close();
    }

    @SuppressWarnings("unchecked")
    public List<News> findAll() {
        return (List<News>) HibernateSessionFactoryUtil.getSession()
                .createQuery("From News")
                .list();
    }
}
