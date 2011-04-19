package jobs;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import models.MonitoredFeed;
import play.cache.Cache;
import play.jobs.Every;
import play.jobs.Job;
import uk.org.catnip.eddie.FeedData;

import com.google.gson.Gson;

import controllers.RESTinator;

@Every("5m")
public class FetchUpdatedFeeds extends Job {

    @Override
    public void doJob() throws Exception {
       updateFeeds();
    }

    /**
     * Checks the fees for updates and caches results when needed
     */
    protected void updateFeeds() {
        //1. Check cache to see if empty and if so update all Caches.
        HashMap<String, String> feedResults = Cache.get("feedResults", HashMap.class);
        List<MonitoredFeed> feeds;
        if(feedResults == null) {
            feedResults = new HashMap<String, String>();
            feeds = MonitoredFeed.getActiveFeeds();
            
        } else {
            feeds = MonitoredFeed.getUpdatableFeeds();
            
        }
        
        Calendar expiresTime = Calendar.getInstance();
        expiresTime.add(Calendar.MINUTE, 4);
        for (MonitoredFeed monitoredFeed : feeds) {
            try {
                FeedData feed = RESTinator.getFeedFromUrl(monitoredFeed.url);
                String json = new Gson().toJson(feed);
                feedResults.put(monitoredFeed.url, json);
                monitoredFeed.expires = expiresTime.getTime();
                monitoredFeed.save();
            } catch (IOException e) {
                //if null in Cache, set error message. else leave it as is.
                feedResults.put(monitoredFeed.url, "{}");
            }
        }
        //Check MonitoredFeeds for expired feeds, not suspended.
        //  retrieve Expired Feed
        //  Parse feed into JSON
        //  Add JSON to Cache, key ==URL
        //  Update expired in MonitoredFeed for +4mins
        //      Update expired in MonitoredFeed for +4mins
    }

}

