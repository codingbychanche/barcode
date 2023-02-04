import java.io.IOException;

import barcode.Decode;


public class Main {

	public static void main(String arghs[]) throws IOException {
		Decode.ean("perfect.png","debugImage.png",90);

	}

}
