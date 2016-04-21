package es.qopuir.idealistabot.back;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import es.qopuir.idealistabot.back.model.IdealistaBuildingModel;

@RunWith(BlockJUnit4ClassRunner.class)
public class IdealistaRestTest {
    @Test
    public void givenXWhenYThenZ() {
        IdealistaRest idealistaRest = new IdealistaRest();
        
        IdealistaBuildingModel idealistaBuilding = idealistaRest.findBuildingById("33087169");
        
        Assert.assertNotNull(idealistaBuilding);
        Assert.assertEquals("33087169", idealistaBuilding.getBuildingId());
        Assert.assertEquals("Piso en venta en calle de barrilero, 5, Adelfas, Madrid", idealistaBuilding.getTitle());
        Assert.assertNotNull(idealistaBuilding.getMainPhotoUrl());
        Assert.assertEquals("https://img3.idealista.com/thumbs?wi=1500&he=0&en=%2BtSLyO%2BcnvWFQ1vfQ1%2FQRLsNpVv6ia3a7nEHrCJxiZE%2FnmJAb6fgqzMbkG9EXYnkcaTcDzYm1RQ5nSzQSnUA5xWT8ctid8xY3jdvWiruy7q%2BPzVNQtasL6ov1%2FFQx7uiAv%2FseCUeJWntYidx4vgb5sJLhSvR8a8b98urfy0NMAgpmKQMiKEvtYrIRgmKBwFm&ch=1487807127", idealistaBuilding.getMainPhotoUrl().toString());
    }
}