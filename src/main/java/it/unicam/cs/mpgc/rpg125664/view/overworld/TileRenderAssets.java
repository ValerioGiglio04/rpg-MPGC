package it.unicam.cs.mpgc.rpg125664.view.overworld;

import java.util.Objects;
import javafx.scene.image.Image;

/** Asset grafici condivisi dal renderer delle celle overworld. */
public final class TileRenderAssets {

  private final int tileSize;
  private final Image gymBuildingImage;
  private final Image treeImage;
  private final Image bushImage;

  private TileRenderAssets(Builder builder) {
    this.tileSize = builder.tileSize;
    this.gymBuildingImage = Objects.requireNonNull(builder.gymBuildingImage, "gymBuildingImage");
    this.treeImage = Objects.requireNonNull(builder.treeImage, "treeImage");
    this.bushImage = Objects.requireNonNull(builder.bushImage, "bushImage");
  }

  public static Builder builder() {
    return new Builder();
  }

  public int tileSize() {
    return tileSize;
  }

  public Image gymBuildingImage() {
    return gymBuildingImage;
  }

  public Image treeImage() {
    return treeImage;
  }

  public Image bushImage() {
    return bushImage;
  }

  public static final class Builder {

    private int tileSize;
    private Image gymBuildingImage;
    private Image treeImage;
    private Image bushImage;

    public Builder tileSize(int tileSize) {
      this.tileSize = tileSize;
      return this;
    }

    public Builder gymBuildingImage(Image gymBuildingImage) {
      this.gymBuildingImage = gymBuildingImage;
      return this;
    }

    public Builder treeImage(Image treeImage) {
      this.treeImage = treeImage;
      return this;
    }

    public Builder bushImage(Image bushImage) {
      this.bushImage = bushImage;
      return this;
    }

    public TileRenderAssets build() {
      return new TileRenderAssets(this);
    }
  }
}
