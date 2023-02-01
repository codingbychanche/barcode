import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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

		File file = new File("perfect.png");
		BufferedImage inputImage = ImageIO.read(file);

		int width = inputImage.getWidth();
		int height = inputImage.getHeight();

		System.out.println("w=" + width + " h=" + height);

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
		System.out.println("Durchschnitt Helligkeitswerte=" + avgNormSum);

		//
		// Create output
		// Es werden nur jene Bildpunkte ausgegeben deren
		// Standartabweichung über die Helligkeit, gebilded aus
		// den r,g,b - Werten, über dem Durchnittswert gebildet
		// aus allen Bildpunkten liegt.
		//

		BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics = outputImage.createGraphics();

		Color color = new Color(150, 0, 0);
		graphics.setColor(color);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int clr = inputImage.getRGB(x, y);

				float norm = getAvgCol(x, y, clr);

				//
				// Das machts....
				//

				if (norm <= avgNormSum) {
					graphics.drawLine(x, y, x, y);

				}
			}

		}

		//
		// ISBN 13 => 13 Ziffern
		//
		// Schwarze Balken und Weisse Balken jeweils 4 unterschiedliche Breiten.
		// Dünner Schwarzer Balken=1 // Dünner weisser Balken=0
		//
		graphics.setColor(new Color(0,255, 0));// Zum markieren der Mitte, des Anfangs und des Endes des Codes...
		
		int c[];			// Rohe Bilddaten binary.
		c=new int[width];
		
		int barcode[];
		barcode=new int[width]; // Bildaten konvertiert in den barcode, binary
		
		//
		// Eine Zeile des barcodes lesen
		//
		int y = 185; 
		for (int x = 0; x < width; x = x + 1) {
			graphics.drawLine(0, y+2, width, y+2);
			int p=outputImage.getRGB(x, y);
			if (p==0) {
				c[x]=0;
			}else {
				c[x]=1;
			}
		}
		
		for (int i=0;i<width;i++)
			System.out.print(c[i]);
		System.out.println();
		
		//
		// Beginn des Barcodes in der Zeile suchen.
		//
		// Wir wissen, der Beginn wird durch 101 markiert.
		//
		int xStart=0,xEnd=0;
		
		while (c[xStart]!=1)
			xStart++;
		
		graphics.drawLine(xStart, 0, xStart, height);
		
		//
		// Die Breite des ersten Balkens bestimmt die Midestbreite für eine 0
		// oder eine 1. Diese Ziffern werden "Module" genannt.
		//
		xEnd=xStart;
		while(c[xEnd]!=0)
			xEnd++;
	
		int minBarWidth=xEnd-xStart;
		int moduleWidth=minBarWidth*7;
		
		System.out.println(minBarWidth);
		
		//
		// Nun bestimmen wir das Ende der aktuellen Zeile des Barcodes in der Bilddatei. 
		//
		// Ein ISBN 13 Barcode besteht aus 95 Modulen. Er:
		//
		// - beginnt mit 101 = 3 Module
		// - Hat in der Mitte die markierung 01010 = 5 Module
		// - Ended mit 101 = 3 Module.
		//
		// Dieses "Framework" lässt nun platz für 84 Module. 42 in der linken/ 42 in der rechten 
		// Hälfte...
		//
		// Wir markieren nun zunächst einmal die restlichen Bereiche des Barcodes...
		// 
		xEnd=xStart+minBarWidth*95;
		graphics.drawLine(xEnd, 0, xEnd, height);
	
		
		int startOfFirstHalf=xStart+3*minBarWidth;
		graphics.drawLine(startOfFirstHalf, 0, startOfFirstHalf, height);
		int endOfFirstHalf=startOfFirstHalf+42*minBarWidth;
		graphics.drawLine(endOfFirstHalf, 0, endOfFirstHalf, height);
		int startOfSecondHalf=endOfFirstHalf+5*minBarWidth;
		graphics.drawLine(startOfSecondHalf, 0, startOfSecondHalf, height);
		int endOfSecondHalf=startOfSecondHalf+42*minBarWidth;
		graphics.drawLine(endOfSecondHalf, 0, endOfSecondHalf, height);
		
		graphics.setColor(new Color(0,255,255));
		
		for (int x=startOfFirstHalf+moduleWidth;x<endOfFirstHalf;x=x+moduleWidth)
			graphics.drawLine(x, 0, x, height);
		
		for (int x=startOfSecondHalf+moduleWidth;x<endOfSecondHalf;x=x+moduleWidth)
			graphics.drawLine(x, 0, x, height);
		
		//
		// Ergebnis speichern.
		//
		File outputfile = new File("saved.png");
		ImageIO.write(outputImage, "png", outputfile);

		
		//
		// Jede Ziffer wird mit 7 Modulen codiert...
		// Aus den Rohdaten werden diese Module nun extraiert.
		//
	
		System.out.print(c[xStart]);
		xStart=xStart+minBarWidth;
		System.out.print(c[xStart]);
		xStart=xStart+minBarWidth;
		System.out.println(c[xStart]);
		xStart=xStart+minBarWidth;
		
		StringBuilder module=new StringBuilder();
		int mod=7;
		for (int i=startOfSecondHalf;i<=endOfSecondHalf;i=i+minBarWidth) {
			System.out.print(c[i]);
			module.append(c[i]);
			mod--;
			if (mod==0) {
				mod=7;
				int digit=getDigitSecondH(module.toString());
				System.out.println("    "+digit);
				module.setLength(0);
			}
			
		}
		
		
	}
	
	/**
	 * Binär modul der zweiten Hälfte des Barcodes 
	 * in einen integer decodieren. Wenn ein unbekanntes
	 * Muster übergeben wird, dann wird die 666 zurückgegeben.
	 * 
	 * @param d
	 * @return
	 */
	private static int getDigitSecondH(String d) {
		
		final int INVALID=666;
		
		if (d.equals("1110010")) return 0;
		if (d.equals("1100110")) return 1;
		if (d.equals("1101100")) return 2;
		if (d.equals("1000010")) return 3;
		if (d.equals("1011100")) return 4;
		if (d.equals("1001110")) return 5;
		if (d.equals("1010000")) return 6;
		if (d.equals("1000100")) return 7;
		if (d.equals("1001000")) return 8;
		if (d.equals("1110100")) return 9;
		
		return INVALID;
	}

	/**
	 * prints the contents of buff2 on buff1 with the given opaque value.
	 */
	static private void addImage(BufferedImage buff1, BufferedImage buff2, float opaque, int x, int y) {
		Graphics2D g2d = buff1.createGraphics();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opaque));
		g2d.drawImage(buff2, x, y, null);
		g2d.dispose();
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
