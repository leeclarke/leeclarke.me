package jobs;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class FetchUpdatedFeedsTest extends UnitTest {
    
    @Before
    public void setUp() {
        Fixtures.deleteAll();
        Fixtures.load("data.yml");
    }
    
    @Test
    public void createAndReturn() {
        
        FetchUpdatedFeeds fetchJob = new FetchUpdatedFeeds();
        //TODO: This causes some Hibernate error.        
        fetchJob.updateFeeds();
        // Retrieve Cache to see is it worked.
        
    }
    
    
}
