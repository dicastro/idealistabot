package es.qopuir.idealistabot.internal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.qopuir.idealistabot.Command;
import es.qopuir.idealistabot.CommandHandler;
import es.qopuir.idealistabot.CommandType;
import es.qopuir.idealistabot.Methods;
import es.qopuir.idealistabot.back.DmiCityModel;
import es.qopuir.idealistabot.back.DmiRest;
import es.qopuir.idealistabot.back.WeatherImageMode;
import es.qopuir.telegrambot.model.Update;

@Component
public class CommandHandlerImpl implements CommandHandler {
    @Autowired
    private Methods methods;

    @Autowired
    private DmiRest dmiRest;

    @Override
    public void handleCommand(Update update, Command command) throws MalformedURLException, IOException {
        switch (command.getCommand()) {
            case HELP:
            case START:
                sendIntroductionMessage(update);
                break;
            case UNKNOWN:
                sendInformationMessage(update);
                break;
            default:
                handleWeatherCommand(update, command);
                break;
        }
    }

    private void sendIntroductionMessage(Update update) {
        methods.sendMessage(update.getMessage().getChat().getId(), "You like the weather charts from the dmi.dk site?" + System.lineSeparator()
                + "This bot can show you the weather forecast graphs for your desired city." + System.lineSeparator()
                + "The following commands can be used:" + System.lineSeparator() + System.lineSeparator()
                + "/now cityname - showing the two day weather" + System.lineSeparator() + "/week cityname - showing furhter weather of the week"
                + System.lineSeparator() + System.lineSeparator() + "This bot project can be found at https://github.com/SimonScholz/telegram-bot");
    }

    private void sendInformationMessage(Update update) {
        methods.sendMessage(update.getMessage().getChat().getId(),
                "Command received (" + update.getMessage().getText() + ") is not well formatted." + System.lineSeparator()
                        + "We are sorry to not be able to process it." + System.lineSeparator() + "The following commands can be used:"
                        + System.lineSeparator() + System.lineSeparator() + "/now cityname - showing the two day weather" + System.lineSeparator()
                        + "/week cityname - showing furhter weather of the week" + System.lineSeparator() + System.lineSeparator()
                        + "This bot project can be found at https://github.com/SimonScholz/telegram-bot");
    }

    private void handleWeatherCommand(Update update, Command command) throws MalformedURLException, IOException {
        WeatherImageMode imageType = WeatherImageMode.NOW;

        if (command.getCommand() == CommandType.WEEK_WHEATHER) {
            imageType = WeatherImageMode.WEEK;
        }

        DmiCityModel cityModel = dmiRest.findCityId(command.getArgs());
        URL weatherImageURL = dmiRest.getWeatherImageURL(cityModel, imageType);

        if (null == weatherImageURL) {
            methods.sendMessage(update.getMessage().getChat().getId(), "Please use /now + cityname or /week + cityname");
            return;
        }

        Path createTempFile = Files.createTempFile("", ".png", new FileAttribute[0]);
        File file = createTempFile.toFile();

        methods.sendPhoto(update.getMessage().getChat().getId(), weatherImageURL, file, "DMI weather in " + cityModel.getLabel());

        file.delete();
    }
}