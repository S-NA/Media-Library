package medialibrary.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import medialibrary.*;

import java.util.Date;
import java.util.HashMap;

/**
 * @author sna
 */
public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  static GridPane createCreationControl(DatabaseController dbc, ListView<ObservableEntry> lvMediaItems) {
    GridPane gpMedia = new GridPane();
    gpMedia.getStyleClass().add("nested-grid");
    Label lblTitle = new Label("Title:");
    TextField txfTitle = new TextField();
    txfTitle.setPromptText("Title");
    gpMedia.add(lblTitle, 0, 0);
    gpMedia.add(txfTitle, 1, 0);
    Label lblFormat = new Label("Format:");
    TextField txfFormat = new TextField();
    txfFormat.setPromptText("Format");
    gpMedia.add(lblFormat, 0, 1);
    gpMedia.add(txfFormat, 1, 1);
    Button btnAdd = new Button("Add");
    btnAdd.setOnAction(ev -> {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      if (txfTitle.getText().trim().equals("") && txfFormat.getText().trim().equals("")) {
        alert.setContentText("You must enter a title and format.");
        alert.showAndWait();
      } else {
        if (!dbc.add(new Media(txfTitle.getText().trim(), txfFormat.getText().trim()), new Loanee())) {
          alert.setContentText("You must enter a nonduplicate.");
          alert.showAndWait();
        }
      }
    });
    gpMedia.add(btnAdd, 0, 2);
    return gpMedia;
  }

  static VBox createButtons(DatabaseController dbc, ListView<ObservableEntry> lvMediaItems) {
    VBox vbxBtnsRR = new VBox();
    vbxBtnsRR.getStyleClass().add("vbox-buttons");
    Button btnRemove = new Button("Remove");
    btnRemove.prefWidthProperty().bind(vbxBtnsRR.widthProperty().multiply(0.32));
    btnRemove.setOnAction(ev -> {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      if (lvMediaItems.getSelectionModel().getSelectedIndex() == -1) {
        alert.setContentText("You must have selected a row from the list view.");
        alert.showAndWait();
      } else {
        dbc.remove(lvMediaItems.getSelectionModel().getSelectedIndex());
      }
    });

    Button btnReturn = new Button("Return");
    btnReturn.prefWidthProperty().bind(vbxBtnsRR.widthProperty().multiply(0.32));
    btnReturn.setOnAction(ev -> {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      try {
        dbc.setMediaLoanee(lvMediaItems.getSelectionModel().getSelectedIndex(), new Loanee());
      } catch (Exception ex) {
        alert.setContentText(ex.getLocalizedMessage());
        alert.showAndWait();
      }
    });
    vbxBtnsRR.getChildren().addAll(btnRemove, btnReturn);
    return vbxBtnsRR;
  }

  static GridPane createLoanControls(DatabaseController dbc, ListView<ObservableEntry> lvMediaItems) {
    GridPane gpLoan = new GridPane();
    gpLoan.getStyleClass().add("nested-grid");
    Button btnLoan = new Button("Loan");
    Label lblLoanedTo = new Label("Loaned To:");
    Label lblLoanedOn = new Label("Loaned On:");
    TextField txfLoanedTo = new TextField();
    txfLoanedTo.setPromptText("Name");
    TextField txfLoanedOn = new TextField();
    txfLoanedOn.setPromptText("Date (yyyy-MM-dd)");
    btnLoan.setOnAction(ev -> {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      if (!txfLoanedTo.getText().trim().equals("") && !txfLoanedOn.getText().trim().equals("")) {
        try {
          Date parsedDate = dbc.getDate(txfLoanedOn.getText());
          dbc.setMediaLoanee(lvMediaItems.getSelectionModel().getSelectedIndex(),
              new Loanee(txfLoanedTo.getText(), parsedDate));
        } catch (Exception ex) {
          alert.setContentText(ex.getLocalizedMessage());
          alert.showAndWait();
        }
      } else {
        alert.setContentText("Enter the information for loaned to, and loaned on.");
        alert.showAndWait();
      }
    });
    gpLoan.add(lblLoanedTo, 0, 0);
    gpLoan.add(txfLoanedTo, 1, 0);
    gpLoan.add(lblLoanedOn, 0, 1);
    gpLoan.add(txfLoanedOn, 1, 1);
    gpLoan.add(btnLoan, 0, 2);
    return gpLoan;
  }

  static VBox createSortControls(DatabaseController dbc, ListView<ObservableEntry> lvMediaItems) {
    VBox vbxSort = new VBox();
    vbxSort.getStyleClass().add("vbox-sort");
    Label lblSort = new Label("Sort");
    ToggleGroup toggleGroup = new ToggleGroup();
    RadioButton rdiBtnByTitle = new RadioButton("By title");
    rdiBtnByTitle.setOnAction(ev -> {
      dbc.setSortMethod(Database.SortBy.TITLE);
    });
    RadioButton rdiBtnByDateLoaned = new RadioButton("By date loaned");
    rdiBtnByDateLoaned.setOnAction(ev -> {
      dbc.setSortMethod(Database.SortBy.DATE);
    });
    rdiBtnByTitle.setToggleGroup(toggleGroup);
    rdiBtnByTitle.setSelected(true);
    rdiBtnByDateLoaned.setToggleGroup(toggleGroup);
    vbxSort.getChildren().addAll(lblSort, rdiBtnByTitle, rdiBtnByDateLoaned);
    return vbxSort;
  }

  @Override
  public void start(Stage primaryStage) {

    /*
     * The types are documentation, do not use the diamond operator here. It is
     * nice to know that the internals of the database is really a HashMap.
     */
    Database db = new Database(new HashMap<Media, Loanee>());
    if (db.open("db.datsav")) {
    } else {
      System.err.println("No media collection found to read...");
    }

    DatabaseController dbc = new DatabaseController(db);

    ListView<ObservableEntry> lvMediaItems = new ListView<>();
    lvMediaItems.getStyleClass().add("list");
    lvMediaItems.setItems(dbc.getObservableDatabase());

    GridPane options = new GridPane();
    options.getStyleClass().add("grid");
    options.add(createCreationControl(dbc, lvMediaItems), 0, 0);
    options.add(createButtons(dbc, lvMediaItems), 0, 1);
    options.add(createLoanControls(dbc, lvMediaItems), 0, 2);
    options.add(createSortControls(dbc, lvMediaItems), 0, 3);

    BorderPane root = new BorderPane();
    root.setCenter(lvMediaItems);
    root.setRight(options);

    Scene scene = new Scene(root, 800, 600);
    scene.getStylesheets().add(Main.class.getResource("MediaLibrary.css").toExternalForm());

    primaryStage.setTitle("Media Collection");
    /*
     * XMonad becomes spastic when resizable is true.
     * primaryStage.setResizable(false);
     */
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
