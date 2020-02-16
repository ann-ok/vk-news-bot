package org.example.newsbot.servers;

import org.example.newsbot.utils.Messenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VkServer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(VkServer.class);

    public VkServer() {
        new Messenger();
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Пауза между запросами
                Thread.sleep(300);
                Messenger.update();

            } catch (InterruptedException e) {
                LOG.error("Возникли проблемы: " + e.getMessage());
                final int RECONNECT_TIME = 10;
                LOG.error("Повторное соединение через " + RECONNECT_TIME + " секунд...");
                try {
                    Thread.sleep(RECONNECT_TIME * 1000);
                } catch (InterruptedException ex) {
                    LOG.error("Совсем всё сломалось :`(");
                    LOG.error(ex.getMessage());
                    return;
                }
            }
        }
    }
}
