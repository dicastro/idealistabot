package es.qopuir.idealistabot.back;

import java.net.URL;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import es.qopuir.idealistabot.back.model.IdealistaBuildingModel;

@Component
public class IdealistaRest {
    private static final Logger LOG = LoggerFactory.getLogger(IdealistaRest.class);
    
    @Autowired
    private ProxyProperties proxyProperties;

    //@Cacheable("idealistaBuildingHtmlRequest")
    public String getHtmlOfBuilding(String buildingId) {
        try {
            Connection connection = Jsoup.connect("http://www.idealista.com/inmueble/" + buildingId);
            
            if (proxyProperties.isEnabled()) {
                connection.proxy(proxyProperties.getHost(), proxyProperties.getPort());
            }
            
            Document doc = connection.get();
            
            String botHtml = "<div><h2 class=\"txt-medium txt-bold\">Telegram Bot</h2><ul><li><a href=\"https://telegram.me/idealistabot\">Iniciar chat</a></li></ul></div>";
            
            doc.select("#details").first().children().first().after(botHtml);
            
            return doc.toString();
        } catch (Exception e) {
            LOG.error("Problem retrieving building {}. Retourning empty page.", buildingId, e);
            
            return "<html><head></head><body><p>Lo lamentamos, pero no hemos encontrado el inmueble indicado.</p></body></html>";
        }
    }
    
    @Cacheable("idealistaBuildingModelRequest")
    public IdealistaBuildingModel findBuildingById(String buildingId) {
        if (null == buildingId) {
            return null;
        }

        try {
            Connection connection = Jsoup.connect("http://www.idealista.com/inmueble/" + buildingId);
            
            if (proxyProperties.isEnabled()) {
                connection.proxy(proxyProperties.getHost(), proxyProperties.getPort());
            }
            
            Document doc = connection.get();

            String title = doc.select("#main-info h1 span").text();

            String mainPhotoUrl = doc.select("meta[name='og:image'").attr("content");

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