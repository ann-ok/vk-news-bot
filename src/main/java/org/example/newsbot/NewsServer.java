package org.example.newsbot;

import org.example.newsbot.utils.NewsObserver;
import org.example.newsbot.utils.parsers.ParserNews;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NewsServer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(NewsServer.class);
    private static final int PAUSE_MINUTES = 10;

    @Override
    public void run() {
        var observer = new NewsObserver();

        while (true) {
            try {
                ParserNews.initiate();
                observer.exec();
                Thread.sleep(1000 * 60 * PAUSE_MINUTES);
            } catch (IOException | InterruptedException e) {
                LOG.error(e.getMessage());
            }
        }
    }
}
