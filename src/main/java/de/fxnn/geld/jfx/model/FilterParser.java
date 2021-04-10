package de.fxnn.geld.jfx.model;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.Value;

public class FilterParser {

  private static final Predicate<TransactionModel> SHOW_ALL_TRANSACTIONS = t -> true;
  private static final Pattern QUOTED_STRING_PATTERN = Pattern.compile("^\"(.+)\"$");
  private static final Pattern QUOTED_SUBSTRING_SPLIT_PATTERN =
      Pattern.compile("\\s+(?=\"[^\"\\s][^\"]*\"(\\s+|$))");
  private static final Pattern WORD_SPLIT_PATTERN = Pattern.compile("\\s+");
  private static final Pattern META_CRITERION_SPLIT_PATTERN = Pattern.compile(":");

  public Predicate<TransactionModel> parse(String filterExpression) {
    if (isNullOrEmpty(filterExpression)) {
      return SHOW_ALL_TRANSACTIONS;
    }

    return Stream.of(QUOTED_SUBSTRING_SPLIT_PATTERN.split(filterExpression))
        .flatMap(this::detectSimpleAndQuotedCriteria)
        .map(this::detectMetaCriteria)
        .map(Criterion::asTransactionPredicate)
        .reduce(Predicate::or)
        .orElse(SHOW_ALL_TRANSACTIONS);
  }

  private Stream<? extends Criterion> detectSimpleAndQuotedCriteria(String part) {
    var trimmedPart = part.trim();
    var quoteMatcher = QUOTED_STRING_PATTERN.matcher(trimmedPart);
    if (quoteMatcher.matches()) {
      return Stream.of(new QuotedCriterion(quoteMatcher.group(1)));
    }
    return Stream.of(WORD_SPLIT_PATTERN.split(trimmedPart))
        .map(String::toLowerCase)
        .map(SimpleCriterion::new);
  }

  private Criterion detectMetaCriteria(Criterion criterion) {
    if (criterion instanceof SimpleCriterion) {
      var parts = META_CRITERION_SPLIT_PATTERN.split(criterion.getSourceExpression(), 2);
      if (parts.length == 2) {
        return new MetaCriterion(parts[0], parts[1]);
      }
    }

    return criterion;
  }

  private interface Criterion {
    String getSourceExpression();

    Predicate<TransactionModel> asTransactionPredicate();
  }

  @Value
  private static class SimpleCriterion implements Criterion {
    String sourceExpression;

    @Override
    public Predicate<TransactionModel> asTransactionPredicate() {
      return t -> t.containsLowerCaseWord(sourceExpression);
    }
  }

  @Value
  private static class QuotedCriterion implements Criterion {
    String sourceExpression;

    @Override
    public Predicate<TransactionModel> asTransactionPredicate() {
      return t -> t.containsSubString(sourceExpression);
    }
  }

  @Value
  private static class MetaCriterion implements Criterion {
    String keyExpression;
    String valueExpression;

    @Override
    public String getSourceExpression() {
      return keyExpression + ":" + valueExpression;
    }

    @Override
    public Predicate<TransactionModel> asTransactionPredicate() {
      if ("without".equalsIgnoreCase(keyExpression)) {
        if ("category".equalsIgnoreCase(valueExpression)) {
          return t -> null == t.getCategoryModel();
        }
      }
      throw new IllegalArgumentException(
          "Cannot understand filter expression '" + getSourceExpression() + "'");
    }
  }
}
