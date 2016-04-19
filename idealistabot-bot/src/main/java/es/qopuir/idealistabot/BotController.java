package es.qopuir.idealistabot;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.qopuir.idealistabot.internal.CommandImpl;
import es.qopuir.telegrambot.model.Message;
import es.qopuir.telegrambot.model.Update;
import es.qopuir.telegrambot.model.response.UpdateResponse;

@RestController
public class BotController {
    @Autowired
    private CommandHandler commandHandler;
    
    @Autowired
    private Methods methods;

    @RequestMapping("/ping")
    public void test() throws IOException {
        System.out.println("ping ok");
    }
    
    @RequestMapping("/updates")
    public Update[] getUpdates() throws IOException {
        UpdateResponse updateResponse = methods.getUpdates();
        if (updateResponse.isOk()) {
            System.out.println("updates: " + updateResponse.getResult().length);
            return updateResponse.getResult();
        } else {
            System.out.println("updates ko");
            return new Update[0];
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/idealistabot")
    public void idealistabotRequest(@RequestBody Update update) throws IOException {
        Command command = getCommand(update);
        commandHandler.handleCommand(update, command);
    }

    private Command getCommand(Update update) {
        Message message = update.getMessage();
        
        String text = message.getText();
        
        CommandImpl command = new CommandImpl();
        
        int commandIndex = text.indexOf(" ");
        
        if (text.length() > commandIndex) {
            command.setCommand(text.substring(0, commandIndex));
            command.setArgs(text.substring(commandIndex + 1));
        } else {
            command.setCommand(text.trim());
        }

        return command;
    }

    @RequestMapping("/sampleKeyboard")
    public void sampleKeyboard() {
        //
        // UpdateResponse updates = methods.getUpdates();
        //
        // System.out.println(updates);
        //
        // String[][] buttons = new String[3][1];
        // buttons[0][0] = "Eins";
        // buttons[1][0] = "Zwei";
        // buttons[2][0] = "Drei";
        //
        // ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        // replyKeyboardMarkup.setKeyboard(buttons);
        //
        // MultiValueMap<String, Object> vars = new LinkedMultiValueMap<String,
        // Object>();
        // vars.add("chat_id", 3130440);
        // vars.add("text", "Hallo vom Spring BotController");
        // vars.add("reply_markup", buttons);

        // methods.sendMessage(3130440, "Hallo vom Spring BotController", false,
        // 0, replyKeyboardMarkup);
    }
}