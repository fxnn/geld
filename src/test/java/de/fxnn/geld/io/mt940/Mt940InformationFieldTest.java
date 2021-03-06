package de.fxnn.geld.io.mt940;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Mt940InformationFieldTest {

  @Test
  void exampleSwiftMt940International1() {
    var sut =
        createSut(
            "020?00Wyplata/przelew?20DEUTSCHE ELEKTROAPPARATUR?21OBENSTRAS"
                + "SE 4 MUNCHEN?22OCMT/EUR1088,41?23CHGS/SHA/EUR20,00?24FAKTURA 333"
                + "/2003 ZAPLATA ZA?25FABRYKATY DO TUB 200 SZTUK?26GZX 76 I 300 SZT"
                + "UK GZY 77 T?27RANZYSTORY 300 SZTUK BT34SX?28OPORNIKI 500 SZTUK W"
                + "Q2?29232FX?30HYVEDEMM700?31701890012872?38DE09700202701890012872");
    assertTrue(sut.isDomesticTransfer());
    assertFalse(sut.isSepaTransfer());
    assertFalse(sut.isInternationalTransfer());
    assertFalse(sut.isUnstructuredContent());
    assertEquals("Wyplata/przelew", sut.getTransactionDescription());
    assertEquals(
        "DEUTSCHE ELEKTROAPPARATUR OBENSTRASSE 4 MUNCHEN "
            + "OCMT/EUR1088,41 CHGS/SHA/EUR20,00 FAKTURA 333/2003 ZAPLATA ZA "
            + "FABRYKATY DO TUB 200 SZTUK GZX 76 I 300 SZTUK GZY 77 T "
            + "RANZYSTORY 300 SZTUK BT34SX OPORNIKI 500 SZTUK WQ2 232FX",
        sut.getReferenceText());
    assertEquals("HYVEDEMM700", sut.getBeneficiaryBankCode());
    assertEquals("701890012872", sut.getBeneficiaryAccountNumber());
    assertNull(sut.getBeneficiaryName());
    assertEquals("DE09700202701890012872", sut.getIban());
  }

  @Test
  void exampleSwiftMt940Sparkasse1_1() {
    var sut =
        createSut(
            "006?00EIGENE KREDITKARTENABRECHN.?101234?20KREDITKARTEN-ABR.X"
                + "VOM 18.11?21447595XXXXXX7209     106,75?3083050000?310123456789?3"
                + "2SPARKASSE GERA-GREIZ?34123");
    assertTrue(sut.isDomesticTransfer());
    assertFalse(sut.isSepaTransfer());
    assertFalse(sut.isInternationalTransfer());
    assertFalse(sut.isUnstructuredContent());
    assertEquals("EIGENE KREDITKARTENABRECHN.", sut.getTransactionDescription());
    assertEquals("KREDITKARTEN-ABR.XVOM 18.11 447595XXXXXX7209     106,75", sut.getReferenceText());
  }

  @Test
  void exampleSwiftMt940Sparkasse1_2() {
    var sut =
        createSut(
            "107?00SEPA-ELV-LASTSCHRIFT?101234?20EREF+12345678901234567890"
                + "12?2112345?22MREF+0123456789012345678890?23CRED+01234567890123456"
                + "7?24SVWZ+0123456789012345678901?2512345 REWE SAGT DANKE. 6789?260"
                + "123?30ABCDEFGH?310123456789012345678901?32REWE Berlin/Friede?3401"
                + "2");
    assertFalse(sut.isDomesticTransfer());
    assertTrue(sut.isSepaTransfer());
    assertFalse(sut.isInternationalTransfer());
    assertFalse(sut.isUnstructuredContent());
    assertEquals("SEPA-ELV-LASTSCHRIFT", sut.getTransactionDescription());
    assertEquals(
        "EREF+1234567890123456789012 "
            + "12345 MREF+0123456789012345678890 "
            + "CRED+012345678901234567 "
            + "SVWZ+0123456789012345678901 "
            + "12345 REWE SAGT DANKE. 6789 0123",
        sut.getReferenceText());
    assertEquals("123456789012345678901212345", sut.getSepaEndToEndReference());
    assertEquals("0123456789012345678890", sut.getSepaMandateReference());
    assertEquals("012345678901234567", sut.getSepaCreditorId());
    assertEquals(
        "012345678901234567890112345 REWE SAGT DANKE. 67890123", sut.getSepaReferenceText());
    assertEquals("ABCDEFGH", sut.getBeneficiaryBankCode());
    assertEquals("0123456789012345678901", sut.getBeneficiaryAccountNumber());
    assertEquals("REWE Berlin/Friede", sut.getBeneficiaryName());
  }

  @Test
  void exampleSwiftMt940Sparkasse1_3() {
    var sut =
        createSut(
            "106?00AUSZAHLUNG?109123?20SVWZ+2016-11-25T01.02.03 Ka?21rte9"
                + "2016-12?22ABWA+123 456789//Berliner S?23parkasse/DE?30BELADEBEXXX"
                + "?31DE01234567890123456789?32Berliner Sparkasse?34123");
    assertFalse(sut.isDomesticTransfer());
    assertTrue(sut.isSepaTransfer());
    assertFalse(sut.isInternationalTransfer());
    assertFalse(sut.isUnstructuredContent());
    assertEquals("AUSZAHLUNG", sut.getTransactionDescription());
    assertEquals(
        "SVWZ+2016-11-25T01.02.03 Ka rte92016-12 " + "ABWA+123 456789//Berliner S parkasse/DE",
        sut.getReferenceText());
    assertEquals("2016-11-25T01.02.03 Karte92016-12", sut.getSepaReferenceText());
    assertEquals("123 456789//Berliner Sparkasse/DE", sut.getSepaUltimatePrincipal());
    assertEquals("BELADEBEXXX", sut.getBeneficiaryBankCode());
    assertEquals("DE01234567890123456789", sut.getBeneficiaryAccountNumber());
    assertEquals("Berliner Sparkasse", sut.getBeneficiaryName());
  }

  @Test
  void exampleSwiftMt940Sparkasse1_4() {
    var sut =
        createSut(
            "106?00KARTENZAHLUNG?101234?20SVWZ+2016-11-25T10.11.12 Ka?21rt"
                + "e9 2016-11?22ABWA+TK Maxx Berlin Schoene?23berg//Berlin/DE?30DRES"
                + "DEFF300?31DE01234567890123456789?32TK MAXX BERLIN SCHOE BERLIN?34"
                + "012");
    assertFalse(sut.isDomesticTransfer());
    assertTrue(sut.isSepaTransfer());
    assertFalse(sut.isInternationalTransfer());
    assertFalse(sut.isUnstructuredContent());
    assertEquals("KARTENZAHLUNG", sut.getTransactionDescription());
    assertEquals(
        "SVWZ+2016-11-25T10.11.12 Ka rte9 2016-11 " + "ABWA+TK Maxx Berlin Schoene berg//Berlin/DE",
        sut.getReferenceText());
    assertEquals("2016-11-25T10.11.12 Karte9 2016-11", sut.getSepaReferenceText());
    assertEquals("TK Maxx Berlin Schoeneberg//Berlin/DE", sut.getSepaUltimatePrincipal());
    assertEquals("DRESDEFF300", sut.getBeneficiaryBankCode());
    assertEquals("DE01234567890123456789", sut.getBeneficiaryAccountNumber());
    assertEquals("TK MAXX BERLIN SCHOE BERLIN", sut.getBeneficiaryName());
  }

  private Mt940InformationField createSut(String rawContent) {
    return Mt940InformationField.of(new Mt940RawField("86", rawContent));
  }
}
