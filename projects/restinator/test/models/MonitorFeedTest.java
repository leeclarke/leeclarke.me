package models;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.Logger;
import play.test.Fixtures;
import play.test.UnitTest;

public class MonitorFeedTest extends UnitTest {
    
    @Before
    public void setUp() {
        Fixtures.deleteAll();
        Fixtures.load("data.yml");
    }
    
    @Test
    public void createAndReturn() {
        String url = "www.hostname.com/feed";
        String feedName = "UnitTestFeed";
        new MonitoredFeed(url,feedName).save();
        
        MonitoredFeed result = MonitoredFeed.find("feedName = ?", feedName).first();
        assertNotNull(result);
        
        assertEquals(url, result.url);
        assertEquals(feedName, result.feedName);
        assertEquals(result.created, result.expires);
        assertEquals(false, result.suspended);
    }
    
    @Test
    public void testGetUpdatableFeeds() {
        
        List<MonitoredFeed> results = MonitoredFeed.getUpdatableFeeds();
        assertNotNull(results);
        Logger.debug("Results == %s", results.toString());
        assertTrue("Size not matching expected actual="+results.size(),results.size()==1);
        assertEquals("Test Feed 3", results.get(0).feedName);
    }
    
    @Test
    public void testGetActiveFeeds() {
        List<MonitoredFeed> results = MonitoredFeed.getActiveFeeds();
        assertNotNull(results);
        Logger.debug("Active Results == %s", results.toString());
        assertTrue("Result set should be all entries with suspend==false. results ="+results,results.size()>=3);
        
    }
    
}
