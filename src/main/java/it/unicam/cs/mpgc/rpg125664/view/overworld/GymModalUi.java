package it.unicam.cs.mpgc.rpg125664.view.overworld;

import java.util.Objects;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/** Riferimenti UI del modale palestra montato su {@link OverworldMap}. */
public final class GymModalUi {

  private final Label modalTitle;
  private final HBox modalActions;
  private final StackPane modalLayer;

  private GymModalUi(Builder builder) {
    this.modalTitle = Objects.requireNonNull(builder.modalTitle, "modalTitle");
    this.modalActions = Objects.requireNonNull(builder.modalActions, "modalActions");
    this.modalLayer = Objects.requireNonNull(builder.modalLayer, "modalLayer");
  }

  public static Builder builder() {
    return new Builder();
  }

  public Label modalTitle() {
    return modalTitle;
  }

  public HBox modalActions() {
    return modalActions;
  }

  public StackPane modalLayer() {
    return modalLayer;
  }

  public static final class Builder {

    private Label modalTitle;
    private HBox modalActions;
    private StackPane modalLayer;

    public Builder modalTitle(Label modalTitle) {
      this.modalTitle = modalTitle;
      return this;
    }

    public Builder modalActions(HBox modalActions) {
      this.modalActions = modalActions;
      return this;
    }

    public Builder modalLayer(StackPane modalLayer) {
      this.modalLayer = modalLayer;
      return this;
    }

    public GymModalUi build() {
      return new GymModalUi(this);
    }
  }
}
