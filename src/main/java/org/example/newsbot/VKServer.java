package org.example.newsbot;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Message;
import org.example.newsbot.utils.Messenger;
import org.example.newsbot.utils.VKCore;

import java.util.concurrent.Executors;

public class VKServer implements Runnable {

    private VKCore vkCore;

    VKServer(VKCore vkCore) {
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
                App.LOG.error("Возникли проблемы: " + e.getMessage());
                final int RECONNECT_TIME = 10000;
                App.LOG.error("Повторное соединение через " + RECONNECT_TIME / 1000 + " секунд");
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
