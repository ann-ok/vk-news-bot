package org.example.newsbot;

import org.example.newsbot.utils.NewsObserver;
import org.example.newsbot.utils.Parser;

import java.io.IOException;

public class NewsServer implements Runnable {

    private static final int PAUSE_MINUTES = 10;

    @Override
    public void run() {
        var observer = new NewsObserver();

        while (true) {
            try {
                Parser.initiate();
                observer.exec();
                Thread.sleep(1000 * 60 * PAUSE_MINUTES);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
