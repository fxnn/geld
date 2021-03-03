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
  private static final String SUBFIELD_TRANSACTION_DESCRIPTION = "00";
  private static final String SUBFIELD_BENEFICIARY_BANK_CODE = "30";
  private static final String SUBFIELD_BENEFICIARY_ACCOUNT_NUMBER = "31";
  private static final String SUBFIELD_IBAN = "38";
  private static final String SEPA_END_TO_END_REFERENCE = "EREF+";
  private static final String SEPA_CUSTOMER_REFERENCE = "KREF+";
  private static final String SEPA_MANDATE_REFERENCE = "MREF+";
  private static final String SEPA_CREDITOR_ID = "CRED+";
  private static final String SEPA_DEBITOR_ID = "DEBT+";
  private static final String SEPA_REFERENCE_TEXT = "SVWZ+";
  private static final String SEPA_PAYER = "ABWA+";
  private static final String SEPA_BENEFICIARY = "ABWE+";

  /**
   * Code for the type of transaction.
   *
   * @see <a
   *     href="https://www.hettwer-beratung.de/business-portfolio/zahlungsverkehr/elektr-kontoinformationen-swift-mt-940/">Fachwissen
   *     Zahlungsverkehr - Elektronische Kontoinformationen via SWIFT Format MT 940</a>
   */
  String gvcCode;

  Map<String, String> subfieldContents;

  private Mt940InformationField(String tag, String gvcCode, Map<String, String> subfieldContents) {
    super(tag);
    this.gvcCode = gvcCode;
    this.subfieldContents = subfieldContents;
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
    return getJoinedSubfieldContents(20, 29);
  }

  public String getBeneficiaryBankCode() {
    return getSubfieldContents(SUBFIELD_BENEFICIARY_BANK_CODE);
  }

  public String getBeneficiaryAccountNumber() {
    if (isDomesticTransfer()) {
      return getSubfieldContents(SUBFIELD_BENEFICIARY_ACCOUNT_NUMBER);
    }
    return null;
  }

  public String getBeneficiaryName() {
    if (isDomesticTransfer()) {
      return getJoinedSubfieldContents(32, 33);
    }
    return null;
  }

  public String getIban() {
    return getSubfieldContents(SUBFIELD_IBAN);
  }

  public String getSepaEndToEndReference() {
    return null;
  }

  public String getSepaMandateReference() {
    return null;
  }

  public String getSepaCreditorId() {
    return null;
  }

  public String getSepaReferenceText() {
    return null;
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

  public static Mt940InformationField of(Mt940RawField rawField) {
    var gvcMatcher = GVC_PATTERN.matcher(rawField.getRawContent());
    if (!gvcMatcher.matches()) {
      throw new IllegalArgumentException(
          "No valid field contents: '" + rawField.getRawContent() + "'");
    }
    var gvcCode = gvcMatcher.group(1);

    var subfieldContents = new HashMap<String, String>();
    var subfieldMatcher = SUBFIELD_PATTERN.matcher(rawField.getRawContent());
    while (subfieldMatcher.find()) {
      var code = subfieldMatcher.group(1);
      var contents = subfieldMatcher.group(2);
      subfieldContents.put(code, contents);
    }

    return new Mt940InformationField(rawField.getTag(), gvcCode, subfieldContents);
  }
}
