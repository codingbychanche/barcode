import java.io.IOException;

import barcode.Decode;


public class Main {

	/**
	 * Färb areale mit anähernd gleicher Farbe.
	 * 
	 * Aktzentuiert Schrift und Barcodes in beliebigen Bildern....
	 * 
	 * @param arghs
	 * @throws IOException
	 */
	public static void main(String arghs[]) throws IOException {
		Decode.ean("perfect.png","debugImage.png", 89);

	}

}
