package de.fxnn.geld.io.mt940;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class Mt940BalanceField extends SimpleMt940Field {

  private static final Pattern PATTERN = Pattern.compile("^([CD])([0-9]{6})([A-Z]{3})([0-9,]+)$");
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyMMdd");
  private static final int EARLIEST_SUPPORTED_YEAR = 1980;

  BalanceType balanceType;
  LocalDate date;
  Currency currency;
  /** Amount of microunits (i.e. cents) in the given {@link #currency}. */
  long amount;

  private Mt940BalanceField(
      String tag, BalanceType balanceType, LocalDate date, Currency currency, long amount) {
    super(tag);
    this.balanceType = balanceType;
    this.date = date;
    this.currency = currency;
    this.amount = amount;
  }

  public static Mt940BalanceField of(Mt940RawField rawField) {
    var matcher = PATTERN.matcher(rawField.getRawContent());
    if (!matcher.matches()) {
      throw new IllegalArgumentException("No valid balance: '" + rawField.getRawContent() + "'");
    }

    var balanceType = "C".equals(matcher.group(1)) ? BalanceType.CREDIT : BalanceType.DEBIT;
    LocalDate date = DATE_TIME_FORMATTER.parse(matcher.group(2), LocalDate::from);
    if (date.getYear() >= EARLIEST_SUPPORTED_YEAR + 100) {
      date = date.minusYears(100);
    }
    Currency currency = Currency.getInstance(matcher.group(3));
    var amountString = matcher.group(4).replace(",", "");
    var amount = Long.valueOf(amountString);

    return new Mt940BalanceField(rawField.getTag(), balanceType, date, currency, amount);
  }

  enum BalanceType {
    CREDIT,
    DEBIT
  }
}
