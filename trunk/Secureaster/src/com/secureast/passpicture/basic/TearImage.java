package com.secureast.passpicture.basic;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

public class TearImage {

	public void tear(String fileName) {
		try {
			BufferedImage inImage = ImageIO.read(new File(fileName));
			int w = inImage.getWidth();
			int h = inImage.getHeight();
			Raster inRaster = inImage.getData();

			BufferedImage outImage1 = new BufferedImage(w, h,
					BufferedImage.TYPE_INT_RGB);
			WritableRaster outRaster1 = outImage1.getRaster();
			BufferedImage outImage2 = new BufferedImage(w, h,
					BufferedImage.TYPE_INT_RGB);
			WritableRaster outRaster2 = outImage2.getRaster();

			// image has to be RGB colour image
			int[] aPixel = new int[3];

			// int[] lineX=RandomLine.oneDimen(0, w);
			int[] lineY = RandomLine.oneDimen(90, 125, w);

			int[] lineValue = new int[3 * h];
			for (int x = 0; x < w; x++) {
				inRaster.getPixels(x, 0, 1, h, lineValue);
				
				for (int y = 0; y <h ; y++) {
					
					aPixel[0] = lineValue[3 * y];
					aPixel[1] = lineValue[3 * y + 1];
					aPixel[2] = lineValue[3 * y + 2];
					
					if (y < lineY[x])
						outRaster1.setPixel(x, y, aPixel);
					else
						outRaster2.setPixel(x, y, aPixel);
				}

			}

			ImageIO.write(outImage1, "jpg", new File("bld_tear1.jpg"));
			ImageIO.write(outImage2, "jpg", new File("bld_tear2.jpg"));
		} catch (Exception e) {
			;
		}
	}

	public void getImageProperties(String fileName) {
		try {
			BufferedImage bImage = ImageFileHandler.loadImage(fileName);
			int height = bImage.getHeight();
			int width = bImage.getWidth();
			int type = bImage.getType();
			ColorModel cm = bImage.getColorModel();
			String[] prop = bImage.getPropertyNames();
			System.out.println("Image width: " + width + " height: " + height
					+ "  type: " + type);

		} catch (Exception e) {
			;
		}
	}

	static public void main(String[] argc) {

		TearImage mm = new TearImage();

		mm.getImageProperties("bld.jpg");

		mm.tear("bld.jpg");
	}

}
