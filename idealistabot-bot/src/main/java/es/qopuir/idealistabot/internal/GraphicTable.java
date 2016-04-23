package es.qopuir.idealistabot.internal;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

import javax.imageio.ImageIO;

public class GraphicTable {
	private static final int ROWS = 2;
	private static final int COLS = 7;
	private static final int PAD = 10;
	private static final int WIDTH = 280;
	private static final int HEIGHT = 200;
	private static final String[] msgs;

	static {
		msgs = new String[] { "L-01", "M-02", "X-03", "J-04", "V-05", "S-06", "D-07" };
	}

	private static void paintComponent(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Insets insets = new Insets(0, 5, 0, 5); // border info
		
		double xInc = (WIDTH - insets.left - insets.right - (2 * PAD)) / COLS;
		double yInc = (HEIGHT - insets.top - insets.bottom - (2 * PAD)) / ROWS;
		
		g.setPaint(Color.blue);
		
		// vertical lines
		double x1 = insets.left + PAD;
		double y1 = insets.top + PAD;
		
		double y2 = HEIGHT - insets.bottom - PAD;
		double x2 = 0.0;
		
		for (int j = 0; j <= COLS; j++) {
			g.draw(new Line2D.Double(x1, y1, x1, y2));
			x1 += xInc;
		}
		
		// horizontal lines
		x1 = insets.left + PAD;
		x2 = WIDTH - insets.right - PAD;
		for (int j = 0; j <= ROWS; j++) {
			g.draw(new Line2D.Double(x1, y1, x2, y1));
			y1 += yInc;
		}
		
		// try a couple of cell strings
		g.setPaint(Color.red);
		
		Font font = g.getFont().deriveFont(10f);
		g.setFont(font);
		
		FontRenderContext frc = g.getFontRenderContext();
		x1 = 0;
		
		for (int j = 0; j < msgs.length; j++) {
			float width = (float) font.getStringBounds(msgs[j], frc).getWidth();
			
			LineMetrics lm = font.getLineMetrics(msgs[j], frc);
			float ascent = lm.getAscent();
			float descent = lm.getDescent();
			
			float sx = (float) (insets.left + PAD + x1 + (xInc - width) / 2);
			float sy = (float) (insets.top + PAD + (yInc + ascent) / 2 - descent);
			g.drawString(msgs[j], sx, sy);
			
			x1 += xInc;
		}
	}

	public static void main(String[] args) throws IOException {
		BufferedImage buf = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    	Graphics2D graphics = buf.createGraphics();
    	
    	Path createTempFile = Files.createTempFile("", ".png", new FileAttribute[0]);
    	FileOutputStream os = new FileOutputStream(createTempFile.toFile());
    	
    	System.out.println(createTempFile.toFile().toString());
		
		GraphicTable.paintComponent(graphics);
		
		ImageIO.write(buf, "png", os);
	}
}