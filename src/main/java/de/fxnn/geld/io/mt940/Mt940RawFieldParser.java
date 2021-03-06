package de.fxnn.geld.io.mt940;

import java.io.IOException;
import java.io.PushbackReader;
import java.nio.CharBuffer;

/** Consumes raw MT940 character data and parses it into {@link Mt940RawField} instances. */
public class Mt940RawFieldParser {

  private static final int BUFFER_SIZE = 10240;
  private static final char END_OF_MESSAGE = '-';
  private static final char LINE_BREAK = '\n';
  private static final char TAG_SEPARATOR = ':';
  private static final char BEGIN_OF_BLOCK = '{';
  private static final char END_OF_BLOCK = '}';

  private final PushbackReader reader;
  private final CharBuffer buf;

  public Mt940RawFieldParser(PushbackReader reader) {
    this.reader = reader;
    this.buf = CharBuffer.allocate(BUFFER_SIZE);
  }

  public boolean hasNext() throws IOException {
    var nextChar = reader.read();
    reader.unread(nextChar);
    // HINT (\n): ignores line endings on EOF, but effectively also quits on every empty line
    return nextChar >= 0 && nextChar != END_OF_MESSAGE && nextChar != LINE_BREAK;
  }

  public Mt940RawField next() throws IOException {
    int firstChar = reader.read();
    // HINT: should either be '{' or ':'

    buf.clear();
    String tag = bufferUntil(TAG_SEPARATOR).flip().toString();
    buf.clear();
    String content = bufferUntilEndOfField(firstChar).flip().toString().trim();

    return new Mt940RawField(tag, content);
  }

  private CharBuffer bufferUntilEndOfField(int firstChar) throws IOException {
    if (firstChar == BEGIN_OF_BLOCK) {
      bufferUntil(Mt940RawFieldParser.END_OF_BLOCK);
      return buf;
    }

    while (true) {
      bufferUntil(LINE_BREAK);
      int nextChar = reader.read();
      if (nextChar < 0) {
        return buf;
      }
      reader.unread(nextChar);
      if (nextChar == TAG_SEPARATOR || nextChar == END_OF_MESSAGE) {
        return buf;
      }
    }
  }

  /**
   * Reads characters into the {@link #buf} until the given {@code terminalCharacter} is read. That
   * last character is not part of the buffer.
   */
  private CharBuffer bufferUntil(char terminalCharacter) throws IOException {
    while (true) {
      int nextChar = reader.read();
      if (nextChar == terminalCharacter || nextChar < 0) {
        return buf;
      }
      buf.put((char) nextChar);
    }
  }
}
