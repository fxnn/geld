package de.fxnn.geld.io.mt940;

import de.fxnn.geld.io.mt940.Mt940Message.Transaction;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.function.Predicate;

public class Mt940MessageParser {

  private final PushbackReader reader;
  private final Mt940FieldParser fieldParser;
  private long serialTransactionImportNumber = 0L;

  public Mt940MessageParser(Reader reader) {
    this.fieldParser = new Mt940FieldParser(reader);
    this.reader = fieldParser.getPushbackReader();
  }

  public boolean hasNext() throws IOException {
    skipAllCharsThat(c -> c == Mt940Constants.END_OF_MESSAGE || Character.isWhitespace(c));
    return fieldParser.hasNext();
  }

  private void skipAllCharsThat(Predicate<Integer> characterPredicate) throws IOException {
    int recentChar;
    do {
      recentChar = reader.read();
    } while (characterPredicate.test(recentChar));
    if (recentChar > -1) {
      reader.unread(recentChar);
    }
  }

  public Mt940Message next() throws IOException {
    var message = new Mt940Message();
    Transaction transaction = null;
    while (fieldParser.hasNext()) {
      var field = fieldParser.next();
      if (field.isHeaderField()) {
        message.getHeaderFields().add(field);
      }
      if (field.isTransactionBegin()) {
        transaction = new Transaction(serialTransactionImportNumber++);
        message.getTransactions().add(transaction);
      }
      if (field.isTransactionalField()) {
        if (transaction == null) {
          throw new IllegalStateException("Unexpected transactional field: " + field);
        }
        transaction.getFields().add(field);
      }
      if (field.isFooterField()) {
        message.getFooterFields().add(field);
      }
    }
    return message;
  }
}
