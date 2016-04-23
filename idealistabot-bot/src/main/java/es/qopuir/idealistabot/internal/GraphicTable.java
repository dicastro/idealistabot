package es.qopuir.idealistabot.internal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

import javax.imageio.ImageIO;

public class GraphicTable {
	private static final float FONT_SIZE = 10.0f;
	
	private static final int LINE_WIDTH = 1;
	
	private static final int PADDING_T = 10;
	private static final int PADDING_R = 10;
	private static final int PADDING_B = 10;
	private static final int PADDING_L = 10;
	
	private static final int MARGIN_T = 10;
	private static final int MARGIN_R = 10;
	private static final int MARGIN_B = 10;
	private static final int MARGIN_L = 10;
	
	private final String[] colTitles;
	private final int cols;
	private final String[] rowTitles;
	private final int rows;
	
	private final String[][] tableContent;
	
	private final int width;
	private final int height;
	
	private final int cellWidth;
	private final int cellHeight;

	private final BufferedImage buf;
	private final Graphics2D g;

	public GraphicTable(String[] colTitles, String[] rowTitles, String[][] tableContent) {
		this.colTitles = colTitles;
		cols = Integer.max(colTitles.length + 1, getMaxWidth(tableContent));
		
		this.rowTitles = rowTitles;
		rows = Integer.max(rowTitles.length + 1, tableContent.length);
		
		this.tableContent = tableContent;
		
		cellWidth = getMaxContentWidth(colTitles, rowTitles, tableContent);
		
		int totalHPadding = cols * (PADDING_L + PADDING_R);
		int totalHMargin = MARGIN_L + MARGIN_R;
		int totalContentWidth = cols * cellWidth;
		int totalVLinesWidth = (cols + 1) * LINE_WIDTH;
		
		width = totalHPadding + totalHMargin + totalContentWidth + totalVLinesWidth;
		
		cellHeight = getMaxContentHeight(colTitles, rowTitles, tableContent);
		
		int totalVPadding = rows * (PADDING_T + PADDING_B);
		int totalVMargin = MARGIN_T + MARGIN_B;
		int totalContentHeight = rows * cellHeight;
		int totalHLinesHeight = (rows + 1) * LINE_WIDTH;
		
		height = totalVPadding + totalVMargin + totalContentHeight + totalHLinesHeight;
		
		buf = getBufferedImage(width, height);
		g = getInitializedGraphics(buf);
		
		paintTable();
	}
	
	private BufferedImage getBufferedImage(int width, int height) {
		BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		return buf;
	}
	
	private Graphics2D getInitializedGraphics(BufferedImage buf) {
		Graphics2D g = buf.createGraphics();
		
		initializeGraphics(g);
		
		return g;
	}
	
