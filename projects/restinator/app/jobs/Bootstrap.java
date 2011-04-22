package jobs;

import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

/**
 * Ensures the Cache is populated when server starts up
 * @author leeclarke
 */
@OnApplicationStart
public class Bootstrap extends Job {
    @Override
    public void doJob() throws Exception {
        Logger.warn("Bootstrap Loading Cache %s", "");
        
        FetchUpdatedFeeds fetch = new FetchUpdatedFeeds();
        fetch.updateFeeds();
    }
}
