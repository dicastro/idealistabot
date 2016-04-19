package es.qopuir.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplyKeyboardHide {
    private boolean hideKeyboard;
    private boolean selective;

    public boolean isHideKeyboard() {
        return hideKeyboard;
    }

    public void setHideKeyboard(boolean hideKeyboard) {
        this.hideKeyboard = hideKeyboard;
    }

    public boolean isSelective() {
        return selective;
    }

    public void setSelective(boolean selective) {
        this.selective = selective;
    }
}