	private void initializeGraphics(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	
    	Font font = g.getFont().deriveFont(FONT_SIZE);
		g.setFont(font);
		
		g.setStroke(new BasicStroke(LINE_WIDTH));
    	
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, width, height);
	}
	
	private int getMaxWidth(String[][] table) {
		int maxWidth = 0;
		
		for (int y = 0; y < table.length; y++) {
			if (table[y].length > maxWidth) {
				maxWidth = table[y].length;
			}
		}
		
		return maxWidth;
	}
	
	private int getMaxContentWidth(String[] colTitles, String[] rowTitles, String[][] tableContent) {
		int maxContentWidth = 0;
		
		for (int y = 0; y < tableContent.length; y++) {
			int temp = getMaxContentWidth(tableContent[y]);
			
			if (temp > maxContentWidth) {
				maxContentWidth = temp;
			}
		}
				
		return Integer.max(Integer.max(getMaxContentWidth(colTitles), getMaxContentWidth(rowTitles)), maxContentWidth);
	}
	
	private int getMaxContentWidth(String[] contents) {
		int maxWidth = 0;
		
		BufferedImage buf = getBufferedImage(1, 1);
		Graphics2D g = getInitializedGraphics(buf);
		
		FontRenderContext frc = g.getFontRenderContext();
		
		for (String content : contents) {
			int width = (int) Math.ceil(g.getFont().getStringBounds(content, frc).getWidth());
			
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		
		return maxWidth;
	}
	
	private int getMaxContentHeight(String[] colTitles, String[] rowTitles, String[][] tableContent) {
		int maxContentHeight = 0;
		
		for (int y = 0; y < tableContent.length; y++) {
			int temp = getMaxContentHeight(tableContent[y]);
			
			if (temp > maxContentHeight) {
				maxContentHeight = temp;
			}
		}
				
		return Integer.max(Integer.max(getMaxContentHeight(colTitles), getMaxContentHeight(rowTitles)), maxContentHeight);
	}
	
	private int getMaxContentHeight(String[] contents) {
		int maxHeight = 0;
		
		BufferedImage buf = getBufferedImage(1, 1);
		Graphics2D g = getInitializedGraphics(buf);
		
		FontRenderContext frc = g.getFontRenderContext();
		
		for (String content : contents) {
			int height = (int) Math.ceil(g.getFont().getStringBounds(content, frc).getHeight());
			
			if (height > maxHeight) {
				maxHeight = height;
			}
		}
		
		return maxHeight;
	}
	
	private void paintTable() {
		double xInc = PADDING_L + cellWidth + PADDING_R + LINE_WIDTH;
		double yInc = PADDING_T + cellHeight + PADDING_B + LINE_WIDTH;
		
		g.setPaint(Color.BLUE);
		
		// vertical lines
		double x1 = MARGIN_L;
		double y1 = MARGIN_T;
		
		double x2 = x1;
		double y2 = height - MARGIN_B;
		
		for (int x = 0; x <= cols; x++) {
			g.draw(new Line2D.Double(x1, y1, x2, y2));
			
			if (x == 0) {
				x1++;
				x2++;
			}
			
			x1 += xInc;
			x2 += xInc;
		}
		
		// horizontal lines
		x1 = MARGIN_L;
		y1 = MARGIN_T;
		
		x2 = width - MARGIN_R;
		y2 = y1;
		
		for (int y = 0; y <= rows; y++) {
			g.draw(new Line2D.Double(x1, y1, x2, y2));
			
			if (y == 0) {
				y1++;
				y2++;
			}
			
			y1 += yInc;
			y2 += yInc;
		}
		
		// try a couple of cell strings
		g.setPaint(Color.RED);
		
		FontRenderContext frc = g.getFontRenderContext();
		
		x1 = MARGIN_L + LINE_WIDTH + PADDING_L + cellWidth + PADDING_R + LINE_WIDTH + PADDING_L;
		y1 = MARGIN_T + LINE_WIDTH + PADDING_T;
		
		for (int j = 0; j < colTitles.length; j++) {
			LineMetrics lm = g.getFont().getLineMetrics(colTitles[j], frc);
			float ascent = lm.getAscent();
			float descent = lm.getDescent();
			
			float xOffset = (((float)cellWidth) - ((float) g.getFont().getStringBounds(colTitles[j], frc).getWidth())) / 2;
			float yOffset = (((float)cellHeight) - ((float) g.getFont().getStringBounds(colTitles[j], frc).getHeight())) / 2 + ((float) (g.getFont().getStringBounds(colTitles[j], frc).getHeight() / 2));
			
			float sx = ((float) x1) + xOffset;
			float sy = ((float) y1) + yOffset;
			
			g.drawString(colTitles[j], sx, sy);
			
			x1 += xInc;
		}
	}
	
	public BufferedImage getImage() {
		return buf;
	}

	public static void main(String[] args) throws IOException {
		Path tmpDir = Paths.get("target", "tmp", "img");

		if (!tmpDir.toFile().exists()) {
			tmpDir.toFile().mkdirs();
		}
		
    	Path createTempFile = Files.createTempFile(tmpDir, "weekTimetable", ".png", new FileAttribute[0]);
    	FileOutputStream os = new FileOutputStream(createTempFile.toFile());
    	
    	System.out.println(createTempFile.toFile().toString());
		
    	String[] colTitles = new String[] { "LU-01", "MA-02", "MI-03", "JU-04", "VI-05", "SA-06", "DO-07" };
    	String[] rowTitles = new String[] { "18:00", "18:30", "19:00", "19:00" };
    	String[][] tableContent = new String[][] { {"/1"}, {}, {}, {} };
    	
		GraphicTable graphicTable = new GraphicTable(colTitles, rowTitles, tableContent);
		
		ImageIO.write(graphicTable.getImage(), "png", os);
		os.close();
	}
}