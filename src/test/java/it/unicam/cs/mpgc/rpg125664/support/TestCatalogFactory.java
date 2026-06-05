package it.unicam.cs.mpgc.rpg125664.support;

import it.unicam.cs.mpgc.rpg125664.model.catalog.BossTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.CreatureTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import it.unicam.cs.mpgc.rpg125664.model.catalog.GymTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.MoveTemplate;
import it.unicam.cs.mpgc.rpg125664.model.catalog.NewGameSettings;
import java.util.List;

/** Minimal in-memory catalog for unit and integration tests. */
public final class TestCatalogFactory {

  public static final long CREATURE_FAST = 101L;
  public static final long CREATURE_SLOW = 102L;
  public static final long GYM_START = 201L;
  public static final long GYM_NEXT = 202L;

  private TestCatalogFactory() {}

  public static GameCatalog minimal() {
    MoveTemplate weakMove = new MoveTemplate("Graffio", 5, 50, "Colpo debole.");
    MoveTemplate strongMove = new MoveTemplate("Raffica", 12, 90, "Colpo forte.");
    MoveTemplate bossFallback = new MoveTemplate("Morso", 8, 40, "Mossa boss.");

    CreatureTemplate fast =
        new CreatureTemplate(
            CREATURE_FAST,
            "Veloce",
            "Attaccante",
            "/test/fast.png",
            40,
            10,
            2,
            12,
            List.of(weakMove, strongMove));

    CreatureTemplate slow =
        new CreatureTemplate(
            CREATURE_SLOW,
            "Lento",
            "Tank",
            "/test/slow.png",
            50,
            6,
            8,
            4,
            List.of(bossFallback, strongMove));

    GymTemplate startGym =
        new GymTemplate(
            GYM_START,
            "Palestra Test A",
            1,
            0,
            List.of(GYM_NEXT),
            new BossTemplate("Boss A", 15, List.of(CREATURE_SLOW)));

    GymTemplate nextGym =
        new GymTemplate(
            GYM_NEXT,
            "Palestra Test B",
            2,
            10,
            List.of(GYM_START),
            new BossTemplate("Boss B", 25, List.of(CREATURE_SLOW)));

    NewGameSettings settings =
        new NewGameSettings("Tester", GYM_START, "/test/player.png", List.of(CREATURE_FAST));

    return new GameCatalog(settings, List.of(fast, slow), List.of(startGym, nextGym));
  }
}
