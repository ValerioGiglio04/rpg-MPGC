package it.unicam.cs.mpgc.rpg125664.view.support;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Singolo punto di accesso per le stringhe localizzate della UI. Si appoggia a un {@link
 * ResourceBundle} cosi' aggiungere una lingua nuova e' solo droppare un {@code
 * messages_xx.properties} accanto a quello esistente. Volutamente semplice: niente hot-reload,
 * niente fallback oltre a quelli che il JDK gia' fornisce.
 */
public final class Messages {

  private static final String BUNDLE_BASE = "i18n.messages";
  private static volatile ResourceBundle bundle =
      ResourceBundle.getBundle(BUNDLE_BASE, Locale.ITALIAN);

  private Messages() {}

  public static String get(String key) {
    return bundle.getString(key);
  }

  public static String format(String key, Object... arguments) {
    return MessageFormat.format(bundle.getString(key), arguments);
  }

  public static void setLocale(Locale locale) {
    bundle = ResourceBundle.getBundle(BUNDLE_BASE, locale);
  }

  /**
   * Stesso bundle che FXML risolve per i {@code %key} tramite {@link
   * javafx.fxml.FXMLLoader#setResources}.
   */
  public static ResourceBundle resourceBundle() {
    return bundle;
  }

  public static Locale locale() {
    return bundle.getLocale();
  }
}
