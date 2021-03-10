package de.fxnn.geld.io.workspace;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import de.fxnn.geld.jfx.model.TransactionModel;
import de.fxnn.geld.jfx.model.WorkspaceModel;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkspaceModelIoTest {

  FileSystem fs;
  Path path;

  @BeforeEach
  void setup() {
    fs = Jimfs.newFileSystem(Configuration.unix());
    path = fs.getPath("/test.txt");
  }

  @Test
  void roundtrip() throws IOException {
    var givenTransaction = new TransactionModel();
    givenTransaction.setBalanceBefore(new BigDecimal("1.11"));
    givenTransaction.setAmount(new BigDecimal("5.55"));
    givenTransaction.setBalanceAfter(new BigDecimal("9.99"));
    givenTransaction.setTransactionDescription("DESCRIPTION");
    givenTransaction.setReferenceText("REFERENCE TEXT");
    givenTransaction.setBeneficiary("BENEFICIARY");
    givenTransaction.setDate(LocalDate.of(1999, 12, 31));

    var givenWorkspace = new WorkspaceModel();
    givenWorkspace.getTransactionList().add(givenTransaction);

    var sut = new WorkspaceModelIo();
    sut.store(givenWorkspace, path, StandardOpenOption.CREATE_NEW);
    var actualWorkspace = sut.load(path);

    assertEquals(
        givenWorkspace.getTransactionList().size(), actualWorkspace.getTransactionList().size());

    var actualTransaction = actualWorkspace.getTransactionList().get(0);
    assertEquals(givenTransaction.getBalanceBefore(), actualTransaction.getBalanceBefore());
    assertEquals(givenTransaction.getAmount(), actualTransaction.getAmount());
    assertEquals(givenTransaction.getBalanceAfter(), actualTransaction.getBalanceAfter());
    assertEquals(
        givenTransaction.getTransactionDescription(),
        actualTransaction.getTransactionDescription());
    assertEquals(givenTransaction.getReferenceText(), actualTransaction.getReferenceText());
    assertEquals(givenTransaction.getBeneficiary(), actualTransaction.getBeneficiary());
    assertEquals(givenTransaction.getDate(), actualTransaction.getDate());
  }
}
