package barcode;

/**
 * Various methods to handle en- and decoding of the
 * bit patterns of a bar code.
 * 
 * @author Berthold
 *
 */
public class DigitCodes {

	public static final int INVALID = 666;

	/**
	 * Binär modul der zweiten Hälfte des Barcodes in einen integer decodieren. Wenn
	 * ein unbekanntes Muster übergeben wird, dann wird die 666 zurückgegeben.
	 * 
	 * @param d
	 * @return
	 */
	public static int getDigitSecondH(String d) {

		if (d.equals("1110010"))
			return 0;
		if (d.equals("1100110"))
			return 1;
		if (d.equals("1101100"))
			return 2;
		if (d.equals("1000010"))
			return 3;
		if (d.equals("1011100"))
			return 4;
		if (d.equals("1001110"))
			return 5;
		if (d.equals("1010000"))
			return 6;
		if (d.equals("1000100"))
			return 7;
		if (d.equals("1001000"))
			return 8;
		if (d.equals("1110100"))
			return 9;
		return INVALID;
	}

	/**
	 * Binär Modul für die erste Hälfte des Barcodes.
	 * 
	 * @param sceme
	 * @param d
	 * @return
	 */
	public static int getDigitFirstH(String sceme, String d) {

		if (sceme.equals("A")) {
			if (d.equals("0001101"))
				return 0;
			if (d.equals("0011001"))
				return 1;
			if (d.equals("0010011"))
				return 2;
			if (d.equals("0111101"))
				return 3;
			if (d.equals("1011100"))
				return 4;
			if (d.equals("0110001"))
				return 5;
			if (d.equals("0101111"))
				return 6;
			if (d.equals("0111011"))
				return 7;
			if (d.equals("0110111"))
				return 8;
			if (d.equals("0001011"))
				return 9;
			return INVALID;
		}
		if (sceme.equals("B")) {
			if (d.equals("0100111"))
				return 0;
			if (d.equals("0110011"))
				return 1;
			if (d.equals("0011011"))
				return 2;
			if (d.equals("0100001"))
				return 3;
			if (d.equals("10011101"))
				return 4;
			if (d.equals("0111001"))
				return 5;
			if (d.equals("0000101"))
				return 6;
			if (d.equals("0010001"))
				return 7;
			if (d.equals("0001001"))
				return 8;
			if (d.equals("0010111"))
				return 9;
			return INVALID;
		}
		return INVALID;
	}
}
