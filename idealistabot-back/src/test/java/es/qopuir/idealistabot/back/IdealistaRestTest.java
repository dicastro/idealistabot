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
        Assert.assertEquals(new Integer("33087169"), idealistaBuilding.getBuildingId());
        Assert.assertEquals("", idealistaBuilding.getTitle());
        Assert.assertNotNull(idealistaBuilding.getMainPhotoUrl());
    }
}