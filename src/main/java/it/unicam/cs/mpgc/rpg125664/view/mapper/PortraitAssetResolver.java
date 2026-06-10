package it.unicam.cs.mpgc.rpg125664.view.mapper;

import it.unicam.cs.mpgc.rpg125664.model.catalog.GameCatalog;
import java.util.Objects;

/**
 * Risolve i percorsi classpath dei ritratti a partire dal catalogo, senza leggere il dominio
 * runtime.
 */
public final class PortraitAssetResolver {

  private static final String DEFAULT_TRAINER_IMAGE = "/images/player/player-skin.png";

  /** Vecchio default PNG nel catalogo: stesso portrait del nuovo asset. */
  private static final String LEGACY_TRAINER_PNG = "/images/player/trainer-default.png";

  private final GameCatalog catalog;

  public PortraitAssetResolver(GameCatalog catalog) {
    this.catalog = Objects.requireNonNull(catalog, "catalog");
  }

  public String playerSkinPath() {
    return resolvePlayerSkinPath(catalog.settings().playerSkinPath());
  }

  public String creatureSkinPath(long catalogId) {
    return catalog.creatureSkinPath(catalogId);
  }

  public String resolvePlayerSkinPath(String skinPath) {
    if (LEGACY_TRAINER_PNG.equals(skinPath)) {
      return DEFAULT_TRAINER_IMAGE;
    }
    return skinPath;
  }
}
