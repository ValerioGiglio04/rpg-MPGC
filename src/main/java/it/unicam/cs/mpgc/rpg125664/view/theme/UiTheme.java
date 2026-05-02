package it.unicam.cs.mpgc.rpg125664.view.theme;

import javafx.scene.layout.StackPane;

/**
 * Punto di estensione per skin di presentazione alternative (es. futuri client mobile/web possono
 * applicare uno stack di classi tema diverso agli stessi host FXML).
 */
public interface UiTheme {

  void applyTo(StackPane root);
}
