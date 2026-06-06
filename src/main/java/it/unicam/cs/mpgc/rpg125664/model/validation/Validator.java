package it.unicam.cs.mpgc.rpg125664.model.validation;

/**
 * Validazione di un aggregato {@code T}. Implementazioni in {@code validation.implementations}.
 * Pattern: {@code Validator<T> validator = ValidatorFactory.get*Validator();
 * validator.validate(instance);}.
 */
public abstract class Validator<T> {

  protected Validator() {}

  public final void validate(T value) {
    Rules.requireNonNull(value, nullMessage());
    validateRules(value);
  }

  protected abstract void validateRules(T value);

  protected abstract String nullMessage();
}
