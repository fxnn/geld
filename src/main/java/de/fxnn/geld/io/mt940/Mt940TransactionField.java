package de.fxnn.geld.io.mt940;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class Mt940TransactionField extends SimpleMt940Field {

  private static final Pattern PATTERN =
      Pattern.compile(
          "^(\\d{2})(\\d{4})(\\d{4})"
              + "(C|D|RC|RD)"
              + "[A-Z]?" // Currency, the last character of the ISO4217 code
              + "([0-9,]{1,15})"
              + "[A-Z][A-Z0-9]{3}" // "Transaction Code", type of transaction, seemingly
              // bank-specific
              + "([^/]{0,16})(?://.{16})?" // References for Account Owner and Bank
              + "(.*)$");
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      new Mt940DateTimeFormatterFactory().create();
  private static final Map<String, FundsCode> FUNDS_CODE_MAP =
      Map.of(
          "C",
          FundsCode.CREDIT,
          "RC",
          FundsCode.REVERSAL_OF_CREDIT,
          "D",
          FundsCode.DEBIT,
          "RD",
          FundsCode.REVERSAL_OF_DEBIT);
  private static final String DEFAULT_REFERENCE = "NONREF";

  LocalDate date;
  LocalDate entryDate;
  @Nullable FundsCode fundsCode;
  /** Amount given in hundredths of the currency (e.g. cents). */
  long amount;

  @Nullable String reference;
  @Nullable String description;

  private Mt940TransactionField(
      String tag,
      LocalDate date,
      LocalDate entryDate,
      @Nullable FundsCode fundsCode,
      long amount,
      @Nullable String reference,
      @Nullable String description) {
    super(tag);
    this.date = date;
    this.entryDate = entryDate;
    this.fundsCode = fundsCode;
    this.amount = amount;
    this.reference = reference;
    this.description = description;
  }

  public BigDecimal getAmountAsBigDecimal() {
    var result = new BigDecimal(amount).scaleByPowerOfTen(-2);
    if (FundsCode.DEBIT == fundsCode || FundsCode.REVERSAL_OF_DEBIT == fundsCode) {
      result = result.negate();
    }
    return result;
  }

  public static Mt940TransactionField of(Mt940RawField rawField) {
    var matcher = PATTERN.matcher(rawField.getRawContent());
    if (!matcher.matches()) {
      throw new IllegalArgumentException(
          "No valid transaction: '" + rawField.getRawContent() + "'");
    }

    var yearString = matcher.group(1);
    var date = DATE_TIME_FORMATTER.parse(yearString + matcher.group(2), LocalDate::from);
    var entryDate = DATE_TIME_FORMATTER.parse(yearString + matcher.group(3), LocalDate::from);
    if (entryDate.isBefore(date)) {
      entryDate = entryDate.plusYears(1);
    }
    var fundsCodeString = matcher.group(4);
    // HINT: we tolerate a <null> value here for robustness (some banks use non-standard values)
    var fundsCode = FUNDS_CODE_MAP.get(fundsCodeString);
    var amountString = matcher.group(5).replace(",", "");
    var amount = Long.parseLong(amountString);
    var reference = matcher.group(6);
    if (DEFAULT_REFERENCE.equals(reference)) {
      reference = null;
    }
    var description = matcher.group(7);

    return new Mt940TransactionField(
        rawField.getTag(), date, entryDate, fundsCode, amount, reference, description);
  }

  enum FundsCode {
    CREDIT,
    REVERSAL_OF_CREDIT,
    DEBIT,
    REVERSAL_OF_DEBIT
  }
}
