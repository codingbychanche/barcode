package barcode;

import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Decode {

	//
	// Consatnts reffering to an EAN/ ISBN 13 Code
	//
	public static final int INDEX_OF_PREFIX = 0; // EAN prefix. 9 for ISBN....
	

	/**
	 * Decodes an 13 Digit barcode, according to the given decoding sceme.
	 *  
	 * @param fileName Image of the barcode to be decoded.
	 * @param outputFile The file to which an imiga containing debbung information is written.
	 * @param scemeSequence One of the supported decoding scemes. See:{@link Barcode.java}
	 * @param barcodeVerticalpos Vertical start of barcode.
	 * @throws IOException
	 */

	public static int[] ean(String fileName, String outputFile, String scemeSequence,int barcodeVerticalpos) throws IOException {

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
		
		normSum=normSum/1.0f; // Je größer, desto weniger "Abfall"

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

		//
		// Start des Barcodes in der Zeile suchen....
		//
		protocol.append("Searching for horizontal start of barcode" + " [" + (System.currentTimeMillis() - startTime)
				+ "ms]\n");

		int xStart = 0, xEnd = 0;

		while (rawBarcodeData[xStart] != 1 && xStart < width)
			xStart++;

		if (CheckBoundary.startEndOfEAN(rawBarcodeData, xStart))
			protocol.append("possible horizontal start 101 idendified at x=" + xStart + " ["
					+ (System.currentTimeMillis() - startTime) + "ms]\n");
		else
			protocol.append(">>>> NO HORIZONTAL START 101 IDENDIFED... DECODING ANYWAY....["
					+ (System.currentTimeMillis() - startTime) + "ms]\n");

		graphics.drawLine(xStart, 0, xStart, height);

		//
		// Die Breite des ersten Balkens bestimmt die Midestbreite für eine 0
		// oder eine 1. Diese Ziffern werden "Module" genannt.
		//
		protocol.append("Determining min- width of one digit of the barcode and the width of one module\n");

		xEnd = xStart;
		while (rawBarcodeData[xEnd] != 0)
			xEnd++;

		int minBarWidth = xEnd - xStart;// Was wenn 0 und eins nicht gleich breit sind?
		int moduleWidth = minBarWidth * Barcode.ONE_MODULE_EQUALS_7_BARS;

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

		xEnd = xStart + minBarWidth * Barcode.NUM_OF_MODULES_OF_EAN;

		if (xEnd >= width) {
			protocol.append(">>>> CALCULATED END OF BARCODE IS OUTSIDE IMAGE BOUNDS....TRYING TO DECODE ANYWAY..."
					+ " [" + (System.currentTimeMillis() - startTime) + "ms]\n");
			xEnd = width;
		} else
			graphics.drawLine(xEnd, 0, xEnd, height);

		int startOfFirstHalf = xStart + Barcode.NUM_OF_START_END_BARS * minBarWidth;
		if (startOfFirstHalf >= width) {
			protocol.append(
					">>>> CALCULATED START OF FIRST HALF OF BARCODE IS OUTSIDE IMAGE BOUNDS....WONT BE ABLE TO DECODE"
							+ " [" + (System.currentTimeMillis() - startTime) + "ms]\n");
			startOfFirstHalf = width;
		} else
			graphics.drawLine(startOfFirstHalf, 0, startOfFirstHalf, height);

		int endOfFirstHalf = startOfFirstHalf + Barcode.NUM_OF_DATA_MODULES * minBarWidth;
		if (endOfFirstHalf >= width) {
			protocol.append(
					">>>> CALCULATED END OF FIRST HALF IS OUTSIDE IMAGE BOUNDS....TRYING TO DECODE ANYWAY...LETS SEE WHAT WE'VE GOT"
							+ " [" + (System.currentTimeMillis() - startTime) + "ms]\n");
			endOfFirstHalf = width;
		} else
			graphics.drawLine(endOfFirstHalf, 0, endOfFirstHalf, height);

		int startOfSecondHalf = endOfFirstHalf + Barcode.NUM_OF_CENTER_BARS * minBarWidth;
		if (startOfSecondHalf >= width) {
			protocol.append(
					">>>> CALCULATED START OF SECOND HALF IS OUTSIDE IMAGE BOUNDS....TRYING TO DECODE ANYWAY...LETS SEE WHAT WE'VE GOT"
							+ " [" + (System.currentTimeMillis() - startTime) + "ms]\n");
			startOfSecondHalf = width;
		} else
			graphics.drawLine(startOfSecondHalf, 0, startOfSecondHalf, height);

		int endOfSecondHalf = startOfSecondHalf + Barcode.NUM_OF_DATA_MODULES * minBarWidth;
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
		// Check for center
		//
		if (CheckBoundary.centerBarOfEAN(rawBarcodeData, endOfFirstHalf))
			protocol.append("Possible start for center bars 01010 idendified at=" + endOfFirstHalf + " ["
					+ (System.currentTimeMillis() - startTime) + "ms]\n");
		else
			protocol.append(">>>> COULD NOT IDENDIFIY CENTER BARS AT " + endOfFirstHalf
					+ "....TRYING TO DECODE ANYWAY..." + " [" + (System.currentTimeMillis() - startTime) + "ms]\n");

		//
		// Check for '101'end...
		//
		if (CheckBoundary.startEndOfEAN(rawBarcodeData, endOfSecondHalf))
			protocol.append("possible horizontal end 101 idendified at=" + endOfSecondHalf + " ["
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
		// Decodierung des Barcodes....
		//
		// Jede Ziffer wird mit 7 Modulen codiert...
		// Aus den Rohdaten werden diese Module nun extraiert.
		//
		protocol.append("Decoding starts......\n");

		int decodedDigits[] = new int[Barcode.LENGTH_OF_EAN_13];
		StringBuilder modulesToDecode = new StringBuilder();

		int mod = Barcode.ONE_MODULE_EQUALS_7_BARS;
		int digit = 0;

		// Erste Hälfte des Barcodes

		decodedDigits[INDEX_OF_PREFIX] = 9; // We assume an ISBN code...
		int digitIndex = 1;

		int digitNr = 0;

		for (int i = startOfFirstHalf + 1; i < endOfFirstHalf; i = i + minBarWidth) {

			modulesToDecode.append(rawBarcodeData[i]);

			//
			// Decode ISBN (Kennziffer=9)
			//
			// Die ersten Ziffern eines als ISBN kodierten Barcodes (erste Ziffer=9)
			// sind im Schema ABBABA kodiert.
			//
			mod--;
			if (mod == 0) {
				mod = Barcode.ONE_MODULE_EQUALS_7_BARS;
				if (digitNr == 0)
					digit = DigitCodes.getDigitFirstH(scemeSequence.substring(1,2), modulesToDecode.toString());
				if (digitNr == 1)
					digit = DigitCodes.getDigitFirstH(scemeSequence.substring(2, 3), modulesToDecode.toString());
				if (digitNr == 2)
					digit = DigitCodes.getDigitFirstH(scemeSequence.substring(3, 4), modulesToDecode.toString());
				if (digitNr == 3)
					digit = DigitCodes.getDigitFirstH(scemeSequence.substring(4, 5), modulesToDecode.toString());
				if (digitNr == 4)
					digit = DigitCodes.getDigitFirstH(scemeSequence.substring(5, 6), modulesToDecode.toString());
				if (digitNr == 5)
					digit = DigitCodes.getDigitFirstH(scemeSequence.substring(6, 7), modulesToDecode.toString());

				digitNr++;
				protocol.append(" -> " + modulesToDecode + "=" + digit + "\n");
				decodedDigits[digitIndex++] = digit;
				modulesToDecode.setLength(0);
			}
		}

		modulesToDecode.setLength(0);
		protocol.append("\n");

		// Zweite Hälfte des Barcodes

		for (int i = startOfSecondHalf; i < endOfSecondHalf; i = i + minBarWidth) {
			modulesToDecode.append(rawBarcodeData[i]);
			mod--;
			if (mod == 0) {
				mod = 7;
				digit = DigitCodes.getDigitSecondH(modulesToDecode.toString());
				protocol.append(" -> " + modulesToDecode + "=" + digit + "\n");
				decodedDigits[digitIndex++] = digit;
				modulesToDecode.setLength(0);
			}
		}

		//
		// Just for debuging, create a clean binary of the barcode...
		//
		protocol.append("\n\nBinary of barcode:\n");

		int w = 0;
		for (int i = xStart; i < width; i++) {
			protocol.append(rawBarcodeData[i]);
			w++;
			if (w == minBarWidth) {
				protocol.append(" ");
				w = 0;
			}
		}
		protocol.append("\n\n");

		//
		// Finish
		//
		protocol.append("Decoding ended." + " [" + (System.currentTimeMillis() - startTime) + "ms]\n");
		System.out.println(protocol.toString());

		return decodedDigits;
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
