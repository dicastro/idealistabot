package es.qopuir.idealistabot;

import java.io.IOException;
import java.net.MalformedURLException;

import es.qopuir.telegrambot.model.Update;

public interface CommandHandler {
    void handleCommand(Update update, Command command) throws MalformedURLException, IOException;
}