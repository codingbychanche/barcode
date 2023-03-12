package barcode;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Renders a barcode.
 * 
 * @author berthold
 *
 */
public class BarcodeRenderer {

	//
	// The canvas.
	//
	private static final int BAR_WIDTH = 3;
	private static final int BORDER_WIDTH = 10;
	private static final int LEFT_BORDER = 30;
	private static final int TEXT_Y_OFFSET=20;
	private static final int TEXT_HEIGHT=20;
	private static final int BOTTOM_BORDER = 5+TEXT_HEIGHT;
	private static final int WIDTH = BAR_WIDTH * 96 + 2 * BORDER_WIDTH + LEFT_BORDER;
	private static final int HEIGHT = WIDTH / 3 + 2 * BORDER_WIDTH;
	private static final int BAR_HEIGHT = HEIGHT - 2 * BORDER_WIDTH - BOTTOM_BORDER;

	private static int X = BORDER_WIDTH + LEFT_BORDER;
	private static int Y = BORDER_WIDTH;

	/**
	 * Draws a barcode.
	 * 
	 * @param barcodeResult A object of type {@link Result}
	 */
	public static void draw(Result barcodeResult) {
		BufferedImage barcode = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D b = barcode.createGraphics();

		Color color = new Color(255, 255, 255);
		b.setColor(color);
		b.fillRect(0, 0, WIDTH, HEIGHT);

		color = new Color(0, 0, 0);
		b.setColor(color);

		b.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		b.setFont(new Font(b.getFont().getFontName(), Font.PLAIN, TEXT_HEIGHT));

		//
		// First digit is for the type of barcode.
		//
		int digitNumber = 0;
		b.drawString(barcodeResult.getDecimalDigitAtIndex(digitNumber) + "", X - LEFT_BORDER, Y + BAR_HEIGHT + 20);

		//
		// Draw bars, each at a time...
		//
		int barWidth = BAR_WIDTH;
		int c = 0;
		digitNumber++;
		for (int i = 0; i < barcodeResult.getBarcodeBinary().length(); i++) {
			String digit = barcodeResult.getBarcodeBinary().substring(i, i + 1);
			c++;
			if (c == 7) {
				int d = barcodeResult.getDecimalDigitAtIndex(digitNumber);
				if (d != 666)
					b.drawString(d + "" + "", X, Y + BAR_HEIGHT + TEXT_Y_OFFSET);
				digitNumber++;
				c = 0;
			}
			if (digit.equals("1"))
				b.fillRect(X, Y, barWidth, BAR_HEIGHT);
			X = X + barWidth;
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
