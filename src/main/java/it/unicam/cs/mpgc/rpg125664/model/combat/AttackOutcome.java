package it.unicam.cs.mpgc.rpg125664.model.combat;

/**
 * Risultato di una singola risoluzione di mossa, senza testo cosi' la presentazione resta solo
 * nella UI.
 *
 * @param hit se l'attacco e' andato a segno
 * @param damage danno inflitto (zero in caso di miss)
 * @param defenderKnockedOut se il difensore e' arrivato a zero HP
 */
public record AttackOutcome(boolean hit, int damage, boolean defenderKnockedOut) {

  public static AttackOutcome miss() {
    return new AttackOutcome(false, 0, false);
  }

  public static AttackOutcome landed(int damage, boolean knockedOut) {
    return new AttackOutcome(true, damage, knockedOut);
  }
}
