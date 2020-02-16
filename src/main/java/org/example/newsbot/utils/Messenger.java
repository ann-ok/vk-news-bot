package org.example.newsbot.utils;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executors;

public class Messenger {

    private static final Logger LOG = LoggerFactory.getLogger(Messenger.class);
    private static final Random random = new Random();
    private static VkApiClient vkApiClient;
    private static GroupActor groupActor;
    private static String DEFAULT_KEYBOARD;
    private static int ts;
    private static int maxMsgId = -1;

    public Messenger() {

        keyboardInit();

        var prop = new Properties();
        try {
            prop.load(new FileInputStream("src/main/resources/app.properties"));
        } catch (IOException e) {
            LOG.error("Ошибка при загрузке конфигурации: " + e.getMessage());
        }

        var groupId = Integer.parseInt(prop.getProperty("groupId"));
        var accessToken = prop.getProperty("accessToken");
        groupActor = new GroupActor(groupId, accessToken);

        initializeClient();
    }

    private static List<Message> getMessages() throws ClientException, ApiException {
        var eventQuery = vkApiClient.messages()
                .getLongPollHistory(groupActor)
                .ts(ts);

        if (maxMsgId > 0) eventQuery.maxMsgId(maxMsgId);

        var messages = eventQuery.execute()
                .getMessages()
                .getItems();

        if (!messages.isEmpty()) {
            ts = vkApiClient.messages()
                    .getLongPollServer(groupActor)
                    .execute()
                    .getTs();

            messages.removeIf(x -> x.getFromId() < 0);
            messages.forEach(x -> maxMsgId = Math.max(maxMsgId, x.getId()));
        }

        return messages;
    }

    private static int getRandomId() {
        var millis = System.currentTimeMillis();
        var randomLong = random.nextLong();
        return (int) ((millis + randomLong) % Integer.MAX_VALUE);
    }

    public static void update() throws InterruptedException {
        List<Message> messages = null;
        final int MAX_RECONNECTS = 5;
        final int RECONNECT_TIME = 5;
        for (int i = 0; i < MAX_RECONNECTS; i++) {
            try {
                messages = getMessages();
                break;
            } catch (ClientException | ApiException e) {
                LOG.error("Ошибка при получении сообщений: " + e.getMessage());
                LOG.error("Повторное подключение к ВК через " + RECONNECT_TIME + " секунд...");
                Thread.sleep(RECONNECT_TIME * 1000);
                initializeClient();
            }
        }
        if (messages != null) {
            Executors.newCachedThreadPool()
                    .execute(new MessageHandler(messages));
        }
    }

    public static void sendMessage(String msg, int peerId, String inlineKeyboard) {
        if (msg == null) {
            LOG.error("msg == null");
            return;
        }
        try {
            while (msg.length() > 4096) {
                int i = 0;
                while (true) {
                    int j = msg.indexOf('\n', i + 1);
                    if (j > 4096 || j == -1) break;
                    i = j;
                }
                vkApiClient.messages()
                        .send(groupActor)
                        .peerId(peerId)
                        .randomId(getRandomId())
                        .message(msg.substring(0, i))
                        .unsafeParam("keyboard", DEFAULT_KEYBOARD)
                        .execute();
                msg = msg.substring(i + 1);
            }
            if (inlineKeyboard != null)
                vkApiClient.messages()
                        .send(groupActor)
                        .peerId(peerId)
                        .randomId(getRandomId())
                        .message(msg)
                        //.unsafeParam("keyboard", defaultKeyboard)
                        .unsafeParam("keyboard", inlineKeyboard)
                        .execute();
            else
                vkApiClient.messages()
                        .send(groupActor)
                        .peerId(peerId)
                        .randomId(getRandomId())
                        .message(msg)
                        .unsafeParam("keyboard", DEFAULT_KEYBOARD)
                        .execute();
        } catch (ApiException | ClientException e) {
            LOG.error("Ошибка при отправке сообщения: " + e.getMessage());
        }
    }

    public static void sendMessage(String msg, int peerId) {
        sendMessage(msg, peerId, null);
    }

    private static void initializeClient() {
        TransportClient transportClient = new HttpTransportClient();
        vkApiClient = new VkApiClient(transportClient);

        try {
            ts = vkApiClient.messages()
                    .getLongPollServer(groupActor)
                    .execute()
                    .getTs();
        } catch (ApiException | ClientException e) {
            LOG.error("Ошибка при обращении к ВК: " + e.getMessage());
        }
    }

    private void keyboardInit() {
        String keyboard = null;
        try {
            var fs = new FileInputStream("src/main/resources/keyboard.json");
            keyboard = new String(fs.readAllBytes());
        } catch (IOException e) {
            LOG.error("Не удалось прочитать конфигурацию клавиатуры: ");
            LOG.error(e.getMessage());
        }
        if (keyboard == null) DEFAULT_KEYBOARD = "{\"buttons\":[], \"one_time\":true}";
        else DEFAULT_KEYBOARD = keyboard;
    }
}