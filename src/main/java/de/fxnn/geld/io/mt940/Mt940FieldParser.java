package de.fxnn.geld.io.mt940;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class Mt940FieldParser {

  private final Mt940RawFieldParser inputParser;
  private Mt940RawFieldParser textBlockParser;

  public Mt940FieldParser(Reader reader) {
    this.inputParser = new Mt940RawFieldParser(reader);
  }

  public boolean hasNext() throws IOException {
    return hasNextInTextBlock() || inputParser.hasNext();
  }

  public Mt940Field next() throws IOException {
    if (hasNextInTextBlock()) {
      return map(textBlockParser.next());
    }

    return map(inputParser.next());
  }

  private Mt940Field map(Mt940RawField rawField) throws IOException {
    var optionalType = Mt940FieldType.ofTag(rawField.getTag());
    if (optionalType.isEmpty()) {
      return new SimpleMt940Field(rawField.getTag());
    }

    var type = optionalType.get();
    if (Mt940FieldType.TEXT_BLOCK.equals(type)) {
      if (textBlockParser != null) {
        throw new IllegalStateException("No nested text blocks allowed");
      }
      textBlockParser = new Mt940RawFieldParser(new StringReader(rawField.getRawContent()));
      return next();
    }

    return type.create(rawField);
  }

  private boolean hasNextInTextBlock() throws IOException {
    if (textBlockParser != null) {
      if (textBlockParser.hasNext()) {
        return true;
      }
      textBlockParser = null;
    }

    return false;
  }
}
