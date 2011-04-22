package jobs;

import java.util.Calendar;
import java.util.HashMap;

import models.MonitoredFeed;

import org.junit.Before;
import org.junit.Test;

import play.Logger;
import play.cache.Cache;
import play.modules.siena.SienaFixture;
import play.test.UnitTest;

/**
 * Verifies that the Job will perform as expected.
 * @author leeclarke
 */
public class FetchUpdatedFeedsTest extends UnitTest {
    
    @Before
    public void setUp() throws Exception {
        SienaFixture.deleteAll();
        SienaFixture.load("data.yml");
    }
    
    @Test
    public void testUpdateFeeds() {
        
        FetchUpdatedFeeds fetchJob = new FetchUpdatedFeeds();
        fetchJob.updateFeeds();
        // Retrieve Cache to see is it worked.
        HashMap<String, String> feedResults = Cache.get(FetchUpdatedFeeds.FEED_RESULTS_CACHE_KEY, HashMap.class);
        
        assertNotNull(feedResults);
        //make sure default items have been added
        Logger.debug("Cache map contains the following: %s", feedResults.keySet());
        
        assertTrue("Missing Cache for given url key not found.", feedResults.containsKey("ws.audioscrobbler.com/2.0/user/lees2bytes/recenttracks.rss"));
        assertTrue("Missing Cache for given url key not found.", feedResults.containsKey("lees2bytes.blogspot.com/feeds/posts/default"));
        
    }
    
    
    /**
     * Need to test other code path which is updating an item in the Cache which has expired.
     */
    public void testUpdateFeeds_UpdateExpiredCacheItem() {
        Logger.info("ENTER TEST", "testUpdateFeeds_UpdateExpiredCacheItem");
        String targetURL = "ws.audioscrobbler.com/2.0/user/lees2bytes/recenttracks.rss";
        FetchUpdatedFeeds fetchJob = new FetchUpdatedFeeds();
        //Get an item from Cache
        
        HashMap<String, String> feedResults = Cache.get(FetchUpdatedFeeds.FEED_RESULTS_CACHE_KEY, HashMap.class);
        if(!feedResults.containsKey(targetURL)){
            Logger.debug("Needed to load Cache %s", "");
            fetchJob.updateFeeds();
        }
        
      //make it expired. 
        MonitoredFeed feed = MonitoredFeed.all().filter("url", targetURL).get();
        Calendar expDate = Calendar.getInstance();
        expDate.add(Calendar.HOUR, -12);
        feed.expires = expDate.getTime();
        feed.insert();
        
        //Clear Cache just for testing that way it will be null if the update failed.
        feedResults.put(targetURL, null);
        Cache.replace(FetchUpdatedFeeds.FEED_RESULTS_CACHE_KEY, feedResults);
        HashMap<String, String> feedResultsReplace = Cache.get(FetchUpdatedFeeds.FEED_RESULTS_CACHE_KEY, HashMap.class);
        String jsonVal = feedResultsReplace.get(targetURL);
        assertNull("Cache update didn't take, value not set to null",jsonVal);
        
        //Then call UpdateFeeds which should update the expired feed.
        fetchJob.updateFeeds();
        
        //Check for Cache Value
        HashMap<String, String> feedResultsUpdated = Cache.get(FetchUpdatedFeeds.FEED_RESULTS_CACHE_KEY, HashMap.class);
        assertNotNull(feedResultsUpdated);
        assertTrue(feedResultsUpdated.containsKey(targetURL));
        assertNotNull(feedResultsUpdated.get(targetURL));
        assertTrue(feedResultsUpdated.get(targetURL).contains("lees2bytes"));
    }
}
