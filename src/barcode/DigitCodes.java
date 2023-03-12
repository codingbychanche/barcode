package barcode;

/**
 * Various methods to handle en- and decoding of the bit patterns of a bar code.
 * 
 * @author Berthold
 *
 */
public class DigitCodes {

	public static final int INVALID = 666;
	public static final String NOT_ENCODABLE_DIGIT = "XXXXXXX";

	/**
	 * Decodes a binary digit which beleong to the second half of an ean code
	 * into a decimal digit (0-9).
	 * 
	 * @param d The binary reperesenting the decimal digit.
	 * @return The decimal digit.
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
	 * Encodes a single digit to binary for the second half of an ean- code.
	 * 
	 * @param digit The decimal digit (0-9) to be encoded.
	 * @return Binary reprentesation of the digit passed.
	 */
	public static String getBinarySecondH(int digit) {
		if (digit == 0)
			return "1110010";
		if (digit == 1)
			return "1100110";
		if (digit == 2)
			return "1101100";
		if (digit == 3)
			return "1000010";
		if (digit == 4)
			return "1011100";
		if (digit == 5)
			return "1001110";
		if (digit == 6)
			return "1010000";
		if (digit == 7)
			return "1000100";
		if (digit == 8)
			return "1001000";
		if (digit == 9)
			return "1110100";
		return NOT_ENCODABLE_DIGIT;
	}

	/**
	 * Decodes the binary digit of the first half of an ean code
	 * into its decimal representation.
	 *  
	 * @param sceme The encoding sceme.
	 * @param d The binary representation of the digit.
	 * @return The decimal representation of the digit.
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
			if (d.equals("0100011"))
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
			if (d.equals("0011101"))
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

	/**
	 * Encodes a digit of the first half of an ean code to binary.
	 * 
	 * @param digit The digit (0-9) to be encoded.
	 * @param sceme The encoding sceme for to first half of an ean code.
	 * @return The binary represantation of the digit for the first half on an ean
	 *         code depending on the encoding sceme.
	 */
	public static String getBinaryFirstH(int digit, String sceme) {

		if (sceme.equals("A")) {
			if (digit == 0)
				return "0001101";
			if (digit == 1)
				return "0011001";
			if (digit == 2)
				return "0010011";
			if (digit == 3)
				return "0111101";
			if (digit == 4)
				return "0100011";
			if (digit == 5)
				return "0110001";
			if (digit == 6)
				return "0101111";
			if (digit == 7)
				return "0111011";
			if (digit == 8)
				return "0110111";
			if (digit == 9)
				return "0001011";
			return NOT_ENCODABLE_DIGIT;
		}
		if (sceme.equals("B")) {
			if (digit == 0)
				return "0100111";
			if (digit == 1)
				return "0110011";
			if (digit == 2)
				return "0011011";
			if (digit == 3)
				return "0100001";
			if (digit == 4)
				return "0011101";
			if (digit == 5)
				return "0111001";
			if (digit == 6)
				return "0000101";
			if (digit == 7)
				return "0010001";
			if (digit == 8)
				return "0001001";
			if (digit == 9)
				return "0010111";

			return NOT_ENCODABLE_DIGIT;
		}
		return NOT_ENCODABLE_DIGIT;
	}
}
