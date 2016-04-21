package es.qopuir.idealistabot.back;

import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import es.qopuir.idealistabot.back.model.IdealistaBuildingModel;

@Component
public class IdealistaRest {
    private static final Logger LOG = LoggerFactory.getLogger(IdealistaRest.class);

    @Cacheable("idealistaBuildingModelRequest")
    public IdealistaBuildingModel findBuildingById(String buildingId) {
        if (null == buildingId) {
            return null;
        }

        try {
            Document doc = Jsoup.connect("http://www.idealista.com/inmueble/" + buildingId).get();

            String title = doc.select("#main-info h1 span").text();

            String mainPhotoUrl = doc.select("#main-multimedia div img").attr("src");

            IdealistaBuildingModel idealistaBuilding = new IdealistaBuildingModel();
            idealistaBuilding.setBuildingId(buildingId);
            idealistaBuilding.setTitle(title);
            idealistaBuilding.setMainPhotoUrl(new URL(mainPhotoUrl));

            return idealistaBuilding;
        } catch (Exception e) {
            LOG.error("Problem retrieving building {}", buildingId, e);
        }

        return null;
    }
}