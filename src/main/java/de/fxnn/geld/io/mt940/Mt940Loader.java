package de.fxnn.geld.io.mt940;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Mt940Loader {

  public Stream<Mt940Message> loadTransactionModels(Path path) throws IOException {
    var result = Stream.<Mt940Message>builder();

    try (var reader = new LineNumberReader(new InputStreamReader(Files.newInputStream(path)))) {
      var parser = new Mt940MessageParser(reader);
      try {
        while (parser.hasNext()) {
          result.add(parser.next());
        }
      } catch (Exception e) {
        throw new IOException("error at line %d" + reader.getLineNumber(), e);
      }
    } catch (IOException e) {
      throw new IOException("could not read the file '" + path + "'", e);
    }

    return result.build();
  }
}
