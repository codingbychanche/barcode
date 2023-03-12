package barcode;

/**
 * This constitutes a result standard result for each of the various methods
 * provided.
 */
public class Result {

	private String barcodeBinary; // Binary representation of the barcode.
	private String barcodeDecimal; // Decimal representation of the barcode.
	private String protocol; // Description of the result in human readable form.
	public boolean hasError; // True if no valid result could be generated.

	/**
	 * 
	 * @param barcodeBinary
	 * @param protocol
	 * @param hasError
	 */
	public Result(String barcodeBinary, String barcodeDecimal, String protocol, boolean hasError) {
		super();
		this.barcodeBinary = barcodeBinary;
		this.barcodeDecimal = barcodeDecimal;
		this.protocol = protocol;
		this.hasError = hasError;
	}

	/**
	 * Binary representation of the barcode.
	 * 
	 * @return {@link String} object containing the barcode binary.
	 */
	public String getBarcodeBinary() {
		return barcodeBinary;
	}

	/**
	 * Decimal digit of the barcode.
	 * 
	 * @param index Which digit (1th, 3th etc?).
	 * @return A decimal digit found at the specified index. If index passed does
	 *         exceed the length of the barcode, an invalid number containing more
	 *         then 1 digit is returned.
	 */
	public int getDecimalDigitAtIndex(int index) {
		if (index> barcodeDecimal.length()-1)
			return 666;
		return Integer.valueOf(barcodeDecimal.substring(index, index + 1));
	}

	/**
	 * A human readable description of the result.
	 * 
	 * @return
	 */
	public String getProtocol() {
		return protocol;
	}
}
