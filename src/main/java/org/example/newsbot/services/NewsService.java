package org.example.newsbot.services;

import org.example.newsbot.dao.NewsDao;
import org.example.newsbot.models.News;

import java.util.List;

public class NewsService {
    private NewsDao newsDao = new NewsDao();

    public NewsService() {
    }

    public News getNews(int id) {
        return newsDao.findById(id);
    }

    public News findNews(String head) {
        return newsDao.findByHead(head);
    }

    public void saveNews(News news) {
        newsDao.save(news);
    }

    public void deleteNews(News news) {
        newsDao.delete(news);
    }

    public void updateNews(News news) {
        newsDao.update(news);
    }

    public List<News> findAllNews() {
        return newsDao.findAll();
    }
}
