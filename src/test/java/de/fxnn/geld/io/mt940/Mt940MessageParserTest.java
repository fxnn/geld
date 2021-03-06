package de.fxnn.geld.io.mt940;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.fxnn.geld.io.mt940.Mt940Message.Transaction;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class Mt940MessageParserTest {

  @Test
  void exampleSwiftMt940Sparkasse1() throws IOException {
    var mt940Case = Mt940Case.load("exampleSwiftMt940Sparkasse1");
    var sut = createSut(mt940Case);

    Mt940Message message;
    Transaction transaction;

    assertTrue(sut.hasNext());
    message = sut.next();
    assertNotNull(message);
    assertEquals(
        "83050000/0123456789", message.getHeader(Mt940AccountField.class).getAccountNumber());
    assertEquals(22777L, message.getHeader(Mt940BalanceField.class).getAmount());
    assertThat(message.getTransactions(), hasSize(1));
    transaction = message.getTransactions().get(0);
    assertEquals(10675L, transaction.getTransactionField().getAmount());
    assertEquals("SPARKASSE GERA-GREIZ", transaction.getInformationField().getBeneficiaryName());
    assertEquals(12102L, message.getFooter(Mt940BalanceField.class).getAmount());

    assertTrue(sut.hasNext());
    message = sut.next();
    assertNotNull(message);
    assertEquals(
        "83050000/0123456789", message.getHeader(Mt940AccountField.class).getAccountNumber());
    assertEquals(12102L, message.getHeader(Mt940BalanceField.class).getAmount());
    assertThat(message.getTransactions(), hasSize(2));
    transaction = message.getTransactions().get(0);
    assertEquals(515L, transaction.getTransactionField().getAmount());
    assertEquals("REWE Berlin/Friede", transaction.getInformationField().getBeneficiaryName());
    assertEquals(
        "012345678901234567890112345 REWE SAGT DANKE. 67890123",
        transaction.getInformationField().getSepaReferenceText());
    transaction = message.getTransactions().get(1);
    assertEquals(6000L, transaction.getTransactionField().getAmount());
    assertEquals("AUSZAHLUNG", transaction.getInformationField().getTransactionDescription());
    assertEquals(
        "2016-11-25T01.02.03 Karte92016-12",
        transaction.getInformationField().getSepaReferenceText());
    assertEquals(
        "123 456789//Berliner Sparkasse/DE",
        transaction.getInformationField().getSepaUltimatePrincipal());
    assertEquals(5587L, message.getFooter(Mt940BalanceField.class).getAmount());

    assertTrue(sut.hasNext());
    message = sut.next();
    assertNotNull(message);
    assertEquals(
        "83050000/0123456789", message.getHeader(Mt940AccountField.class).getAccountNumber());
    assertEquals(5587L, message.getHeader(Mt940BalanceField.class).getAmount());
    assertThat(message.getTransactions(), hasSize(1));
    transaction = message.getTransactions().get(0);
    assertEquals(2497L, transaction.getTransactionField().getAmount());
    assertEquals("KARTENZAHLUNG", transaction.getInformationField().getTransactionDescription());
    assertEquals(
        "TK Maxx Berlin Schoeneberg//Berlin/DE",
        transaction.getInformationField().getSepaUltimatePrincipal());
    assertEquals(
        "TK MAXX BERLIN SCHOE BERLIN", transaction.getInformationField().getBeneficiaryName());
    assertEquals(3090L, message.getFooter(Mt940BalanceField.class).getAmount());

    assertFalse(sut.hasNext());
  }

  private Mt940MessageParser createSut(Mt940Case mt940Case) {
    return new Mt940MessageParser(new StringReader(mt940Case.getData()));
  }
}
