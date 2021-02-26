package de.fxnn.geld;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class Mt940Parser {

  private static final int BUFFER_SIZE = 10240;
  private static final char END_OF_MESSAGE = '-';
  private static final char LINE_BREAK = '\n';
  private static final char TAG_SEPARATOR = ':';
  private static final char BEGIN_OF_BLOCK = '{';
  private static final char END_OF_BLOCK = '}';

  private final PushbackReader reader;
  private final CharBuffer buf;

  public Mt940Parser(Reader reader) {
    this.reader = new PushbackReader(reader);
    this.buf = CharBuffer.allocate(BUFFER_SIZE);
  }

  public boolean hasNext() throws IOException {
    var nextChar = reader.read();
    reader.unread(nextChar);
    // HINT (\n): ignores line endings on EOF, but effectively also quits on every empty line
    return nextChar >= 0 && nextChar != END_OF_MESSAGE && nextChar != LINE_BREAK;
  }

  public Mt940RawField readField() throws IOException {
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
      bufferUntil(Mt940Parser.END_OF_BLOCK);
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

  public static List<Mt940RawField> parse(String data) throws IOException {
    var parser = new Mt940Parser(new StringReader(data));
    var result = new ArrayList<Mt940RawField>();
    while (parser.hasNext()) {
      result.add(parser.readField());
    }
    return result;
  }
}
