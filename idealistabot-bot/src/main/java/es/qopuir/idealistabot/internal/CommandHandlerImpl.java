package es.qopuir.idealistabot.internal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import es.qopuir.idealistabot.Command;
import es.qopuir.idealistabot.CommandHandler;
import es.qopuir.idealistabot.CommandType;
import es.qopuir.idealistabot.Methods;
import es.qopuir.idealistabot.back.DmiRest;
import es.qopuir.idealistabot.back.IdealistaRest;
import es.qopuir.idealistabot.back.WeatherImageMode;
import es.qopuir.idealistabot.back.model.DmiCityModel;
import es.qopuir.idealistabot.back.model.IdealistaBuildingModel;
import es.qopuir.idealistabot.model.Chat;
import es.qopuir.idealistabot.repo.ChatRepository;
import es.qopuir.telegrambot.model.Update;

@Component
public class CommandHandlerImpl implements CommandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CommandHandlerImpl.class);

    @Autowired
    private Methods methods;

    @Autowired
    // TODO (dcastro): crear un servicio transaccional para acceder al repositorio
    private ChatRepository chatRepository;

    @Autowired
    private DmiRest dmiRest;

    @Autowired
    private IdealistaRest idealistaRest;

    @Override
    public void handleCommand(Update update, Command command) throws MalformedURLException, IOException {
        switch (command.getCommand()) {
            case HELP:
                sendIntroductionMessage(update);
                break;
            case START:
                if (!StringUtils.isEmpty(command.getArgs())) {
                    // TODO (dcastro): validate received buildingId
                    Chat chat = new Chat();
                    chat.setChatId(update.getMessage().getChat().getId());
                    chat.setBuildingId(command.getArgs().trim());

                    chatRepository.save(chat);
                }

                sendIntroductionMessage(update);

                break;
            case UNKNOWN:
                sendInformationMessage(update);
                break;
            default:
                handleIdealistaCommand(update, command);
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

    private void handleIdealistaCommand(Update update, Command command) throws MalformedURLException, IOException {
        switch (command.getCommand()) {
            case INMUEBLE:
                if (StringUtils.isEmpty(command.getArgs())) {
                    if (chatRepository.exists(update.getMessage().getChat().getId())) {
                        Chat chat = chatRepository.findOne(update.getMessage().getChat().getId());

                        IdealistaBuildingModel idealistaBuilding = idealistaRest.findBuildingById(chat.getBuildingId());

                        if (idealistaBuilding == null) {
                            LOG.debug("Idealista building {} not found", chat.getBuildingId());

                            methods.sendMessage(update.getMessage().getChat().getId(),
                                    "Lo lamentamos mucho, pero no hemos conseguido localizar ningun inmueble con el identificador facilitado ("
                                            + chat.getBuildingId() + ")" + System.lineSeparator() + "¿Esta seguro de que es correcto?");
                        } else {
                            LOG.debug("Idealista building {} found with (title, photo) -> ({}, {})", chat.getBuildingId(),
                                    idealistaBuilding.getTitle(), idealistaBuilding.getMainPhotoUrl().toString());

                            Path createTempFile = Files.createTempFile("", ".png", new FileAttribute[0]);
                            File file = createTempFile.toFile();

                            methods.sendPhoto(update.getMessage().getChat().getId(), idealistaBuilding.getMainPhotoUrl(), file,
                                    idealistaBuilding.getTitle());
                            methods.sendMessage(update.getMessage().getChat().getId(), "Para seleccionar otro inmueble, envie el comando:"
                                    + System.lineSeparator() + "/inmueble identificador - seleccionar un inmueble");

                            file.delete();
                        }
                    } else {
                        LOG.debug("Idealista building not selected yet");

                        methods.sendMessage(update.getMessage().getChat().getId(),
                                "Todavia no se ha seleccionado ningun inmueble." + System.lineSeparator()
                                        + "Para seleccionar un inmueble envie el comando:" + System.lineSeparator()
                                        + "/inmueble identificador - seleccionar un inmueble");
                    }
                } else {
                    LOG.debug("Idealista building {} selected", command.getArgs().trim());

                    // TODO (dcastro): validate received inmuebleId
                    Chat chat = new Chat();
                    chat.setChatId(update.getMessage().getChat().getId());
                    chat.setBuildingId(command.getArgs().trim());

                    chatRepository.save(chat);

                    methods.sendMessage(update.getMessage().getChat().getId(), "Inmueble modificado a " + command.getArgs().trim());
                }

                break;
            default:
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
}