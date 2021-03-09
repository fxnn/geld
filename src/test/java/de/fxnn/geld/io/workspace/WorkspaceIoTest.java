package de.fxnn.geld.io.workspace;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkspaceIoTest {

  FileSystem fs;
  Path path;

  @BeforeEach
  void setup() {
    fs = Jimfs.newFileSystem(Configuration.unix());
    path = fs.getPath("/test.txt");
  }

  @Test
  void serialize() throws IOException {
    var transaction =
        new ExternalTransaction(
            new BigDecimal("1.11"),
            new BigDecimal("5.55"),
            new BigDecimal("9.99"),
            "DESCRIPTION",
            "REFERENCE TEXT",
            "BENEFICIARY",
            LocalDate.of(1999, 12, 31));
    var workspace = new ExternalWorkspace(List.of(transaction));

    new WorkspaceIo().store(workspace, path, StandardOpenOption.CREATE);
    var contents = whenReadFile();

    assertEquals(
        "{\"transactionList\":[{"
            + "\"balanceBefore\":1.11,"
            + "\"amount\":5.55,"
            + "\"balanceAfter\":9.99,"
            + "\"transactionDescription\":\"DESCRIPTION\","
            + "\"referenceText\":\"REFERENCE TEXT\","
            + "\"beneficiary\":\"BENEFICIARY\","
            + "\"date\":\"1999-12-31\""
            + "}]}",
        contents);
  }

  @Test
  void deserialize() throws IOException {
    Path file =
        givenFile(
            "{\"transactionList\": [{"
                + "\"balanceBefore\": \"1.11\","
                + "\"amount\": \"5.55\","
                + "\"balanceAfter\": \"9.99\","
                + "\"transactionDescription\": \"DESCRIPTION\","
                + "\"referenceText\": \"REFERENCE TEXT\","
                + "\"beneficiary\": \"BENEFICIARY\","
                + "\"date\": \"1999-12-31\""
                + "}]}");

    var workspace = new WorkspaceIo().load(file);

    assertNotNull(workspace);
    assertThat(workspace.getTransactionList(), hasSize(1));

    var transaction = workspace.getTransactionList().get(0);
    assertEquals(new BigDecimal("1.11"), transaction.getBalanceBefore());
    assertEquals(new BigDecimal("5.55"), transaction.getAmount());
    assertEquals(new BigDecimal("9.99"), transaction.getBalanceAfter());
    assertEquals("DESCRIPTION", transaction.getTransactionDescription());
    assertEquals("REFERENCE TEXT", transaction.getReferenceText());
    assertEquals("BENEFICIARY", transaction.getBeneficiary());
    assertEquals(LocalDate.of(1999, 12, 31), transaction.getDate());
  }

  private String whenReadFile() throws IOException {
    return Files.readString(path, StandardCharsets.UTF_8);
  }

  private Path givenFile(String contents) throws IOException {
    Files.write(path, List.of(contents), StandardCharsets.UTF_8);
    return path;
  }
}
