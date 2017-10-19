package medialibrary;

import java.util.HashMap;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class DatabaseTest {

  private Database instance;

  public DatabaseTest() {
  }

  /**
   * Sets up the test fixture. (Called before every test case method.)
   */
  @Before
  public void setUp() {
    instance = new Database(new HashMap<>());
    instance.add(new Media("duplicate", "test"), new Loanee());
  }

  /**
   * Tears down the test fixture. (Called after every test case method.)
   */
  @After
  public void tearDown() {
    instance = null;
  }

  /**
   * Test of add method, of class Database.
   */
  @Test
  public void testAdd() {
    System.out.println("add");
    Media item = new Media("title", "format");
    Loanee loanee = new Loanee();
    boolean expResult = true;
    boolean result = instance.add(item, loanee);
    /* Add to db. */
    assertEquals(expResult, result);
    /* Make sure duplicates fail to be added to db. */
    result = instance.add(new Media("duplicate", "test"), new Loanee());
    assertEquals(!expResult, result);
  }

  /**
   * Test of remove method, of class Database.
   */
  @Test
  public void testRemove() {
    System.out.println("remove");
    String title = "duplicate";
    boolean expResult = true;
    boolean result = instance.remove(title);
    assertEquals(expResult, result);
    /* test removing non-existent media */
    result = instance.remove("non-existent");
    assertEquals(!expResult, result);
  }

  @Test
  public void testSetMediaLoanee() {
    String title = "duplicate";
    Loanee loanee = new Loanee();
    boolean expResult = true;
    boolean result = instance.setMediaLoanee(title, loanee);
    assertEquals(expResult, result);
    result = instance.setMediaLoanee("DNE", loanee);
    assertEquals(!expResult, result);
    result = instance.setMediaLoanee("duplicate", null);
    assertEquals(!expResult, result);
  }
}
