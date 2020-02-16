package org.example.newsbot.servers;

import org.example.newsbot.utils.parsers.ParserSchedules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleServer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleServer.class);
    private static final int PAUSE_DAYS = 1;

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (true) {
            ParserSchedules.parse();
            try {
                Thread.sleep(1000 * 60 * 60 * 24 * PAUSE_DAYS);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            }
        }
    }
}
