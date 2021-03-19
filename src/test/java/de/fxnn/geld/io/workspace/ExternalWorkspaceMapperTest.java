package de.fxnn.geld.io.workspace;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.fxnn.geld.io.ikonli.CategoryIcon;
import de.fxnn.geld.jfx.model.CategoryModel;
import de.fxnn.geld.jfx.model.TransactionModel;
import de.fxnn.geld.jfx.model.WorkspaceModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExternalWorkspaceMapperTest {

  @Test
  void toExternal() {
    var internalCategory = new CategoryModel();
    internalCategory.setFilterExpression("filter");
    internalCategory.setCategoryIcon(CategoryIcon.CAR);

    var internalTransaction = new TransactionModel();
    internalTransaction.setBalanceBefore(new BigDecimal("1.11"));
    internalTransaction.setAmount(new BigDecimal("5.55"));
    internalTransaction.setBalanceAfter(new BigDecimal("9.99"));
    internalTransaction.setTransactionDescription("DESCRIPTION");
    internalTransaction.setReferenceText("REFERENCE TEXT");
    internalTransaction.setBeneficiary("BENEFICIARY");
    internalTransaction.setDate(LocalDate.of(1999, 12, 31));

    var internalWorkspace = new WorkspaceModel();
    internalWorkspace.getCategoryList().add(internalCategory);
    internalWorkspace.getTransactionList().add(internalTransaction);

    var externalWorkspace = ExternalWorkspaceMapper.create().toExternal(internalWorkspace);
    assertNotNull(externalWorkspace);

    assertThat(externalWorkspace.getCategoryList(), hasSize(1));
    var externalCategory = externalWorkspace.getCategoryList().get(0);
    assertEquals("CAR", externalCategory.getCategoryIconName());
    assertEquals("filter", externalCategory.getFilterExpression());

    assertThat(externalWorkspace.getTransactionList(), hasSize(1));
    var externalTransaction = externalWorkspace.getTransactionList().get(0);

    assertEquals(new BigDecimal("1.11"), externalTransaction.getBalanceBefore());
    assertEquals(new BigDecimal("5.55"), externalTransaction.getAmount());
    assertEquals(new BigDecimal("9.99"), externalTransaction.getBalanceAfter());
    assertEquals("DESCRIPTION", externalTransaction.getTransactionDescription());
    assertEquals("REFERENCE TEXT", externalTransaction.getReferenceText());
    assertEquals("BENEFICIARY", externalTransaction.getBeneficiary());
    assertEquals(LocalDate.of(1999, 12, 31), externalTransaction.getDate());
  }

  @Test
  void toInternal() {
    var externalCategory = new ExternalCategory(CategoryIcon.CAR.name(), "filter");
    var externalTransaction =
        new ExternalTransaction(
            new BigDecimal("1.11"),
            new BigDecimal("5.55"),
            new BigDecimal("9.99"),
            "DESCRIPTION",
            "REFERENCE TEXT",
            "BENEFICIARY",
            LocalDate.of(1999, 12, 31));
    var externalWorkspace =
        new ExternalWorkspace(List.of(externalCategory), List.of(externalTransaction));

    var internalWorkspace = ExternalWorkspaceMapper.create().toInternal(externalWorkspace);
    assertNotNull(internalWorkspace);

    assertThat(internalWorkspace.getCategoryList(), hasSize(1));
    var internalCategory = internalWorkspace.getCategoryList().get(0);

    assertEquals(CategoryIcon.CAR, internalCategory.getCategoryIcon());
    assertEquals("filter", internalCategory.getFilterExpression());

    assertThat(internalWorkspace.getTransactionList(), hasSize(1));
    var internalTransaction = internalWorkspace.getTransactionList().get(0);

    assertEquals(new BigDecimal("1.11"), internalTransaction.getBalanceBefore());
    assertEquals(new BigDecimal("5.55"), internalTransaction.getAmount());
    assertEquals(new BigDecimal("9.99"), internalTransaction.getBalanceAfter());
    assertEquals("DESCRIPTION", internalTransaction.getTransactionDescription());
    assertEquals("REFERENCE TEXT", internalTransaction.getReferenceText());
    assertEquals("BENEFICIARY", internalTransaction.getBeneficiary());
    assertEquals(LocalDate.of(1999, 12, 31), internalTransaction.getDate());
  }
}
