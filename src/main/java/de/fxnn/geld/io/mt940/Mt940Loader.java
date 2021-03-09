package de.fxnn.geld.io.mt940;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.util.stream.Stream;

public class Mt940Loader {

  public Stream<Mt940Message> loadTransactionModels(File file) throws IOException {
    var result = Stream.<Mt940Message>builder();

    try (var reader =
        new LineNumberReader(new InputStreamReader(Files.newInputStream(file.toPath())))) {
      var parser = new Mt940MessageParser(reader);
      try {
        while (parser.hasNext()) {
          result.add(parser.next());
        }
      } catch (Exception e) {
        throw new IOException("error at line %d" + reader.getLineNumber(), e);
      }
    } catch (IOException e) {
      throw new IOException("could not read the file '" + file + "'", e);
    }

    return result.build();
  }
}
