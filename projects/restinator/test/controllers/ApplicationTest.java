package controllers;
import java.io.IOException;
import java.net.URLConnection;

import org.junit.Test;
import org.xml.sax.SAXException;

import play.mvc.Http.Response;
import play.test.FunctionalTest;
import uk.org.catnip.eddie.FeedData;
import uk.org.catnip.eddie.parser.Parser;

public class ApplicationTest extends FunctionalTest {

    @Test
    public void testThatIndexPageWorks() {
        Response response = GET("/");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset("utf-8", response);
    }
    
    /**
     * Verify that the parser works.
     * @throws SAXException
     * @throws IOException
     */
    @Test
    public void testEdi() throws SAXException, IOException{
        Parser parser = new Parser();
        java.net.URL url = new java.net.URL("http://lees2bytes.blogspot.com/feeds/posts/default");
        URLConnection testConn = url.openConnection();
        
        FeedData feed = parser.parse(testConn.getInputStream());
 //       
        System.out.println(feed);
    }
}