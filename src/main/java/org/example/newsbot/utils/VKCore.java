package org.example.newsbot.utils;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import org.example.newsbot.App;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class VKCore {

    private static final String defaultKeyboard;
    private static int ts;
    private static int maxMsgId = -1;

    static {
        String keyboard = null;
        try {
            var fs = new FileInputStream("src/main/resources/keyboard.json");
            keyboard = new String(fs.readAllBytes());
        } catch (IOException e) {
            App.LOG.error("Не удалось прочитать конфигурацию клавиатуры");
            e.printStackTrace();
        }
        if (keyboard == null) defaultKeyboard = "{\"buttons\":[], \"one_time\":true}";
        else defaultKeyboard = keyboard;
    }

    private VkApiClient vk;
    private GroupActor actor;

    public VKCore() throws ClientException, ApiException {
        // Инициализация клиента для работы с вк
        TransportClient transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);

        // Загрузка конфигураций
        Properties prop = new Properties();
        int groupId;
        String accessToken;
        try {
            prop.load(new FileInputStream("src/main/resources/app.properties"));
            groupId = Integer.parseInt(prop.getProperty("vk.groupId"));
            accessToken = prop.getProperty("vk.accessToken");
            actor = new GroupActor(groupId, accessToken);
            ts = vk.messages()
                    .getLongPollServer(actor)
                    .execute()
                    .getTs();
        } catch (IOException e) {
            e.printStackTrace();
            App.LOG.error("Ошибка при загрузке файла конфигурации");
        }
    }

    public GroupActor getActor() {
        return actor;
    }

    public VkApiClient getVk() {
        return vk;
    }

    public Message getMessage() throws ClientException, ApiException {

        MessagesGetLongPollHistoryQuery eventsQuery = vk.messages()
                .getLongPollHistory(actor)
                .ts(ts);
        if (maxMsgId > 0) {
            eventsQuery.maxMsgId(maxMsgId);
        }
        List<Message> messages = eventsQuery.execute()
                .getMessages()
                .getMessages();

        if (!messages.isEmpty()) {
            try {
                ts = vk.messages()
                        .getLongPollServer(actor)
                        .execute()
                        .getTs();
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }

        if (!messages.isEmpty() && !messages.get(0).isOut()) {
            /*
             *   messageId - максимально полученный ID, нужен, чтобы не было ошибки 10 internal server error,
             *   который является ограничением в API VK. В случае, если ts слишком старый (больше суток),
             *   а max_msg_id не передан, метод может вернуть ошибку 10 (Internal server error).
             */
            int msgId = messages.get(0).getId();
            if (msgId > maxMsgId) {
                maxMsgId = msgId;
            }
            return messages.get(0);
        }
        return null;
    }

    public void sendMessage(String msg, int peerId) {
        sendMessage(msg, peerId, null);
    }

    public void sendMessage(String msg, int peerId, String inlineKeyboard) {
        if (msg == null) {
            App.LOG.error("msg == null");
            return;
        }
        try {
            App.LOG.info("Отправка сообщения пользователю " + peerId);
            while (msg.length() > 4096) {
                int i = 0;
                while (true) {
                    int j = msg.indexOf('\n', i + 1);
                    if (j > 4096 || j == -1) break;
                    i = j;
                }
                vk.messages()
                        .send(actor)
                        .peerId(peerId)
                        .message(msg.substring(0, i))
                        .unsafeParam("keyboard", defaultKeyboard)
                        .execute();
                msg = msg.substring(i + 1);
            }
            if (inlineKeyboard != null)
                vk.messages()
                        .send(actor)
                        .peerId(peerId)
                        .message(msg)
                        //.unsafeParam("keyboard", defaultKeyboard)
                        .unsafeParam("keyboard", inlineKeyboard)
                        .execute();
            else
                vk.messages()
                        .send(actor)
                        .peerId(peerId)
                        .message(msg)
                        .unsafeParam("keyboard", defaultKeyboard)
                        .execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }
}