package it.unicam.cs.mpgc.rpg125664.model.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg125664.model.builder.CreatureBuilder;
import it.unicam.cs.mpgc.rpg125664.model.builder.MoveBuilder;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DomainValidationTest {

  @ParameterizedTest
  @MethodSource("invalidCreatures")
  void creatureBuilderRejectsInvalidInput(Runnable build) {
    assertThrows(Exception.class, build::run);
  }

  @ParameterizedTest
  @MethodSource("invalidMoves")
  void moveBuilderRejectsInvalidInput(Runnable build) {
    assertThrows(Exception.class, build::run);
  }

  static Stream<Arguments> invalidCreatures() {
    var move = new MoveBuilder().name("Colpo").power(5).build();
    return Stream.of(
        Arguments.of(
            (Runnable)
                () ->
                    new CreatureBuilder()
                        .catalogId(1L)
                        .name("")
                        .maxHealth(10)
                        .attack(5)
                        .moves(List.of(move))
                        .build()),
        Arguments.of(
            (Runnable)
                () ->
                    new CreatureBuilder()
                        .catalogId(1L)
                        .name("X")
                        .maxHealth(10)
                        .attack(0)
                        .moves(List.of(move))
                        .build()));
  }

  static Stream<Arguments> invalidMoves() {
    return Stream.of(
        Arguments.of(
            (Runnable) () -> new MoveBuilder().name("").power(5).accuracy(50).build()),
        Arguments.of(
            (Runnable) () -> new MoveBuilder().name("X").power(5).accuracy(0).build()),
        Arguments.of(
            (Runnable) () -> new MoveBuilder().name("X").power(5).accuracy(101).build()));
  }
}
