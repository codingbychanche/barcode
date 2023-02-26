import java.io.IOException;

import barcode.Decode;
import barcode.Checksum;
import barcode.Encode;

public class Main {

	public static void main(String arghs[]) throws IOException {

		// Decoding demo.
		int[] decodedDigits = new int[Decode.LENGTH_OF_EAN_13];

		//for (int i = 80; i <= 90; i++) {
			decodedDigits = Decode.ean("perfect2.png", "debugImage.png", 90);

			//if (GetChecksumFor.isbn(decodedDigits))
			//	break;

		//}
		for (int i = 0; i < decodedDigits.length; i++)
			System.out.print(decodedDigits[i] + " ");

		System.out.println();
		System.out.println("Check=>" + Checksum.checkForIsbn(decodedDigits));
		
		// Encoding demo
		
		Encode.doEncoding("988009912345");

	}

}
