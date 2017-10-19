package medialibrary.gui;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public class ObservableEntry {

  StringProperty title = new SimpleStringProperty();
  StringProperty format = new SimpleStringProperty();
  StringProperty name = new SimpleStringProperty();
  StringProperty dateLoaned = new SimpleStringProperty();

  public static Callback<ObservableEntry, Observable[]> extractor() {
    return (ObservableEntry param) -> new Observable[] { param.title, param.format, param.name, param.dateLoaned };
  }

  @Override
  public String toString() {
    if (name.isEmpty().get() && dateLoaned.isEmpty().get() || name.isNull().get() && dateLoaned.isNull().get()) {
      return String.format("%s - %s", title.getValueSafe(), format.getValueSafe());
    }
    return String.format("%s - %s (%s on %s)", title.get(), format.getValueSafe(), name.getValueSafe(),
        dateLoaned.getValueSafe());
  }
}
