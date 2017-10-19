package medialibrary.cli;

/**
 * @author sna
 */

import medialibrary.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

  /**
   * Driver of the media library.
   *
   * @param args
   *          arguments passed to the program via the command line
   */
  public static void main(String[] args) {
    /*
     * The types are documentation, do not use the diamond operator here. It is
     * nice to know that the internals of the database is really a HashMap.
     */
    Database db = new Database(new HashMap<Media, Loanee>());
    if (!db.open("db.datsav")) {
      System.out.println("Failed to read media object...");
    }

    while (true) {
      Choice choice = getChoice();

      switch (choice) {
      case ADD_MEDIA:
        insert_new_media_item(db);
        break;
      case MARK_MEDIA_ON_LOAN:
        mark_an_item_as_on_loan(db);
        break;
      case MARK_MEDIA_AS_RETURNED:
        mark_an_item_as_returned(db);
        break;
      case LIST_ALL_MEDIA:
        list_the_items_currently_in_the_collection(db);
        break;
      case DELETE_MEDIA:
        remove_a_media_item(db);
        break;
      case QUIT:
        quit(db);
        break;
      default:
        System.out.println("You should never reach this.");
        System.exit(0);
        break;
      }

    }

  }

  static void promptOptions() {
    System.out.printf("(%d) %s\n", Choice.ADD_MEDIA.ordinal(), Choice.ADD_MEDIA);
    System.out.printf("(%d) %s\n", Choice.MARK_MEDIA_ON_LOAN.ordinal(), Choice.MARK_MEDIA_ON_LOAN);
    System.out.printf("(%d) %s\n", Choice.MARK_MEDIA_AS_RETURNED.ordinal(), Choice.MARK_MEDIA_AS_RETURNED);
    System.out.printf("(%d) %s\n", Choice.LIST_ALL_MEDIA.ordinal(), Choice.LIST_ALL_MEDIA);
    System.out.printf("(%d) %s\n", Choice.DELETE_MEDIA.ordinal(), Choice.DELETE_MEDIA);
    System.out.printf("(%d) %s\n", Choice.QUIT.ordinal(), Choice.QUIT);
    System.out.println();
  }

  static Choice getChoice() {
    @SuppressWarnings("resource")
    Scanner stdin = new Scanner(System.in);
    while (true) {
      promptOptions();
      System.out.print("Enter a integral that is inclusively between 0-5 (inclusive): ");
      int choice = -1;
      try {
        if (stdin.hasNext()) {
          choice = stdin.nextInt();
        }
        System.out.println("");
        return Choice.values()[choice];
      } catch (InputMismatchException e) {
        System.out.println("... invalid input please enter a INTEGRAL.");
        stdin.next();
        /* previous invalid input. */
      } catch (ArrayIndexOutOfBoundsException e) {
        System.out.println("... invalid input, the integral MUST between 0-5 (inclusive).");
      }
    }
  }

  /*
   * Insert a new media item. The user should be prompted for the new item’s
   * title and format (e.g. DVD, Blu-ray, Xbox, PlayStation, etc.) and this
   * information should be stored. If another item with the same title already
   * exists, the program should require the user to enter a different title.
   */
  static void insert_new_media_item(Database db) {
    @SuppressWarnings("resource")
    Scanner stdin = new Scanner(System.in);
    System.out.print("Enter media title: ");
    String title = null;
    if (stdin.hasNextLine()) {
      title = stdin.nextLine();
    }
    System.out.print("Enter media format: ");
    String format = null;
    if (stdin.hasNextLine()) {
      format = stdin.nextLine();
    }
    while (db.add(new Media(title, format), new Loanee()) == false) {
      System.out.println("Failed to add media, title already exists.");
      System.out.print("Enter a different media title: ");
      if (stdin.hasNextLine()) {
        title = stdin.nextLine();
      }
    }
  }

  // ---------------------------------------------------------------------------
  // END OF COMMAND-LINE INTERFACE METHODS
  // ---------------------------------------------------------------------------

  /*
   * Mark an item as on loan. The user should be prompted for the title of the
   * item to be loaned, the name of the person the item was loaned to and the
   * date it was loaned on and this information should be stored. Display an
   * error message if no item with that title exists or if that item is already
   * on loan.
   */
  static void mark_an_item_as_on_loan(Database db) {
    @SuppressWarnings("resource")
    Scanner stdin = new Scanner(System.in);
    System.out.print("Enter item's title: ");
    String title = null;
    if (stdin.hasNextLine()) {
      title = stdin.nextLine();
    }
    if (!db.containsMedia(title)) {
      System.out.println("Title does not exist...");
      return;
    }
    if (db.isMediaLoaned(title)) {
      System.out.println("Title has already been loaned...");
      return;
    }
    System.out.print("Enter the name of the loanee: ");
    String loaneeName;
    loaneeName = stdin.nextLine();
    while (true) {
      System.out.print("Enter the date of when the loan in the format of yyyy-MM-dd: ");
      String date;
      date = stdin.nextLine();
      try {
        Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        db.setMediaLoanee(title, new Loanee(loaneeName, parsedDate));
        return;
        /* no more work, terminate loop */
      } catch (ParseException e) {
        System.out.println("Please enter a valid format (ISO 8601), for example 1 January 1970 is ‎1970-01-01");
      }
    }
  }

  /*
   * Mark an item as returned. The user should be prompted for the title of the
   * item that was returned and that object should be updated to indicate that
   * it is no longer on loan. Display an error message if no item with that
   * title exists, or if that item is not currently on loan.
   */
  static void mark_an_item_as_returned(Database db) {
    @SuppressWarnings("resource")
    Scanner stdin = new Scanner(System.in);
    String title;
    System.out.print("Enter item's title: ");
    title = stdin.nextLine();
    boolean result = db.setMediaLoanee(title, new Loanee());
    if (result == false) {
      System.out.println("That item does not exist or is not on loan.");
    }
  }

  /*
   * List the items currently in the collection. Each item’s title and format
   * should be displayed. If the item is loaned out, then in parentheses after
   * this it should display who the item was loaned to and what date it was
   * loaned on. The items should be listed alphabetically by title
   */
  static void list_the_items_currently_in_the_collection(Database db) {
    int maxTitleLength = 6;
    int maxFormatLength = 6;
    int maxNameLength = 6;
    for (Map.Entry<Media, Loanee> item : db.getDatabase().entrySet()) {
      if (item.getKey().getTitle().length() > maxTitleLength) {
        maxTitleLength = item.getKey().getTitle().length();
      }
      if (item.getKey().getFormat().length() > maxFormatLength) {
        maxFormatLength = item.getKey().getFormat().length();
      }
      if (!item.getValue().isEmpty()) {
        if (item.getValue().getName().length() > maxNameLength) {
          maxNameLength = item.getValue().getName().length();
        }
      }
    }
    /* Due to Java 8's [effectively] final... */
    final int tlf = maxTitleLength;
    final int flf = maxFormatLength;
    final int nlf = maxNameLength;
    db.getSortedDatabase().forEach((key, value) -> {
      if (value.isEmpty()) {
        System.out.format("Media Title: %-" + tlf + "." + tlf + "s Media Format: %-" + flf + "." + flf + "s%n",
            key.getTitle(), key.getFormat());
      } else {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        System.out.format(
            "Media Title: %-" + tlf + "." + tlf + "s Media Format: %-" + flf + "." + flf + "s (Loanee: %-" + nlf + "."
                + nlf + "s Loaned on: %-10.10s)%n",
            key.getTitle(), key.getFormat(), value.getName(), df.format(value.getDateLoaned()));
      }
    });
    System.out.println();
    /* for formatting on the console. */
  }

  /*
   * Remove a media item . The user should be prompted for the item’s title and
   * the item with this title should be removed from the collection. Display an
   * error message if no item with that title exists
   */
  static void remove_a_media_item(Database db) {
    @SuppressWarnings("resource")
    Scanner stdin = new Scanner(System.in);
    String title = null;
    System.out.print("Enter item's title: ");
    if (stdin.hasNextLine()) {
      title = stdin.nextLine();
    }
    boolean result = db.remove(title);
    if (result == false) {
      System.out.println("Title does not exist...");
    }
  }

  /*
   * Quit. Before quitting, your program should store all of its data to a file.
   * Before your program starts up, it should check for this file and read it in
   * if it exists, so that the information is persistent between program
   * executions.
   */
  static void quit(Database db) {
    if (!db.save("db.datsav")) {
      System.out.println("Failed to save media object...");
    }
    System.exit(0);
  }

  // ---------------------------------------------------------------------------
  // START OF COMMAND-LINE INTERFACE METHODS
  // ---------------------------------------------------------------------------
  enum Choice {
    ADD_MEDIA("Insert a new media item."), MARK_MEDIA_ON_LOAN("Mark an item as on loan."), MARK_MEDIA_AS_RETURNED(
        "Mark an item as returned."), LIST_ALL_MEDIA(
            "List the items currently in the collection."), DELETE_MEDIA("Remove a media item."), QUIT("Quit.");

    private final String text;

    /**
     * Allows label to enum.
     *
     * @param text
     *          associated label for enum.
     */
    private Choice(final String text) {
      this.text = text;
    }

    @Override
    public String toString() {
      return text;
    }
  }

}
