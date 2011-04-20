/**
 * RESTinator , processes any atom/rss feed and returns JSON representation.
 *
 * Copyright (c) 2011 Lee Clarke
 *
 * LICENSE:
 *
 * This file is part of RESTinator (https://github.com/leeclarke/RESTinator).
 *
 * RESTinator is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * RESTinator is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with TheGardenDroid.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 */
package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

import play.mvc.Controller;
import uk.org.catnip.eddie.FeedData;
import uk.org.catnip.eddie.parser.Parser;

/**
 * REST implementation which is used by the external Droid interface to post
 * data. Also used by the web app to update the views with realtime data using
 * ajax.
 * 
 * @author leeclarke
 */
public class RESTinator extends Controller {

    private static final String LOG_MSG_FAILED_TO_CONVERT_REST_POST_JSON_INPUT = "Failed to convert REST Post. JSON input: ";
    private static final String STATUS_INVALID_INPUT = "{status:Invalid Input}";
    static final Logger logger = Logger.getLogger(RESTinator.class);
    
//DONE: Add Caching capability
//TODO: Add ability to maintain Feeds to be retrieved for cache 
//TODO: Build secured admin interface.    
//DONE: Add a JOB to retrieve the Feeds. 
//DONE: (Not much point doing this cuz it has to do a full retrieve every time.)Also a feed check that just sees if feed has been updated. 
    /**
     * REST/JSON services for saving data
     */
    public static void getJSONFromXML(String url) {
        String body = "";
        try {
            logger.debug("REST POSTED Sensor Data Temp");
            body = streamToString(request.body);
            FeedData feed = getFeedFromUrl(url);
            renderJSON(feed);
        } catch (Exception e) {
            logger.warn(LOG_MSG_FAILED_TO_CONVERT_REST_POST_JSON_INPUT + body);
            renderJSON(STATUS_INVALID_INPUT);
        }
    }
    
    /**
     * Retrieves a DO containing the parsed Feeds data.
     * @param url - feed url.
     * @return - DO of the feeds data.
     * @throws IOException - if the URL isn't any good, cough up a hairball. 
     */
    public static FeedData getFeedFromUrl(String url) throws IOException {
        Parser parser = new Parser();
        URL feedurl = new URL("http://lees2bytes.blogspot.com/feeds/posts/default");
        URLConnection testConn = feedurl.openConnection();
        
        FeedData feed = parser.parse(testConn.getInputStream());
        return feed;
    }

    /**
     * Processes request/POST body as input stream and returns a String object
     * containing posted data.
     * 
     * @param body
     * @return
     */
    static String streamToString(InputStream body) {
        BufferedReader in = new BufferedReader(new InputStreamReader(body));
        StringBuffer sb = new StringBuffer();
        String line;
        try {
            while ((line = in.readLine()) != null) { // while loop begins here
                sb.append(line);
            }
        } catch (IOException e1) {
            sb.append("Error reading input");
        }
        return sb.toString();
    }
}
