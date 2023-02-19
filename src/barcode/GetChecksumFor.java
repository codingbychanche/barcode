package barcode;

/**
 * Methods to calculate teh various checksums for EAN barcodes...
 * 
 * @author Berthold
 *
 */
public class GetChecksumFor {

	/**
	 * Checksum for an EAN-13 barcode
	 * 
	 * @param digits EAN- 13 barcode
	 * @return true if a valid EAN- 13 barcode was passed. False if a non valid
	 *         Barcode was passed.
	 */
	public static boolean isbn(int[] digits) {
		int check = digits[Decode.LENGTH_OF_EAN_13 - 1];
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

		int checkSum =(sum+digits[Decode.INDEX_OF_CHECK_DIGIT]) % 10; 
		
		if (checkSum ==0)
			return true;
		else
			return false;
	}
}
