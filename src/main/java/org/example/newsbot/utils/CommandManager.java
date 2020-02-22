package org.example.newsbot.utils;

import com.vk.api.sdk.objects.messages.Message;
import org.example.newsbot.chat.BeforeCommand;
import org.example.newsbot.chat.commands.*;

import java.util.HashSet;

public class CommandManager {
    private static final HashSet<Command> commands = new HashSet<>();

    static {
        add(new UnknownCommand("unknown"));
        add(new GreetingCommand("hello"));
        add(new SubscribeCommand("subscribe"));
        add(new UnsubscribeCommand("unsubscribe"));
        add(new ListCommand("list"));
        add(new HelpCommand("help"));
        add(new ScheduleCommand("schedule"));
        add(new TeacherCommand("teacher"));
        add(new RemindCommand("remind"));
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
        getCommand(message).exec(message);
    }

    static Command getCommand(Message message) {
        BeforeCommand.exec(message);
        String body = message.getText();
        for (Command command : CommandManager.commands) {
            if (command.check(body)) return command;
        }
        return new UnknownCommand("unknown");
    }
}