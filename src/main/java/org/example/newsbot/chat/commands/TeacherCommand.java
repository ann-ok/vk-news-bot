package org.example.newsbot.chat.commands;

import com.vk.api.sdk.objects.messages.Message;
import org.example.newsbot.utils.Messenger;

public class TeacherCommand extends Command {

    private static String[] teachers = new String[]{
            "Ганебных Елена Викторовна",
            "Бушмелева Наталья Александровна",
            "Разова Елена Владимировна",
            "Шалагинова Надежда Владимировна",
            "Чупраков Павел Григорьевич",
            "Полевой Георгий Георгиевич",
            "Сысолятин Алексей Витальевич",
            "Пескишева Татьяна Анатольевна",
            "Лялин Андрей Васильевич",
            "Киселева Наталья Валерьевна",
            "Калабин Олег Владимирович",
            "Крестьянинова Ольга Викторовна",
            "Мешенина Наталья Васильевна",
            "Стрельникова Ирина Васильевна",
            "Шубина Марина Владимировна"
    };

    public TeacherCommand(String name) {
        super(name);
    }

    @Override
    public boolean check(String message) {
        for (var teacher : teachers) {
            var name = teacher.split(" ")[0].toLowerCase();
            if (name.equals(message.trim().toLowerCase())) return true;
        }
        return false;
    }

    @Override
    public void exec(Message message) {
        for (var name : teachers) {
            if (name.split(" ")[0].toLowerCase().equals(message.getText().trim().toLowerCase())) {
                Messenger.sendMessage(name, message.getFromId());
            }
        }
    }
}
