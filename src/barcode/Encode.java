package barcode;

/**
 * Collection of methods to generate the binary representations of various ean
 * barcodes.
 * 
 * TODO: MOE CONSTANTS TO A SEPERATE FILE. ADD A CLASS REPRESENTING THE RESULT
 * WHICH CONTAINS THE RAW BINARY OF THE VARIOUS CODES AND THE PROTOCOL GENERATED
 * TO PROVIDE BOTH, A MACHINE AND A HUMAN READABLE FORM.....
 * 
 * @author Berthold
 *
 */
public class Encode {

	public static final int LENGTH_OF_FIRST_HALF = 6;
	public static final int LENGTH_OF_SECOND_HALF = 6;

	public static final int ISBN = 9;
	public static final String SCEME_SEQUENCE_ISBN = "XABBABA"; // First X=> no sceme necessary....

	public static final boolean ERROR = true;
	public static final boolean NO_ERROR = false;

	/**
	 * Encodes an arbitrary sequence of integers into a valid 13- digit ean code.
	 * First integer must be a valid integer for the kind of ean code to be
	 * generated (e.g. 9= ISBN). The last digit serves as the checksum and depents
	 * on the first 12 integers of the code.
	 * 
	 * @param code A valid string containing a valid sequence of 12 integers to be
	 *             encoded into an ean code.
	 * 
	 * @return A object of the type {@Link Result}.
	 */
	public static Result doEncoding(String code) {
		StringBuilder binary = new StringBuilder(); // Raw binary of the code.
		StringBuilder protocol = new StringBuilder(); // Human readable form of the encoded code.

		String scemeSequence;

		//
		// Check parameters.
		//
		if (code.length() < 12) {
			protocol.append("Invalid number of digits for type of barcode given. Aborded!\n");
			Result r = new Result("", code,protocol.toString(), ERROR);
			return r;
		}

		//
		// Check for type of code and set encoding scheme accordingly..
		//
		int firstDigit = Integer.valueOf(code.substring(0, 1));
		
		if (firstDigit == ISBN) {
			scemeSequence = SCEME_SEQUENCE_ISBN;
			protocol.append("Generating ISBN...\n\n");
		} else {
			protocol.append("No valid encoding sceme for an ean passed. Aborded.\n");
			Result r = new Result("", code,protocol.toString(), ERROR);
			return r;
		}
			
		//
		// Set left boundary.
		//
		binary.append("101");
		protocol.append("101\n");

		//
		// Encode first half of the ean code depending on
		// the known encoding sceme.
		//
		int i = 1;
		for (i = 1; i <= LENGTH_OF_FIRST_HALF; i++) {
			String s = scemeSequence.substring(i, i + 1);
			int digit = Integer.valueOf(code.substring(i, i + 1));
			String b = DigitCodes.getBinaryFirstH(digit, s);
			binary.append(b);
			protocol.append(digit + "=" + b + "\n");
		}

		//
		// Set center bars.
		//
		binary.append("01010");
		protocol.append("01010\n");

		//
		// Encode the second half. Encoding sceme her is always A
		//
		for (; i < LENGTH_OF_SECOND_HALF + 6; i++) {
			int digit = Integer.valueOf(code.substring(i, i + 1));
			String b = DigitCodes.getBinarySecondH(digit);
			binary.append(b);
			protocol.append(digit + "=" + b + "\n");
		}

		//
		// Calculate checksum and add as the last digit.
		//
		int[] d = new int[13];
		for (int j = 0; j < 12; j++)
			d[j] = Integer.valueOf(code.substring(j, j + 1));

		int checksum = Checksum.getForIsbn(d);
		String b = DigitCodes.getBinarySecondH(checksum);
		binary.append(b);
		protocol.append(checksum + "=" + b + "=Checksum\n\n");

		//
		// End, set right boundary
		//
		binary.append("101");

		protocol.append("Binary:\n" + binary);

		// 
		// Generate final result and return home...
		//
		Result r = new Result(binary.toString(), code+checksum,protocol.toString(), NO_ERROR);
		return r;
	}
}
