package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.model.session.SavedSessionSummary;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.LoadGameActions;
import it.unicam.cs.mpgc.rpg125664.ui.javafx.Messages;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public final class LoadGameController implements Initializable {

  private static final DateTimeFormatter SAVED_AT_FORMAT =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withLocale(Locale.ITALY);

  private final GameModel gameModel;
  private final LoadGameActions actions;

  @FXML private ListView<SavedSessionSummary> savesList;

  @FXML private Button loadButton;

  @FXML private Button deleteButton;

  @FXML private Button backButton;

  public LoadGameController(GameModel gameModel, LoadGameActions actions) {
    this.gameModel = gameModel;
    this.actions = actions;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    savesList.setItems(FXCollections.observableArrayList(gameModel.listSaves()));
    savesList.setCellFactory(summaryCellFactory());
    if (!savesList.getItems().isEmpty()) {
      savesList.getSelectionModel().selectFirst();
    }
    updateActionButtons();
    savesList
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((obs, old, selected) -> updateActionButtons());
    loadButton.setOnAction(event -> loadSelected());
    deleteButton.setOnAction(event -> deleteSelected());
    backButton.setOnAction(event -> actions.onBack());
  }

  private void updateActionButtons() {
    boolean selected = savesList.getSelectionModel().getSelectedItem() != null;
    loadButton.setDisable(!selected);
    deleteButton.setDisable(!selected);
  }

  private void loadSelected() {
    SavedSessionSummary selected = savesList.getSelectionModel().getSelectedItem();
    if (selected == null) return;
    actions.onLoadSelected(selected.id());
  }

  private void deleteSelected() {
    SavedSessionSummary selected = savesList.getSelectionModel().getSelectedItem();
    if (selected == null) return;
    actions.onDeleteSelected(selected.id());
  }

  private static Callback<ListView<SavedSessionSummary>, ListCell<SavedSessionSummary>>
      summaryCellFactory() {
    return list ->
        new ListCell<>() {
          @Override
          protected void updateItem(SavedSessionSummary item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText(null);
              return;
            }
            String when = SAVED_AT_FORMAT.format(item.savedAt().atZone(ZoneId.systemDefault()));
            setText(Messages.format("load.screen.row", item.name(), when, item.gloryPoints()));
          }
        };
  }
}
