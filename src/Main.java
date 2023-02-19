import java.io.IOException;

import barcode.Decode;
import barcode.GetChecksumFor;


public class Main {

	public static void main(String arghs[]) throws IOException {
	int [] decodedDigits=new int [Decode.LENGTH_OF_EAN_13];
	
	for (int i=100;i<=200;i++) {
		decodedDigits=Decode.ean("perfect.png","debugImage.png",i);
		
		if (GetChecksumFor.isbn(decodedDigits))
			break;
		
	}
		for (int i=0;i<decodedDigits.length;i++)
			System.out.print(decodedDigits[i]+" ");
		
		System.out.println();
		System.out.println("Check=>"+GetChecksumFor.isbn(decodedDigits));
		

	}

}
