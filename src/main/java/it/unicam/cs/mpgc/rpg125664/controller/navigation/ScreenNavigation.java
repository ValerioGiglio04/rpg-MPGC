package it.unicam.cs.mpgc.rpg125664.controller.navigation;

/**
 * Unione dei comandi di navigazione per schermata. {@link
 * it.unicam.cs.mpgc.rpg125664.controller.navigation.ScreenNavigator} implementa
 * tutte le interfacce ruolo-specifiche; ogni {@code *ActionsImpl} dipende solo dalla propria.
 */
public interface ScreenNavigation
    extends MainMenuNavigation, LoadGameNavigation, HubNavigation, VictoryNavigation {}
