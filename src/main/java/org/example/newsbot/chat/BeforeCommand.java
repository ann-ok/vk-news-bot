package org.example.newsbot.chat;

import com.vk.api.sdk.objects.messages.Message;
import org.example.newsbot.App;
import org.example.newsbot.models.User;

public class BeforeCommand {
    public static void exec(Message message) {
        var user = App.userService.getUser(message.getUserId());

        if (user == null) {
            var newUser = new User(message.getUserId());
            App.userService.saveUser(newUser);
        }
    }
}
