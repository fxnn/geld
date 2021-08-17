package de.fxnn.geld.io.mt940;

public final class Mt940Constants {

  /** Buffer used to read MT940 fields and blocks. */
  public static final int BUFFER_SIZE = 10240;
  /**
   * Separates messages, when it's the only non-whitespace character in a line. Otherwise is allowed
   * inside a message.
   */
  public static final char END_OF_MESSAGE = '-';

  public static final char LINE_BREAK = '\n';
  public static final char TAG_SEPARATOR = ':';
  public static final char BEGIN_OF_BLOCK = '{';
  public static final char END_OF_BLOCK = '}';

  private Mt940Constants() {
    // don't instantiate me
  }
}
