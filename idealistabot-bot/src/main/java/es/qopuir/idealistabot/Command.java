package es.qopuir.idealistabot;

public interface Command {
    CommandType getCommand();

    String getArgs();
}