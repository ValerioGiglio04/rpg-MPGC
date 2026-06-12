package it.unicam.cs.mpgc.rpg125664.controller;

import it.unicam.cs.mpgc.rpg125664.controller.navigation.LoadGameActions;
import it.unicam.cs.mpgc.rpg125664.model.service.GameModel;
import it.unicam.cs.mpgc.rpg125664.model.session.SavedSessionSummary;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

/** Controller FXML caricamento partite (lista, load, delete). */
public final class LoadGameController implements Initializable {

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
    savesList.setCellFactory(list -> new SavedSessionSummaryCell());
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
}
