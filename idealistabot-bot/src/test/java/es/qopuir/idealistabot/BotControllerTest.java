package es.qopuir.idealistabot;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import es.qopuir.telegrambot.model.Message;
import es.qopuir.telegrambot.model.Update;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class BotControllerTest {

    private Update getUpdate(String commandText) {
        return getUpdate(commandText, null);
    }
    
    private Update getUpdate(String commandText, String args) {
        Update update = new Update();
        
        Message message = new Message();
        
        if (args != null) {
            message.setText(String.format("%s %s", commandText, args));
        } else {
            message.setText(commandText);
        }
        
        update.setMessage(message);
        
        return update;
    }
    
    @Test
    public void givenUpdateWithHelpCommandAndWithoutArgsWhenGetCommandThenIsWellParsed() {
        Update update = getUpdate("/help");
        
        BotController botController = new BotController();
        Command command = botController.getCommand(update);
        
        Assert.assertEquals(CommandType.HELP, command.getCommand());
        Assert.assertNull(command.getArgs());
    }
    
    @Test
    public void givenUpdateWithHelpCommandWithSpacesAndWithoutArgsWhenGetCommandThenIsWellParsed() {
        Update update = getUpdate("/help  ");
        
        BotController botController = new BotController();
        Command command = botController.getCommand(update);
        
        Assert.assertEquals(CommandType.HELP, command.getCommand());
        Assert.assertNull(command.getArgs());
    }
    
    @Test
    public void givenUpdateWithHelpCommandAndWithArgsWhenGetCommandThenIsWellParsed() {
        Update update = getUpdate("/help", "arg1 arg2");
        
        BotController botController = new BotController();
        Command command = botController.getCommand(update);
        
        Assert.assertEquals(CommandType.HELP, command.getCommand());
        Assert.assertEquals("arg1 arg2", command.getArgs());
    }
    
    @Test
    public void givenUpdateWithUnkownCommandWhenGetCommandThenUnkownCommandIsReturned() {
        Update update = getUpdate("/ajsdfasdf", "arg1 arg2");
        
        BotController botController = new BotController();
        Command command = botController.getCommand(update);
        
        Assert.assertEquals(CommandType.UNKNOWN, command.getCommand());
    }
}