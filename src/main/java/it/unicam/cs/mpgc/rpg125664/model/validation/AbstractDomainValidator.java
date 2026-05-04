package it.unicam.cs.mpgc.rpg125664.model.validation;

/**
 * Base astratta per i validator del dominio: applica il controllo di nullità condiviso e delega le
 * regole specifiche alle sottoclassi (Template Method). Le implementazioni concrete restano
 * package-private e si ottengono tramite {@link Validators}.
 */
public abstract class AbstractDomainValidator<T> implements Validator<T> {

  @Override
  public final void validate(T value) {
    Rules.requireNonNull(value, nullMessage());
    validateRules(value);
  }

  /** Invarianti specifiche del tipo, dopo il controllo di nullità. */
  protected abstract void validateRules(T value);

  /** Messaggio usato quando il valore passato a {@link #validate(Object)} è null. */
  protected abstract String nullMessage();
}
