package es.qopuir.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForceReply {
    private boolean forceReply;
    private boolean selective;

    public boolean isForceReply() {
        return forceReply;
    }

    public void setForceReply(boolean forceReply) {
        this.forceReply = forceReply;
    }

    public boolean isSelective() {
        return selective;
    }

    public void setSelective(boolean selective) {
        this.selective = selective;
    }
}