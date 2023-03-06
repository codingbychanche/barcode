package barcode;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BarcodeRenderer {
	
private static final int WIDTH=1500;
private static final int HEIGHT=100;

	public static void draw (String barcodeBinary) {
		BufferedImage barcode = new BufferedImage(WIDTH,HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D b = barcode.createGraphics();

		Color color = new Color(0, 0, 0);
		b.setColor(color);
		
		int x=20;
		int barWidth=8;
		for (int i=0;i<barcodeBinary.length();i++) {
			String digit=barcodeBinary.substring(i,i+1);
			System.out.print(digit);
			if (digit.equals("1"))
				b.fillRect (x, 10, barWidth, HEIGHT-20);
			x=x+barWidth;
		}
		
		File outputfile = new File("generated_barcode.png");
		try {
			ImageIO.write(barcode, "png", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
