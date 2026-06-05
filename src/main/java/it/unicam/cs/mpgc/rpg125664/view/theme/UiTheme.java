package it.unicam.cs.mpgc.rpg125664.view.theme;

import javafx.scene.Parent;

/** Strategy per skin di presentazione alternative: stesso FXML, classi CSS diverse sulla root. */
public interface UiTheme {

  void applyTo(Parent root);
}
