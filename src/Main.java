import java.io.IOException;

import barcode.Decode;
import barcode.Checksum;
import barcode.Encode;
import barcode.BarcodeRenderer;
import barcode.Result;
import barcode.Barcode;

public class Main {

	public static void main(String arghs[]) throws IOException {

		// Decoding demo.
		int[] decodedDigits = new int[Barcode.LENGTH_OF_EAN_13];

		//for (int i = 80; i <= 90; i++) {
			decodedDigits = Decode.ean("bar13.png", "debugImage.png", 150);

			//if (GetChecksumFor.isbn(decodedDigits))
			//	break;

		//}
		for (int i = 0; i < decodedDigits.length; i++)
			System.out.print(decodedDigits[i] + " ");

		System.out.println();
		System.out.println("Check=>" + Checksum.checkForIsbn(decodedDigits));
		
		// Encoding demo
		
		Result code=Encode.doEncoding("971234963710");
	
		BarcodeRenderer.draw(code);
		
		System.out.println(code.getProtocol());

	}

}
