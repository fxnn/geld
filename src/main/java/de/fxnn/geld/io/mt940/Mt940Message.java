package de.fxnn.geld.io.mt940;

import java.util.ArrayList;
import java.util.List;
import lombok.Value;

@Value
public class Mt940Message {

  List<Mt940Field> headerFields = new ArrayList<>();
  List<Transaction> transactions = new ArrayList<>();
  List<Mt940Field> footerFields = new ArrayList<>();

  public <T extends Mt940Field> T getHeader(Class<T> fieldClass) {
    return findField(headerFields, fieldClass);
  }

  public <T extends Mt940Field> T getFooter(Class<T> fieldClass) {
    return findField(footerFields, fieldClass);
  }

  private static <T extends Mt940Field> T findField(List<Mt940Field> fields, Class<T> fieldClass) {
    for (Mt940Field f : fields) {
      if (fieldClass.isInstance(f)) {
        return fieldClass.cast(f);
      }
    }
    throw new IllegalArgumentException("No field of type " + fieldClass + " amongst " + fields);
  }

  @Value
  public static class Transaction {
    List<Mt940Field> fields = new ArrayList<>();

    public Mt940TransactionField getTransactionField() {
      return findField(fields, Mt940TransactionField.class);
    }

    public Mt940InformationField getInformationField() {
      return findField(fields, Mt940InformationField.class);
    }
  }
}
