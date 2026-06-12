package it.unicam.cs.mpgc.rpg125664.view.overworld;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;

/** Mostra/nasconde gli overlay fissi della mappa (scroll, zoom, legenda, modale). */
final class OverworldMapChrome {

  private OverworldMapChrome() {}

  static void ensureVisible(
    ScrollPane mapScrollPane,
    Node zoomControls,
    Label legendLabel,
    StackPane modalLayer,
    boolean modalOpen
  ) {
    show(mapScrollPane);
    show(zoomControls);
    show(legendLabel);
    modalLayer.setManaged(true);
    modalLayer.setVisible(modalOpen);
    if (modalOpen) {
      modalLayer.toFront();
      return;
    }
    legendLabel.toFront();
    zoomControls.toFront();
  }

  private static void show(Node node) {
    node.setManaged(true);
    node.setVisible(true);
  }
}
