package org.example.newsbot.servers;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Message;
import org.example.newsbot.utils.Messenger;
import org.example.newsbot.utils.VKCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

public class VKServer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(VKServer.class);
    private VKCore vkCore;

    public VKServer(VKCore vkCore) {
        this.vkCore = vkCore;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Пауза между запросами
                Thread.sleep(300);

                // Получаем ответ и если есть сообщение обрабатываем
                Message message = vkCore.getMessage();
                if (message != null) {
                    Executors.newCachedThreadPool().execute(new Messenger(message));
                }

            } catch (ClientException | InterruptedException | ApiException e) {
                LOG.error("Возникли проблемы: " + e.getMessage());
                final int RECONNECT_TIME = 10000;
                LOG.error("Повторное соединение через " + RECONNECT_TIME / 1000 + " секунд");
                try {
                    Thread.sleep(RECONNECT_TIME);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    return;
                }
            }
        }
    }
}
