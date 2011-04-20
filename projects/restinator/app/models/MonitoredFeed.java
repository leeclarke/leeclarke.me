package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * Simple DAO for a Feed which has been configured to be cached on a regular basis. 
 * @author leeclarke
 */
@Entity
public class MonitoredFeed extends Model {

    public Date created;
    public Date expires;
    public String url;
    public String feedName;
    public boolean suspended;
    
    /**
     * Empty constructor
     */
    public MonitoredFeed(){
        this.suspended = false;
        this.created = Calendar.getInstance().getTime();
    }
    
    /**
     * constructor
     * @param url - feed url
     * @param feedName - name of feed
     */
    public MonitoredFeed(String url, String feedName){
        this(url,feedName,null,true);
    }
    
    /**
     * constructor
     * @param url - feed url
     * @param feedName - name of feed
     * @param expires - dateTime should expire or null if want expired to equal now.
     * @param suspended - true if you don't want the feed to be retrieved.
     */
    public MonitoredFeed(String url, String feedName, Date expires, boolean suspended){
        Date now = Calendar.getInstance().getTime();
        this.expires = (expires == null)?now:expires;
        this.suspended = false;
        this.created = now;
        this.feedName = feedName;
        this.url = url;
    }
    
    /**
     * Retrieve Feeds that are not Disabled and their expires value has past now.
     * @return - Feeds ready for update.
     */
    public static List<MonitoredFeed> getUpdatableFeeds() {
        List params = new ArrayList<Object>();
        params.add(Calendar.getInstance().getTime());
        List<MonitoredFeed> feeds = MonitoredFeed.find("suspended = false AND expires <= ? order by expires desc", params.toArray()).fetch() ;
        return feeds;
    }
    
    /**
     * Retrieve Feeds that are not suspended
     * @return - all unsuspended feeds
     */
    public static List<MonitoredFeed> getActiveFeeds() {
        List<MonitoredFeed> feeds = MonitoredFeed.find("suspended = false order by expires desc").fetch() ;
        return feeds;        
    }

    @Override
    public String toString() {
        return "MonitoredFeed [created=" + this.created + ", expires=" + this.expires + ", url=" + this.url
                + ", feedName=" + this.feedName + ", suspended=" + this.suspended + "]";
    }
}
