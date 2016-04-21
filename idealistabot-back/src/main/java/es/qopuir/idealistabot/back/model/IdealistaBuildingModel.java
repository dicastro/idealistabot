package es.qopuir.idealistabot.back.model;

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IdealistaBuildingModel {
    private String buildingId;
    private String title;
    private URL mainPhotoUrl;

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public URL getMainPhotoUrl() {
        return mainPhotoUrl;
    }

    public void setMainPhotoUrl(URL mainPhotoUrl) {
        this.mainPhotoUrl = mainPhotoUrl;
    }
}