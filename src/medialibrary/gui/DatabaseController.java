package medialibrary.gui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import medialibrary.Database;
import medialibrary.Loanee;
import medialibrary.Media;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DatabaseController {

  private Database database;
  private ObservableList<ObservableEntry> observableDatabase;

  public DatabaseController() {
    this.database = new Database(new HashMap<Media, Loanee>());
    this.observableDatabase = FXCollections.observableArrayList(ObservableEntry.extractor());
  }

  public DatabaseController(Database database) {
    this.database = database;
    this.observableDatabase = FXCollections.observableArrayList(ObservableEntry.extractor());
    this.observableDatabase.addListener((ListChangeListener<ObservableEntry>) (ev) -> {
      while (ev.next()) {
        database.save("db.datsav");
      }
    });
    database.getSortedDatabase().entrySet().forEach(item -> {
      ObservableEntry entry = new ObservableEntry();
      entry.title.set(item.getKey().getTitle());
      entry.format.set(item.getKey().getFormat());
      if (item.getValue().getName() != null && item.getValue().getDateLoaned() != null) {
        entry.name.set(item.getValue().getName());
        entry.dateLoaned.set(new SimpleDateFormat("yyyy-MM-dd").format(item.getValue().getDateLoaned()));
      }
      observableDatabase.add(entry);
    });
  }

  public Database getDatabase() {
    return database;
  }

  public void setDatabase(Database database) {
    this.database = database;
  }

  public ObservableList<ObservableEntry> getObservableDatabase() {
    return observableDatabase;
  }

  public void setObservableDatabase(ObservableList<ObservableEntry> observableDatabase) {
    this.observableDatabase = observableDatabase;
  }

  public boolean add(Media item, Loanee loanee) {
    if (database.add(item, loanee)) {
      ObservableEntry entry = new ObservableEntry();
      entry.title.set(item.getTitle());
      entry.format.set(item.getFormat());
      if ((observableDatabase.add(entry))) {
        refresh();
        return true;
      }
    }
    return false;
  }

  public Date getDate(String unparsedDate) throws ParseException {
      return new SimpleDateFormat("yyyy-MM-dd").parse(unparsedDate);
  }

  public void setSortMethod(Database.SortBy method) {
    database.setMethod(method);
    refresh();
  }

  public void refresh() {
    observableDatabase.clear();
    database.getSortedDatabase().entrySet().forEach(item -> {
      ObservableEntry entry = new ObservableEntry();
      entry.title.set(item.getKey().getTitle());
      entry.format.set(item.getKey().getFormat());
      if (item.getValue().getName() != null && item.getValue().getDateLoaned() != null) {
        entry.name.set(item.getValue().getName());
        entry.dateLoaned.set(new SimpleDateFormat("yyyy-MM-dd").format(item.getValue().getDateLoaned()));
      }
      observableDatabase.add(entry);
    });
  }

  public boolean remove(int index) {
    /* We could use observableDatabase.remove(index) but a problem occurs is that the observable is triggered
     * before the entry is removed from the database. Meaning if the user quits directly after a remove it would not be
     * remove. The method belows allows the item to be removed from the database first then the observable list just like
     * how all the other methods work.
     */
    ObservableEntry item = observableDatabase.get(index);
    if (database.remove(item.title.get())) {
      return observableDatabase.remove(item);
    } else {
      return false;
    }
  }

  public void setMediaLoanee(int index, Loanee loanee) throws Exception {
    if (index == -1) throw new Exception("You did not select a entry from the list view.");
    if (database.setMediaLoanee(observableDatabase.get(index).title.get(), loanee)) {
      if (loanee.isEmpty()) {
        observableDatabase.get(index).name.set(null);
        observableDatabase.get(index).dateLoaned.set(null);
      } else {
        observableDatabase.get(index).name.set(loanee.getName());
        observableDatabase.get(index).dateLoaned.set(new SimpleDateFormat("yyyy-MM-dd").format(loanee.getDateLoaned()));
      }
      refresh();
    } else {
      throw new Exception("The entry you selected is not compatible with operation you selected. (e.g. you attempted to loan an already loaned item)");
    }
    /**
     * Convert boolean -> Exception(), more useful for JavaFX.
     */
  }
}
