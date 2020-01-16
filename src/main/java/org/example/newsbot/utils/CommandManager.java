package org.example.newsbot.utils;

import com.vk.api.sdk.objects.messages.Message;
import org.example.newsbot.chat.BeforeCommand;
import org.example.newsbot.chat.commands.*;

import java.util.Collection;
import java.util.HashSet;

public class CommandManager {
    private static HashSet<Command> commands = new HashSet<>();

    static {
        add(new UnknownCommand("unknown"));
        add(new GreetingCommand("hello"));
        add(new SubscribeCommand("subscribe"));
        add(new UnsubscribeCommand("unsubscribe"));
        add(new ListCommand("list"));
        add(new HelpCommand("help"));
    }

    public static void add(Command command) {
        commands.add(command);
    }

    /**
     * Обработка сообщений, получаемых через сервис ВКонтакте. Имеет ряд дополнительной информации.
     *
     * @param message сообщение (запрос) пользователя
     */
    static void execute(Message message) {
        getCommand(commands, message).exec(message);
    }

    static Command getCommand(Collection<Command> commands, Message message) {
        BeforeCommand.exec(message);
        String body = message.getBody();
        for (Command command : commands) {
            if (command.check(body)) return command;
        }
        return new UnknownCommand("unknown");
    }
}