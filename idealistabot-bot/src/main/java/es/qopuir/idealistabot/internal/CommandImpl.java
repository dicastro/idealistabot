package es.qopuir.idealistabot.internal;

import es.qopuir.idealistabot.Command;

public class CommandImpl implements Command {
	private String command;
	private String args;

    public CommandImpl() {
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public String getArgs() {
		return args;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setArgs(String args) {
		this.args = args;
	}
}