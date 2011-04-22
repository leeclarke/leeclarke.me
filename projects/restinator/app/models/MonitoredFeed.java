package models;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import siena.Id;
import siena.Model;
import siena.Query;


/**
 * Simple DAO for a Feed which has been configured to be cached on a regular basis. 
 * @author leeclarke
 */
public class MonitoredFeed extends Model {

    @Id
    public Long id;
    
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
        return  Model.all(MonitoredFeed.class).filter("suspended", false).filter("expires<=", Calendar.getInstance().getTime()).fetch();
     }
    
    /**
     * Retrieve Feeds that are not suspended
     * @return - all unsuspended feeds
     */
    public static List<MonitoredFeed> getActiveFeeds() {
        return  Model.all(MonitoredFeed.class).filter("suspended", false).fetch();
    }

    public static Query<MonitoredFeed> all() {
        return  Model.all(MonitoredFeed.class);
    }
    
    @Override
    public String toString() {
        return "MonitoredFeed [created=" + this.created + ", expires=" + this.expires + ", url=" + this.url
                + ", feedName=" + this.feedName + ", suspended=" + this.suspended + "]";
    }
}
