package it.unicam.cs.mpgc.rpg125664.app;

import it.unicam.cs.mpgc.rpg125664.controller.navigation.support.MainView;
import it.unicam.cs.mpgc.rpg125664.view.support.Messages;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Applicazione JavaFX principale (stage, scena, bootstrap moduli). */
public final class RpgApplication extends Application {

  private static final int WIDTH = 1200;
  private static final int HEIGHT = 780;
  private static final int STAGE_MIN_WIDTH = 960;
  private static final int STAGE_MIN_HEIGHT = 640;

  private AppModule appModule;

  public static void launchApplication(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    appModule = AppModule.bootstrap();
    MainView mainView = new MainView(appModule.gameModel(), appModule.portraitAssets());

    stage.setTitle(Messages.get("app.title"));
    stage.setMinWidth(STAGE_MIN_WIDTH);
    stage.setMinHeight(STAGE_MIN_HEIGHT);
    stage.setScene(new Scene(mainView.root(), WIDTH, HEIGHT));
    stage.show();
  }

  @Override
  public void stop() {
    if (appModule != null) appModule.close();
  }
}
