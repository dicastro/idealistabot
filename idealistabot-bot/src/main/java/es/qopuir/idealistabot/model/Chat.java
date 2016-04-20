package es.qopuir.idealistabot.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Chat {
    @Id
    private Integer chatId;
    private String buildingId;

    public Chat() {
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }
}