package es.qopuir.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplyKeyboardMarkup {
    private String[][] keyboard;
    private boolean resizeKeyboard;
    private boolean oneTimeKeyboard;
    private boolean selective;

    public String[][] getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(String[][] keyboard) {
        this.keyboard = keyboard;
    }

    public boolean isResizeKeyboard() {
        return resizeKeyboard;
    }

    public void setResizeKeyboard(boolean resizeKeyboard) {
        this.resizeKeyboard = resizeKeyboard;
    }

    public boolean isOneTimeKeyboard() {
        return oneTimeKeyboard;
    }

    public void setOneTimeKeyboard(boolean oneTimeKeyboard) {
        this.oneTimeKeyboard = oneTimeKeyboard;
    }

    public boolean isSelective() {
        return selective;
    }

    public void setSelective(boolean selective) {
        this.selective = selective;
    }
}