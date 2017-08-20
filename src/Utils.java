package kana;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class Utils {

	private static Utils instance = new Utils();

	private Utils() {

	}

	public synchronized BufferedImage getBufferedImage( String path ) {

		Image img = new ImageIcon( ClassLoader.getSystemResource( path ) ).getImage();

		//System.out.printf("Height=%d, Width=%d\n", img.getHeight(null), img.getWidth(null) );

		int width = img.getWidth( null );
		int height = img.getHeight( null );

		BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB ); // was RGB
		Graphics2D g2 = bi.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.drawImage( img, 0, 0, width, height, null);
		g2.dispose();
		
		return bi;


	}

	public static Utils getInstance() {
		return instance;
	}

}
