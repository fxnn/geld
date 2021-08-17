package de.fxnn.geld.io.mt940;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.nio.CharBuffer;
import lombok.extern.slf4j.Slf4j;

/** Consumes raw MT940 character data and parses it into {@link Mt940RawField} instances. */
@Slf4j
public class Mt940RawFieldParser {

  /** How many characters the current code wants to look ahead. */
  private static final int LOOKAHEAD_BUFFER_SIZE = 2;

  private final PushbackReader reader;
  /** Buffer used to read fields and blocks. */
  private final CharBuffer buf;

  private final int[] lookaheadBuffer = new int[LOOKAHEAD_BUFFER_SIZE];
  /**
   * Number of characters we currently looked ahead and probably want to {@link
   * PushbackReader#unread(int)} again.
   */
  private int currentLookaheadCount = 0;

  public Mt940RawFieldParser(Reader reader) {
    this.reader = new PushbackReader(reader, LOOKAHEAD_BUFFER_SIZE);
    this.buf = CharBuffer.allocate(Mt940Constants.BUFFER_SIZE);
  }

  public PushbackReader getPushbackReader() {
    return reader;
  }

  public boolean hasNext() throws IOException {
    var nextChar = lookOneCharacterAhead();
    unreadAllLookaheadCharacters();
    // HINT (\n): ignores line endings on EOF, but effectively also quits on every empty line
    return nextChar >= 0
        && nextChar != Mt940Constants.END_OF_MESSAGE
        && nextChar != Mt940Constants.LINE_BREAK;
  }

  private int lookOneCharacterAhead() throws IOException {
    int bufferIdx = currentLookaheadCount;
    lookaheadBuffer[bufferIdx] = reader.read();
    if (lookaheadBuffer[bufferIdx] > -1) {
      currentLookaheadCount++;
    }

    return lookaheadBuffer[bufferIdx];
  }

  private void unreadAllLookaheadCharacters() throws IOException {
    while (currentLookaheadCount > 0) {
      currentLookaheadCount--;
      reader.unread(lookaheadBuffer[currentLookaheadCount]);
    }
  }

  public Mt940RawField next() throws IOException {
    int firstChar = reader.read();
    // HINT: should either be '{' or ':'

    buf.clear();
    String tag = bufferUntil(Mt940Constants.TAG_SEPARATOR).flip().toString();
    buf.clear();
    String content = bufferUntilEndOfField(firstChar).flip().toString().trim();

    return new Mt940RawField(tag, content);
  }

  private CharBuffer bufferUntilEndOfField(int firstChar) throws IOException {
    if (firstChar == Mt940Constants.BEGIN_OF_BLOCK) {
      bufferUntil(Mt940Constants.END_OF_BLOCK);
      return buf;
    }

    while (true) {
      bufferUntil(Mt940Constants.LINE_BREAK);
      try {
        int nextChar = lookOneCharacterAhead();
        if (nextChar < 0 || nextChar == Mt940Constants.TAG_SEPARATOR) {
          return buf;
        }
        if (nextChar == Mt940Constants.END_OF_MESSAGE) {
          nextChar = lookOneCharacterAhead();
          if (nextChar < 0 || nextChar == Mt940Constants.LINE_BREAK) {
            return buf;
          }
        }
      } finally {
        try {
          unreadAllLookaheadCharacters();
        } catch (Exception ex) {
          log.info("Failed to unread all lookahead characters", ex);
        }
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
