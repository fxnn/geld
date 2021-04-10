package de.fxnn.geld.jfx.model;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TransactionModelTest {

  @ParameterizedTest
  @MethodSource
  void containsAnyLowerCase__matches(String input, String expectedMatchingExpression) {
    var sut = new TransactionModel();
    sut.setReferenceText(input);
    sut.updateTransientProperties();

    assertTrue(sut.containsLowerCaseWord(expectedMatchingExpression));
  }

  static Stream<Arguments> containsAnyLowerCase__matches() {
    return Stream.of(
        arguments("PayPal (Europe) S.a.r.l. et Cie., S.C.A.", "paypal"),
        arguments("PayPal (Europe) S.a.r.l. et Cie., S.C.A.", "europe"),
        arguments("PP.4674.PP . GOOGLE PLAY, Ihr Einkauf bei GOOGLEPLAY", "pp.4674.pp"),
        arguments("PP.4674.PP . GOOGLE PLAY, Ihr Einkauf bei GOOGLEPLAY", "google"),
        arguments("PP.4674.PP . GOOGLE PLAY, Ihr Einkauf bei GOOGLEPLAY", "play"),
        arguments("PP.4674.PP . GOOGLE PLAY, Ihr Einkauf bei GOOGLEPLAY", "4674"),
        arguments(
            "VIELEN.DANK.BEITRAGSZAHLUNG FOERDERMITGLIED .MITGNR.1234.BETRAG. 2,50",
            "beitragszahlung"),
        arguments(
            "VIELEN.DANK.BEITRAGSZAHLUNG FOERDERMITGLIED .MITGNR.1234.BETRAG. 2,50",
            "foerdermitglied"),
        arguments("VIELEN.DANK.BEITRAGSZAHLUNG FOERDERMITGLIED .MITGNR.1234.BETRAG. 2,50", "1234"),
        arguments("VIELEN.DANK.BEITRAGSZAHLUNG FOERDERMITGLIED .MITGNR.1234.BETRAG. 2,50", "2,50"));
  }
}
