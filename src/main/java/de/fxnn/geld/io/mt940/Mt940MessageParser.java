package de.fxnn.geld.io.mt940;

import de.fxnn.geld.io.mt940.Mt940Message.Transaction;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

public class Mt940MessageParser {

  private final PushbackReader reader;
  private final Mt940FieldParser fieldParser;

  public Mt940MessageParser(Reader reader) {
    this.reader = new PushbackReader(reader);
    this.fieldParser = new Mt940FieldParser(this.reader);
  }

  public boolean hasNext() throws IOException {
    int recentChar;
    do {
      recentChar = reader.read();
    } while (recentChar == '-' || Character.isWhitespace(recentChar));
    if (recentChar > -1) {
      reader.unread(recentChar);
    }

    return fieldParser.hasNext();
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
        transaction = new Transaction();
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
