package medialibrary.gui;

import medialibrary.Loanee;
import medialibrary.Media;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sna
 */
public class DatabaseControllerTest {

    private DatabaseController instance;

    public DatabaseControllerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new DatabaseController();
        instance.add(new Media("duplicate", "test"), new Loanee());
    }

    @After
    public void tearDown() {
        instance = null;
    }

    /**
     * Test of add method, of class DatabaseController.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        Media item = new Media("duplicate", "test");
        Loanee loanee = new Loanee();
        boolean expResult = false;
        boolean result = instance.add(item, loanee);
        assertEquals(expResult, result);
        
        item = new Media("nonduplicate", "test");
        expResult = true;
        result = instance.add(item, loanee);
        assertEquals(expResult, result);
        
    }
}
