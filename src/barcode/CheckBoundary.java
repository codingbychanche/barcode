package barcode;

/**
 * Various methods to check bondary conditions of a barcode.
 * 
 * @author Berthold
 *
 */

public class CheckBoundary {

	/**
	 * Start and end of an EAN code should be 101
	 * 
	 * @param barcode
	 * @param xStart
	 * @return True if start/ end could be idendified. False, if not.
	 */
	public static boolean startEndOfEAN(int[] barcode, int xStart) {

		int n0 = 0;
		int n11 = 0;
		int n12 = 0;
		int width = barcode.length;

		xStart++;

		while (barcode[xStart++] == 1 && xStart < width - 1)
			n11++;
		while (barcode[xStart++] == 0 && xStart < width - 1)
			n0++;
		while (barcode[xStart++] == 1 && xStart < width - 1)
			n12++;

		System.out.println("n11=" + n11 + "  n0=" + n0 + "  N12=" + n12);

		if (n0 == n11 && n0 == n12) {

			return true;
		} else
			return false;
	}

	/**
	 * The center bars of an EAN should be 01010
	 * 
	 * @param barcode
	 * @param xStart
	 * @retur True if center bars xcould be uidendified. False, if not.
	 */
	public static boolean centerBarOfEAN(int[] barcode, int xStart) {

		int n01 = 0;
		int n11 = 0;
		int n02 = 0;
		int n12 = 0;
		int n03 = 0;

		int width = barcode.length;

		xStart++;

		if (xStart < width - 1) {
			while (barcode[xStart++] == 0 && xStart < width - 1)
				n01++;
		}
		
		if (xStart < width - 1) {
			while (barcode[xStart++] == 1 && xStart < width - 1)
				n11++;
		}

		if (xStart < width - 1) {
			while (barcode[xStart++] == 0 && xStart < width - 1)
				n02++;
		}

		if (xStart < width - 1) {
			while (barcode[xStart++] == 1 && xStart < width - 1)
				n12++;
		}

		if (xStart < width - 1) {
			while (barcode[xStart++] == 0 && xStart < width - 1)
				n03++;
		}

		System.out.println("n01=" + n01 + "  n11=" + n11 + "  n02=" + n02 + "   n12=" + n12 + "  n03=" + n03);

		if (n01 == n02 && n02 == n03) {
			return true;
		} else
			return false;
	}
}
