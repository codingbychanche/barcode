package barcode;

/**
 * Collection of constants related to generic barcodes.
 * 
 * @author berthold
 *
 */
public class Barcode {

	public static final boolean ERROR = true;
	public static final boolean NO_ERROR = false;
	
	public static final int ISBN = 9;
	public static final String SCEME_SEQUENCE_ISBN = "XABBABA"; // First X=> no sceme necessary....
	public static final String SCEME_SEQUENCE_TEST="XBBAABB";
	
	public static final int LENGTH_OF_EAN_13 = 13;
	public static final int ONE_MODULE_EQUALS_7_BARS = 7;
	public static final int NUM_OF_MODULES_OF_EAN = 95;
	public static final int NUM_OF_DATA_MODULES = 42;
	public static final int NUM_OF_START_END_BARS = 3;
	public static final int NUM_OF_CENTER_BARS = 5;
	public static final int INDEX_OF_CHECK_DIGIT = 12;


}
