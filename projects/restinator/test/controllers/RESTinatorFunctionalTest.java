package controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import jobs.FetchUpdatedFeeds;

import org.junit.Before;
import org.junit.Test;

import play.cache.Cache;
import play.mvc.Http.Response;
import play.test.Fixtures;
import play.test.FunctionalTest;

/**
 * Perform a Functional test to ensure REST call works as expected.
 * @author leeclarke
 */
public class RESTinatorFunctionalTest extends FunctionalTest {
    
    static final String jsonPath = "/json/";
    
    @Before
    public void setUp() {
        Fixtures.deleteAll();
        Fixtures.load("data.yml");
    }
    
    //TODO figure out more RESTful way to call this, currently it only works with this type of call:  Maybe this is ok?
    //   http://localhost:9000/json/parse?url=lees2bytes.blogspot.com/feeds/posts/default
    @Test
    public void testGetJSONFromXMLFeed() throws UnsupportedEncodingException {
      //Make sure the cache is loaded.
        FetchUpdatedFeeds fetch = new FetchUpdatedFeeds();
        fetch.updateFeeds();
        
        String feedUrl = "lees2bytes.blogspot.com/feeds/posts/default";
        Response response = GET(jsonPath+ URLEncoder.encode(feedUrl,"UTF-8" ));
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset("utf-8", response);
        String jsonResp = getContent(response);
        assertTrue(jsonResp.startsWith("{"));
        assertTrue("Response== "+jsonResp,jsonResp.contains("Blogger"));  //content check just to be sure there are matching expected values
        assertTrue(jsonResp.contains("entries"));  //TODO: should reverse parse the JSON object ot see if its good??
    }
    
    /**
     * Test that the request returned is the Cached information and the service isn't retrieving the response needlessly.
     * To do this load up the Cache, change the Cache of one of the items and then make a request. The CacheValue should return and not the actual feed.
     * @throws IOException
     */
    @Test
    public void testGetWithURL_Cached() throws IOException {
        String url = "lees2bytes.blogspot.com/feeds/posts/default";
        String jsonExpected = "{\"test\":\"Just testing\"}";
        //Make sure the cache is loaded.
        FetchUpdatedFeeds fetch = new FetchUpdatedFeeds();
        fetch.updateFeeds();
        
        //Change Cache content for test
        HashMap<String, String> feedResults = Cache.get(FetchUpdatedFeeds.FEED_RESULTS_CACHE_KEY, HashMap.class);
        feedResults.put(url, jsonExpected);
        Cache.safeReplace(FetchUpdatedFeeds.FEED_RESULTS_CACHE_KEY, feedResults, "30mn");
        
        Response response = GET(jsonPath+ URLEncoder.encode(url,"UTF-8" ) );
        assertIsOk(response);
        assertContentType("application/json", response);
        String jsonResp = getContent(response);
        assertTrue("Actual Results="+jsonResp,jsonResp.contains(jsonExpected));
        
    }
}
