package de.fxnn.geld.io.mt940;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Value;

/** Note that the contents of this field seem to depend on the country. */
@Value
@EqualsAndHashCode(callSuper = true)
public class Mt940InformationField extends SimpleMt940Field {

  private static final Pattern GVC_PATTERN = Pattern.compile("(\\d{3}).*");
  private static final Pattern SUBFIELD_PATTERN = Pattern.compile("\\?(\\d{2})([^?]+)");
  private static final Pattern SEPA_FIELD_PREFIX_PATTERN = Pattern.compile("([A-Z]{4})\\+(.*)");
  private static final String SUBFIELD_TRANSACTION_DESCRIPTION = "00";
  private static final String SUBFIELD_BENEFICIARY_BANK_CODE = "30";
  private static final String SUBFIELD_BENEFICIARY_ACCOUNT_NUMBER = "31";
  private static final String SUBFIELD_IBAN = "38";
  public static final int FIRST_REFERENCE_TEXT_SUBFIELD = 20;
  public static final int LAST_REFERENCE_TEXT_SUBFIELD = 29;

  /**
   * Code for the type of transaction.
   *
   * @see <a
   *     href="https://www.hettwer-beratung.de/business-portfolio/zahlungsverkehr/elektr-kontoinformationen-swift-mt-940/">Fachwissen
   *     Zahlungsverkehr - Elektronische Kontoinformationen via SWIFT Format MT 940</a>
   */
  String gvcCode;

  Map<String, String> subfieldContents;
  Map<Mt940SepaSubfield, String> sepaFieldContents;

  private Mt940InformationField(
      String tag,
      String gvcCode,
      Map<String, String> subfieldContents,
      Map<Mt940SepaSubfield, String> sepaFieldContents) {
    super(tag);
    this.gvcCode = gvcCode;
    this.subfieldContents = subfieldContents;
    this.sepaFieldContents = sepaFieldContents;
  }

  public boolean isDomesticTransfer() {
    return gvcCode.charAt(0) == '0';
  }

  public boolean isSepaTransfer() {
    return gvcCode.charAt(0) == '1';
  }

  public boolean isInternationalTransfer() {
    return gvcCode.charAt(0) == '2';
  }

  public boolean isUnstructuredContent() {
    return gvcCode.charAt(0) == '9';
  }

  public String getTransactionDescription() {
    return getSubfieldContents(SUBFIELD_TRANSACTION_DESCRIPTION);
  }

  public String getReferenceText() {
    return getJoinedSubfieldContents(FIRST_REFERENCE_TEXT_SUBFIELD, LAST_REFERENCE_TEXT_SUBFIELD);
  }

  public String getBeneficiaryBankCode() {
    return getSubfieldContents(SUBFIELD_BENEFICIARY_BANK_CODE);
  }

  public String getBeneficiaryAccountNumber() {
    return getSubfieldContents(SUBFIELD_BENEFICIARY_ACCOUNT_NUMBER);
  }

  public String getBeneficiaryName() {
    return getJoinedSubfieldContents(32, 33);
  }

  public String getIban() {
    return getSubfieldContents(SUBFIELD_IBAN);
  }

  public String getSepaEndToEndReference() {
    return getSepaFieldContents(Mt940SepaSubfield.END_TO_END_REFERENCE);
  }

  public String getSepaCustomerReference() {
    return getSepaFieldContents(Mt940SepaSubfield.CUSTOMER_REFERENCE);
  }

  public String getSepaMandateReference() {
    return getSepaFieldContents(Mt940SepaSubfield.MANDATE_REFERENCE);
  }

  public String getSepaCreditorId() {
    return getSepaFieldContents(Mt940SepaSubfield.CREDITOR_ID);
  }

  public String getSepaDebitorId() {
    return getSepaFieldContents(Mt940SepaSubfield.DEBITOR_ID);
  }

  public String getSepaReferenceText() {
    return getSepaFieldContents(Mt940SepaSubfield.REFERENCE_TEXT);
  }

  public String getSepaUltimatePrincipal() {
    return getSepaFieldContents(Mt940SepaSubfield.ULTIMATE_PRINCIPAL);
  }

  public String getSepaUltimateBeneficiary() {
    return getSepaFieldContents(Mt940SepaSubfield.ULTIMATE_BENEFICIARY);
  }

  private String getJoinedSubfieldContents(int firstSubfield, int lastSubfield) {
    var result = new StringBuilder();
    for (int subfield = firstSubfield; subfield <= lastSubfield; subfield++) {
      var contents = getSubfieldContents(Integer.toString(subfield));
      if (contents != null) {
        // HINT (" "): sometimes, it seems like there should be a space, and sometimes not. So for
        //   sake of clarity: better a superfluous space than a missing one.
        result.append(contents.trim()).append(" ");
      }
    }

    if (result.length() == 0) {
      return null;
    }

    return result.toString().trim();
  }

  private String getSubfieldContents(String subfieldNumber) {
    return subfieldContents.get(subfieldNumber);
  }

  private String getSepaFieldContents(Mt940SepaSubfield sepaField) {
    return sepaFieldContents.get(sepaField);
  }

  public static Mt940InformationField of(Mt940RawField rawField) {
    String gvcCode = parseGvcCode(rawField);

    var subfieldContents = parseSubfieldContents(rawField);
    var sepaFieldContents = parseSepaFieldContents(subfieldContents);

    return new Mt940InformationField(
        rawField.getTag(), gvcCode, subfieldContents, sepaFieldContents);
  }

  private static Map<Mt940SepaSubfield, String> parseSepaFieldContents(
      Map<String, String> subfieldContents) {
    var sepaFieldContents = new HashMap<Mt940SepaSubfield, String>();
    Mt940SepaSubfield sepaField = null;
    for (int subfield = FIRST_REFERENCE_TEXT_SUBFIELD;
        subfield <= LAST_REFERENCE_TEXT_SUBFIELD;
        subfield++) {
      var contents = subfieldContents.get(Integer.toString(subfield));
      if (contents == null) {
        sepaField = null;
        continue;
      }

      var matcher = SEPA_FIELD_PREFIX_PATTERN.matcher(contents);
      if (matcher.matches()) {
        var prefix = matcher.group(1);
        var rest = matcher.group(2);
        sepaField = Mt940SepaSubfield.ofPrefix(prefix).orElse(null);
        if (sepaField != null) {
          sepaFieldContents.put(sepaField, rest);
          continue;
        }
      }

      if (sepaField != null) {
        var previous = sepaFieldContents.get(sepaField);
        sepaFieldContents.put(sepaField, previous + contents);
      }
    }
    return sepaFieldContents;
  }

  private static Map<String, String> parseSubfieldContents(Mt940RawField rawField) {
    var subfieldContents = new HashMap<String, String>();
    var subfieldMatcher = SUBFIELD_PATTERN.matcher(rawField.getRawContent());
    while (subfieldMatcher.find()) {
      var code = subfieldMatcher.group(1);
      var contents = subfieldMatcher.group(2);
      subfieldContents.put(code, contents);
    }
    return subfieldContents;
  }

  private static String parseGvcCode(Mt940RawField rawField) {
    var gvcMatcher = GVC_PATTERN.matcher(rawField.getRawContent());
    if (!gvcMatcher.matches()) {
      throw new IllegalArgumentException(
          "No valid field contents: '" + rawField.getRawContent() + "'");
    }
    return gvcMatcher.group(1);
  }
}
