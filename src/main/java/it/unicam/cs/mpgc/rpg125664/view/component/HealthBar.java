package it.unicam.cs.mpgc.rpg125664.view.component;

import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

public final class HealthBar extends VBox {
  private ProgressBar createProgressBar(int currentHealth, int maxHealth) {
    double progressValue = maxHealth <= 0 ? 0 : (double) currentHealth / maxHealth;
    ProgressBar progressBar = new ProgressBar(progressValue);
    progressBar.getStyleClass().add("health-bar");
    progressBar.getStyleClass().add(ratioStyleClass(currentHealth, maxHealth));
    progressBar.setMaxWidth(Double.MAX_VALUE);
    return progressBar;
  }

  private String extractHealthLineText(String label, int currentHealth, int maxHealth) {
    if (label == null || label.isBlank()) {
      return Messages.format("health.bar.points", currentHealth, maxHealth);
    }
    return Messages.format("health.bar.named", label, currentHealth, maxHealth);
  }

  private Label createHealthLabel(String label, int currentHealth, int maxHealth) {
    String line = extractHealthLineText(label, currentHealth, maxHealth);
    Label healthLabel = new Label(line);
    healthLabel.getStyleClass().add("health-bar-label");
    return healthLabel;
  }

  public HealthBar(String label, int currentHealth, int maxHealth) {
    super(5);
    Label healthLabel = createHealthLabel(label, currentHealth, maxHealth);
    ProgressBar progressBar = createProgressBar(currentHealth, maxHealth);
    getChildren().addAll(healthLabel, progressBar);
  }

  private static String ratioStyleClass(int currentHealth, int maxHealth) {
    if (maxHealth <= 0) return "health-bar-low";
    double ratio = (double) currentHealth / maxHealth;
    if (ratio > 0.5) return "health-bar-high";
    if (ratio > 0.2) return "health-bar-mid";
    return "health-bar-low";
  }
}
