package de.fxnn.geld.jfx.model;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilterParser {

  public Predicate<TransactionModel> parse(String filterExpression) {
    if (isNullOrEmpty(filterExpression)) {
      return t -> true;
    }

    List<String> lowerCaseCriteria =
        Stream.of(filterExpression.split("\\s+"))
            .map(String::toLowerCase)
            .collect(Collectors.toList());
    return t -> t.containsAnyLowerCase(lowerCaseCriteria);
  }
}
