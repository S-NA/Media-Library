package medialibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Database {

  private Map<Media, Loanee> internalDatabase;
  private SortBy method;

  public Database(Map<Media, Loanee> internalDatabase) {
    this.internalDatabase = internalDatabase;
  }

  public SortBy getMethod() {
    return method;
  }

  public void setMethod(SortBy method) {
    this.method = method;
  }

  public Map<Media, Loanee> getDatabase() {
    return internalDatabase;
  }

  public void setDatabase(Map<Media, Loanee> internalDatabase) {
    this.internalDatabase = internalDatabase;
  }

  /**
   * Open database from disk, and read into memory.
   *
   * @param databaseFilename
   *          name of save file to load
   * @return true if it succeed, and false if it failed
   */
  @SuppressWarnings("unchecked")
  /* there is no reliable way to check this? */
  public boolean open(String databaseFilename) {
    File dbFile = new File(databaseFilename);
    if (dbFile.exists() && !dbFile.isDirectory()) {
      try (FileInputStream fis = new FileInputStream(databaseFilename)) {
        ObjectInputStream ois = new ObjectInputStream(fis);
        internalDatabase.putAll((HashMap<Media, Loanee>) ois.readObject());
        return true;
      } catch (ClassCastException | ClassNotFoundException | IOException e) {
        return false;
      }
    }
    return true;
    /* File may not exist. */
  }

  // ---------------------------------------------------------------------------
  // HELPER FUNCTIONS/METHODS BEGIN
  // ---------------------------------------------------------------------------

  /**
   * Save data in memory to disk.
   *
   * @param databaseFilename
   *          name of file to save to
   * @return true if it succeed, and false if it failed
   */
  public boolean save(String databaseFilename) {
    try (FileOutputStream fos = new FileOutputStream(databaseFilename)) {
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(internalDatabase);
      return true;
    } catch (Exception e) {
      System.err.println("Failed to save.");
      return false;
    }
  }

  /**
   * Takes string, and searches if it exists in internalDatabase.
   *
   * @param title
   *          to check if it exists in database
   * @return true if title exists in database, false if it does not
   */
  public boolean containsMedia(String title) {
    if (internalDatabase.isEmpty()) {
      return false;
      /* short circuit wasted work */
    }
    return internalDatabase.keySet().parallelStream().map(Media::getTitle).anyMatch(title::equals);
  }

  /**
   * Takes the title of a media, and checks to see if it is loaned.
   *
   * @param title
   *          media title to search for
   * @return true if it is loaned otherwise false
   */
  public boolean isMediaLoaned(String title) {
    return internalDatabase.entrySet().parallelStream().noneMatch((item) -> (item.getKey().getTitle().equals(title)
        && item.getValue().getName() == null && item.getValue().getDateLoaned() == null));
  }

  /**
   * Changes the loanee associated with the title. new Loanee() will make a
   * title available for loaning.
   *
   * @param title
   *          name of the media to have its loanee associated changed
   * @param loanee
   *          loanee record to associate with title
   * @return true if the change succeeds else false
   */
  public boolean setMediaLoanee(String title, Loanee loanee) {
    if (loanee == null) {
      return false;
    }
    
    /*
     * Avoid using containsMedia(String) due to it requiring a full loop over the data instead we can just check the current
     * item in the for loop.
     */
    for (Map.Entry<Media, Loanee> item : internalDatabase.entrySet()) {
      if (item.getKey().getTitle().equals(title) && item.getValue().isEmpty() && !loanee.isEmpty()) {
        item.setValue(loanee);
        return true;
      } else if ((item.getKey().getTitle().equals(title) && !item.getValue().isEmpty() && loanee.isEmpty())) {
        return item.getValue().clear();
      }
    }
    return false;
  }

  /**
   * Add a new media relationship. Duplicates will NOT be added. Only enforces
   * loanee will not be null. You can specify there is no loanee using an empty
   * constructor for loanee.
   *
   * @param item
   *          media to add to the database
   * @param loanee
   *          loanee of the media to add to add the database
   * @return true if the operation succeeds
   */
  public boolean add(Media item, Loanee loanee) {
    if (!containsMedia(item.getTitle()) && loanee != null) {
      internalDatabase.put(item, loanee);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Remove media from database.
   *
   * @param title
   *          name of the media to be removed
   * @return true if the operation succeeds
   */
  public boolean remove(String title) {
    for (Media item : getDatabase().keySet()) {
      if (item.getTitle().equals(title)) {
        getDatabase().remove(item);
        return true;
      }
    }
    return false;
  }

  /**
   * Returns a sorted representation of the database. Map T(Media, Loane) is the
   * abstract type holding a internal LinkedHashMap to preserve order.
   * <p>
   * Selection of what type of topological sort is done via a private member.
   *
   * @return a sorted Map object of the database
   */
  public Map<Media, Loanee> getSortedDatabase() {
    // (oV -> nV) (comparison by key) = sortedList (Java 8 Streams)
    switch ((method != null) ? method : SortBy.TITLE) {
    case DATE:
      return internalDatabase.entrySet().parallelStream().sorted((a, b) -> {
        int cmp = a.getValue().compareTo(b.getValue());
        return cmp != 0 ? cmp : a.getKey().compareTo(b.getKey());
      }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    case TITLE:
    default:
      return internalDatabase.entrySet().parallelStream().sorted(Map.Entry.comparingByKey()).collect(
          Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }
  }

  public enum SortBy {
    DATE, TITLE;
  }

  // --------------------------------------------------------------------------
  // HELPER FUNCTIONS/METHODS END
  // --------------------------------------------------------------------------
}