package controllers;
import java.io.IOException;

import org.junit.Test;

import play.test.UnitTest;
import uk.org.catnip.eddie.FeedData;

public class RESTinatorTest  extends UnitTest {

    @Test
    public void testGetWithURL() throws IOException {
        String url = "lees2bytes.blogspot.com/feeds/posts/default";
        FeedData result = RESTinator.getFeedFromUrl(url);
        assertNotNull(result);
        assertTrue("At least one Entry should have been returned",result.entries().hasNext());
        result.entries().next();
    }
}
