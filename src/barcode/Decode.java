package barcode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Decode {

	//
	// Consatnts reffering to an EAN/ ISBN 13 Code
	//
	private static final int ONE_MODULE_EQUALS_7_BARS = 7;
	private static final int NUM_OF_MODULES_OF_EAN = 95;
	private static final int NUM_OF_DATA_MODULES = 42;
	private static final int NUM_OF_START_END_BARS = 3;
	private static final int NUM_OF_CENTER_BARS = 5;

	/**
	 * This decodes an EAN/ ISBN_ 13 Barcode...
	 * 
	 * @param fileName
	 * @param outputFile
	 * @param barcodeVerticalpos
	 * @return
	 * @throws IOException
	 */

	public static String ean(String fileName, String outputFile, int barcodeVerticalpos) throws IOException {

		long startTime = System.currentTimeMillis();

		StringBuilder protocol = new StringBuilder();

		protocol.append("Reading barcode image:" + fileName + "\n");

		File file = new File(fileName);
		BufferedImage inputImage = ImageIO.read(file);

		int width = inputImage.getWidth();
		int height = inputImage.getHeight();

		protocol.append("Image read. Height=" + height + " Width=" + width + " ["
				+ (System.currentTimeMillis() - startTime) + "ms]\n");

		float normSum = 0;

		//
		// Eingabe lesen, jedem Bildpunkt einen monocromen
		// helligkeitswert zuweisen und die Standartabweichung über
		// die r,g, und b Anteile bilden...
		//
		for (int y = 1; y < height; y++) {
			for (int x = 1; x < width; x++) {
				int clr = inputImage.getRGB(x, y);
				float norm = getAvgCol(x, y, clr);
				normSum = normSum + norm;
			}
		}

		//
		// der Durchschnitt aller Standartabweichungen
		// über alle Bildpunkte.....
		//
		float avgNormSum = normSum / (width * height);
		protocol.append("Image was analyzed. Avarage luminance=" + avgNormSum + " ["
				+ (System.currentTimeMillis() - startTime) + "ms]\n");
		protocol.append("Creating optimized image for decoding\n");

		//
		// Create output
		// Es werden nur jene Bildpunkte ausgegeben deren
		// Standartabweichung über die Helligkeit, gebilded aus
		// den r,g,b - Werten, über dem Durchnittswert gebildet
		// aus allen Bildpunkten liegt.
		//
		BufferedImage imageToDecode = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = imageToDecode.createGraphics();

		Color color = new Color(150, 0, 0);
		graphics.setColor(color);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int clr = inputImage.getRGB(x, y);

				float norm = getAvgCol(x, y, clr);
				if (norm <= avgNormSum) {
					graphics.drawLine(x, y, x, y);
				}
			}
		}
		protocol.append("Optimized image created" + " [" + (System.currentTimeMillis() - startTime) + "ms]\n");

		//
		// Vertikale pos. ab der der barcode gelesen wird markieren.....
		//
		graphics.setColor(new Color(0, 255, 0));// Zum markieren der Mitte, des Anfangs und des Endes des Codes...
		graphics.drawLine(0, barcodeVerticalpos + 2, width, barcodeVerticalpos + 2);

		//
		// ISBN 13 => 13 Ziffern
		//
		// Schwarze Balken und Weisse Balken jeweils 4 unterschiedliche Breiten.
		// Dünner Schwarzer Balken=1 // Dünner weisser Balken=0
		//
		protocol.append("Reading barcode. y=" + barcodeVerticalpos + "\n");

		int rawBarcodeData[] = new int[width + 5]; // Rohe Bilddaten binary

		//
		// Eine Zeile des barcodes lesen
		//
		// Wir habenn nun die Rohdaten. das heist der beginn wäre nur dann als
		// 101 erkennbarm, wenn die Breite eines Balkens genau ein Pixel betrüge....
		//
		//
		for (int x = 0; x < width; x = x + 1) {
			int pixel = imageToDecode.getRGB(x, barcodeVerticalpos);
			if (pixel == 0) {
				rawBarcodeData[x] = 0;
			} else {
				rawBarcodeData[x] = 1;
			}
		}

		int xStart = 0, xEnd = 0;
		boolean noValidStartFound = true;

		protocol.append("Searching for horizontal start of barcode" + " [" + (System.currentTimeMillis() - startTime)
				+ "ms]\n");

		// while (noValidStartFound && xStart <= width) {

		while (rawBarcodeData[xStart] != 1)
			xStart++;

		//
		// Check for '101'
		/*
		 * int n = xStart + 1; int n0 = 0, n11 = 0, n12 = 0;
		 * 
		 * while (rawBarcodeData[n++] == 1) n11++; while (rawBarcodeData[n++] == 0)
		 * n0++; while (rawBarcodeData[n++] == 1) n12++;
		 * 
		 * if (n0 == n11 && n0 == n12) noValidStartFound = false; else xStart++; }
		 */

		graphics.drawLine(xStart, 0, xStart, height);

		protocol.append("possible horizontal start found at x=" + xStart + " ["
				+ (System.currentTimeMillis() - startTime) + "ms]\n");

		//
		// Die Breite des ersten Balkens bestimmt die Midestbreite für eine 0
		// oder eine 1. Diese Ziffern werden "Module" genannt.
		//
		protocol.append("Determining min- width of one digit of the barcode and the width of one module\n");

		xEnd = xStart;
		while (rawBarcodeData[xEnd] != 0)
			xEnd++;

		int minBarWidth = xEnd - xStart;
		int moduleWidth = minBarWidth * ONE_MODULE_EQUALS_7_BARS;

		protocol.append("Min- bar width for one module (digit)=" + minBarWidth + " Module width=" + moduleWidth + " ["
				+ (System.currentTimeMillis() - startTime) + "ms]\n");

		//
		// Nun bestimmen wir das Ende der aktuellen Zeile des Barcodes in der Bilddatei.
		//
		// Ein ISBN 13 Barcode besteht aus 95 Modulen. Er:
		//
		// - beginnt mit 101 
		// - Hat in der Mitte die markierung 01010 = 5 Module
		// - Ended mit 101 
		//
		// ACHTUNG: DIE BREITE DER BALKEN FÜR 1 UND NULL KÖNNEN UNTERSCHIEDLICH SEIN....
		//
		// Dieses "Framework" lässt nun Platz für 84 Module. 42 in der linken/ 42 in der
		// rechten
		// Hälfte...
		//
		// Wir markieren nun zunächst einmal die restlichen Bereiche des Barcodes...
		//
		protocol.append("Calculating width of barcode" + " [" + (System.currentTimeMillis() - startTime) + "ms]\n");

		xEnd = xStart + minBarWidth * NUM_OF_MODULES_OF_EAN;
		if (xEnd >= width) {
			protocol.append(">>>> CALCULATED END OF BARCODE IS OUTSIDE IMAGE BOUNDS....TRYING TO DECODE ANYWAY..."
					+ " [" + (System.currentTimeMillis() - startTime) + "ms]\n");
			xEnd = width;
		} else
			graphics.drawLine(xEnd, 0, xEnd, height);

		int startOfFirstHalf = xStart + NUM_OF_START_END_BARS * minBarWidth;
		if (startOfFirstHalf >= width) {
			protocol.append(
					">>>> CALCULATED START OF FIRST HALF OF BARCODE IS OUTSIDE IMAGE BOUNDS....WONT BE ABLE TO DECODE"
							+ " [" + (System.currentTimeMillis() - startTime) + "ms]\n");
			startOfFirstHalf = width;
		} else
			graphics.drawLine(startOfFirstHalf, 0, startOfFirstHalf, height);

		int endOfFirstHalf = startOfFirstHalf + NUM_OF_DATA_MODULES * minBarWidth;
		if (endOfFirstHalf >= width) {
			protocol.append(
					">>>> CALCULATED END OF FIRST HALF IS OUTSIDE IMAGE BOUNDS....TRYING TO DECODE ANYWAY...LETS SEE WHAT WE'VE GOT"
							+ " [" + (System.currentTimeMillis() - startTime) + "ms]\n");
			endOfFirstHalf = width;
		} else
			graphics.drawLine(endOfFirstHalf, 0, endOfFirstHalf, height);

		int startOfSecondHalf = endOfFirstHalf + NUM_OF_CENTER_BARS * minBarWidth;
		if (startOfSecondHalf >= width) {
			protocol.append(
					">>>> CALCULATED START OF SECOND HALF IS OUTSIDE IMAGE BOUNDS....TRYING TO DECODE ANYWAY...LETS SEE WHAT WE'VE GOT"
							+ " [" + (System.currentTimeMillis() - startTime) + "ms]\n");
			startOfSecondHalf = width;
		} else
			graphics.drawLine(startOfSecondHalf, 0, startOfSecondHalf, height);

		int endOfSecondHalf = startOfSecondHalf + NUM_OF_DATA_MODULES * minBarWidth;
		if (endOfSecondHalf >= width) {
			protocol.append(
					">>>> CALCULATED END OF SECOND HALF IS OUTSIDE IMAGE BOUNDS....TRYING TO DECODE ANYWAY...LETS SEE WHAT WE'VE GOT"
							+ " [" + (System.currentTimeMillis() - startTime) + "ms]\n");
			endOfSecondHalf = width;
		} else
			graphics.drawLine(endOfSecondHalf, 0, endOfSecondHalf, height);

		graphics.setColor(new Color(0, 255, 255));

		for (int x = startOfFirstHalf + moduleWidth; x < endOfFirstHalf; x = x + moduleWidth)
			graphics.drawLine(x, 0, x, height);

		for (int x = startOfSecondHalf + moduleWidth; x < endOfSecondHalf; x = x + moduleWidth)
			graphics.drawLine(x, 0, x, height);

		protocol.append("Calculated width of barcode is " + (xEnd - xStart) + " ["
				+ (System.currentTimeMillis() - startTime) + "ms]\n");

		//
		// Bevor das Decodieren beginnt, schauen wir ob die Grenzen des barcodes klar
		// erkannt werden...
		//
		//
		// Check for '101'end...
		//
		int n = endOfSecondHalf + 1;
		int n0 = 0, n11 = 0, n12 = 0;

		while (rawBarcodeData[n++] == 1 && n < width)
			n11++;
		while (rawBarcodeData[n++] == 0 && n < width)
			n0++;
		while (rawBarcodeData[n++] == 1 && n < width)
			n12++;

		if (n0 == n11 && n0 == n12)
			protocol.append("possible horizontal end found at=" + endOfSecondHalf + " ["
					+ (System.currentTimeMillis() - startTime) + "ms]\n");
		else
			protocol.append(">>>> COULD NOT FIND POSSIBLE END AT END CALCULATED " + endOfSecondHalf
					+ "....TRYING TO DECODE ANYWAY..." + " [" + (System.currentTimeMillis() - startTime) + "ms]\n");

		//
		// Ergebnis speichern.
		//
		File outputfile = new File(outputFile);
		ImageIO.write(imageToDecode, "png", outputfile);

		protocol.append("Output image written for evaluation by user:" + outputFile + " ["
				+ (System.currentTimeMillis() - startTime) + "ms]\n");

		//
		// Jede Ziffer wird mit 7 Modulen codiert...
		// Aus den Rohdaten werden diese Module nun extraiert.
		//
		protocol.append("Decoding starts......\n");

		StringBuilder module = new StringBuilder();
		int mod = ONE_MODULE_EQUALS_7_BARS;
		int digit = 0;

		// Erste Hälfte des Barcodes für ISBN, das heist, die erste Ziffer ist eine 9

		int digitNr = 0;
		for (int i = startOfFirstHalf + 1; i < endOfFirstHalf; i = i + minBarWidth) {

			module.append(rawBarcodeData[i]);

			mod--;
			if (mod == 0) {
				mod = 7;
				if (digitNr == 0)
					digit = DigitCodes.getDigitFirstH("A", module.toString());
				if (digitNr == 1)
					digit = DigitCodes.getDigitFirstH("B", module.toString());
				if (digitNr == 2)
					digit = DigitCodes.getDigitFirstH("B", module.toString());
				if (digitNr == 3)
					digit = DigitCodes.getDigitFirstH("A", module.toString());
				if (digitNr == 4)
					digit = DigitCodes.getDigitFirstH("B", module.toString());
				if (digitNr == 5)
					digit = DigitCodes.getDigitFirstH("A", module.toString());

				digitNr++;
				protocol.append(" -> " + module + "=" + digit + "\n");
				module.setLength(0);
			}
		}

		module.setLength(0);
		protocol.append("\n");

		// Zweite Hälfte des Barcodes
		for (int i = startOfSecondHalf; i < endOfSecondHalf; i = i + minBarWidth) {
			module.append(rawBarcodeData[i]);
			mod--;
			if (mod == 0) {
				mod = 7;
				digit = DigitCodes.getDigitSecondH(module.toString());
				protocol.append(" -> " + module + "=" + digit + "\n");
				module.setLength(0);
			}
		}

		//
		// Just for debuging, create a clean binary of the barcode...
		//
		protocol.append("\n\nBinary of barcode:\n");

		for (int i = xStart; i < width; i++)
			protocol.append(rawBarcodeData[i]);
		protocol.append("\n\n");

		
		//
		// Finish
		//
		protocol.append("Decoding ended." + " [" + (System.currentTimeMillis() - startTime) + "ms]\n");
		System.out.println(protocol.toString());

		return "-";
	}

	/**
	 * Liefert die Standartabweichung über die drei Werte der rot, grün und blau
	 * Anteile eines Pixels.
	 * 
	 * @param x
	 * @param y
	 * @param clr
	 * @return
	 */
	private static float getAvgCol(int x, int y, int clr) {

		//
		// Farbige Bildpunkte werden in gleichwertige Helligkeitswerte
		// eines monocromen Bildes gewandelt.
		//

		float red = ((clr & 0x00ff0000) >> 16) * 0.299f;
		float green = ((clr & 0x0000ff00) >> 8) * 0.587f;
		float blue = (clr & 0x000000ff) * 0.114f;

		//
		// Standartabweichung ermitteln.
		//

		float avg = (int) (0.299 * red + 0.114 * blue + 0.587 * green) / 3;
		return Math.abs(red - avg) + Math.abs(blue - avg) + Math.abs(green - avg);
	}
}
