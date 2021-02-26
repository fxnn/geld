package de.fxnn.geld;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Mt940Parser {

  private final PushbackReader reader;
  private final ArrayList<Mt940Field> fields;

  public Mt940Parser(Reader reader) {
    this.fields = new ArrayList<>();
    this.reader = new PushbackReader(reader);
  }

  public Mt940Field readField() throws IOException {
    var buf = CharBuffer.allocate(1024);

    int nextChar = reader.read();
    if (nextChar != '{' && nextChar != ':') {
      reader.unread(nextChar);
    }

    reader.read(buf.clear().limit(4));
    String tag = String.valueOf(buf.get(0));

    return new Mt940Field(tag);
  }

  public static List<Mt940Field> parse(String data) throws IOException {
    return List.of(new Mt940Parser(new StringReader(data)).readField());
  }
}
