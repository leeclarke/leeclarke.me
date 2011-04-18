package controllers;

import org.junit.Test;

import play.mvc.Http.Response;
import play.test.FunctionalTest;

/**
 * Perform a Functional test to ensure REST call works as expected.
 * @author leeclarke
 */
public class RESTinatorFunctionalTest extends FunctionalTest {
    
    static final String jsonPath = "/json/";

    //TODO figure out more RESTful way to call this, currently it only works with this type of call:  Maybe this is ok?
    //   http://localhost:9000/json/i?url=lees2bytes.blogspot.com/feeds/posts/default
    @Test
    public void testGetJSONFromXMLFeed() {
        
        String feedUrl = "lees2bytes.blogspot.com/feeds/posts/default";
        Response response = GET(jsonPath+feedUrl );
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset("utf-8", response);
        String jsonResp = getContent(response);
        assertTrue(jsonResp.startsWith("{"));
        assertTrue(jsonResp.contains("Blogger"));  //content check just to be sure there are matching expected values
        assertTrue(jsonResp.contains("entries"));  //TODO: should reverse parse the JSON object ot see if its good??

    }
}
