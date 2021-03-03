package de.fxnn.geld.io.mt940;

import static org.junit.jupiter.api.Assertions.*;

import de.fxnn.geld.io.mt940.Mt940TransactionField.FundsCode;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class Mt940TransactionFieldTest {

  @Test
  void date__betweenYears() {
    var givenYear = "99";
    var givenDate = "1231";
    var givenEntryDate = "0101";
    var givenRawContent =
        givenYear + givenDate + givenEntryDate + "C20000,00FMSCNONREF//8327000090031789";

    var sut = Mt940TransactionField.of(new Mt940RawField("61", givenRawContent));

    assertEquals(LocalDate.of(1999, 12, 31), sut.getDate());
    assertEquals(LocalDate.of(2000, 1, 1), sut.getEntryDate());
  }

  @Test
  void currency() {
    // NOTE the "R" behind the "D". It's an optional field, not mentioned by some specs
    var givenRawContent = "1611231123DR106,75N010NONREF";
    var sut = Mt940TransactionField.of(new Mt940RawField("61", givenRawContent));
    assertEquals(FundsCode.DEBIT, sut.getFundsCode());
  }
}
