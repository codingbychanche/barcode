package barcode;

/**
 * Methods to calculate and evaluate the various checksums for EAN barcodes...
 * 
 * @author Berthold
 *
 */
public class Checksum {

	/**
	 * Evaluates if the checksum for the passed ean of type isbn matches.
	 * 
	 * @param digits ean of type isbn.
	 * @return true if a valid isbn barcode was passed. False if a non valid Barcode
	 *         was passed.
	 */
	public static boolean checkForIsbn(int[] digits) {

		if (getForIsbn(digits) == digits[Decode.INDEX_OF_CHECK_DIGIT])
			return true;
		else
			return false;
	}

	/**
	 * Generates a checksum fo an ean of type ean.
	 * 
	 * @param digits The ean code for a valid isbn.
	 * @return The checksum.
	 */
	public static int getForIsbn(int[] digits) {

		int d2 = digits[1];
		int d4 = digits[3];
		int d6 = digits[5];
		int d8 = digits[7];
		int d10 = digits[9];
		int d12 = digits[11];

		int sum = d2 + d4 + d6 + d8 + d10 + d12;
		int p = 3 * sum;

		int d1 = digits[0];
		int d3 = digits[2];
		int d5 = digits[4];
		int d7 = digits[6];
		int d9 = digits[8];
		int d11 = digits[10];

		int s = d1 + d3 + d5 + d7 + d9 + d11;
		sum = s + p;

		int checksum = (int) (Math.ceil((double) sum / 10) * 10) - sum;

		return checksum;
	}
}
