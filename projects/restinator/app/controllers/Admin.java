package controllers;

import java.util.HashMap;

import jobs.FetchUpdatedFeeds;
import models.MonitoredFeed;
import play.Logger;
import play.cache.Cache;
import play.jobs.JobsPlugin;
import play.mvc.Controller;

public class Admin extends Controller {

    /**
     * List items contained in Cache
     */
    public static void listCachedItems() {
        HashMap<String, String> feedResults = Cache.get(FetchUpdatedFeeds.FEED_RESULTS_CACHE_KEY, HashMap.class);
        if(feedResults==null) {
            FetchUpdatedFeeds fetch = new FetchUpdatedFeeds();
            fetch.updateFeeds();
        }
        feedResults = Cache.get(FetchUpdatedFeeds.FEED_RESULTS_CACHE_KEY, HashMap.class);
        renderJSON(feedResults);
    }
    
    public static void seedfeeds() {
        Logger.info("Inserting Data %s", "");
        String resp = "";
        if(MonitoredFeed.all().count()<1) {
          Logger.info("NO Feed data found Inserting Data %s", "");
          MonitoredFeed mf = new MonitoredFeed("lees2bytes.blogspot.com/feeds/posts/default", "Lee Blog");
          mf.insert();
          MonitoredFeed mf2 = new MonitoredFeed("ws.audioscrobbler.com/2.0/user/lees2bytes/recenttracks.rss", "Lee Last FM");
          mf2.insert();
          resp = "{\"success\":\"Got them in there, have fun!\"}";
      } else {
          resp = "{\"error\":\"Looks like the seeds are planted dude!\"}";
          Logger.info("Looks like there is already data int here! %s", "");
      }
      renderJSON(resp);
    }
    
    public static void checkJobStatus() {
        JobsPlugin j = new JobsPlugin();
        
        renderText(j.getStatus());
    }
}
