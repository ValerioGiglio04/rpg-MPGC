package it.unicam.cs.mpgc.rpg125664.model.validation;

/**
 * Contratto comune per i validator del dominio: ricevono un oggetto e lanciano se viola gli
 * invarianti del tipo. Le implementazioni concrete estendono {@link AbstractDomainValidator} e si
 * ottengono tramite {@link Validators}; nessun chiamante esterno deve costruirle direttamente.
 *
 * <p>Il tipo generico {@code T} rappresenta il tipo del valore da validare.
 */
@FunctionalInterface
public interface Validator<T> {

  /** Verifica le invarianti del valore: lancia {@link IllegalArgumentException} se violate. */
  void validate(T value);
}
