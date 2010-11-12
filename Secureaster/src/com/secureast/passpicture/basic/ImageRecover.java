package com.secureast.passpicture.basic;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageRecover {

	public void recover(String fileName1, String fileName2) {
		try {
			BufferedImage inImage1 = ImageIO.read(new File(fileName1));
			int w1 = inImage1.getWidth();
			int h1 = inImage1.getHeight();
			Raster inRaster1 = inImage1.getData();

			BufferedImage inImage2 = ImageIO.read(new File(fileName2));
			int w2 = inImage1.getWidth();
			int h2 = inImage1.getHeight();
			Raster inRaster2 = inImage2.getData();

			BufferedImage outImage = new BufferedImage(w1, h1,
					BufferedImage.TYPE_INT_RGB);
			WritableRaster outRaster = outImage.getRaster();

			// image has to be RGB colour image
			int[] pixel1 = new int[3];
			int[] pixel2 = new int[3];

			for (int x = 0; x < w1; x++) {
				for (int y = 0; y < h1; y++) {
					inRaster1.getPixel(x, y, pixel1);
					inRaster2.getPixel(x, y, pixel2);
					pixel1[0] = pixel1[0] + pixel2[0];
					pixel1[1] = pixel1[1] + pixel2[1];
					pixel1[2] = pixel1[2] + pixel2[2];
					// if (pixel1[0]==0 && pixel1[1]==0 && pixel1[2]==0)
					outRaster.setPixel(x, y, pixel1);
					// else
					// outRaster.setPixel(x, y, pixel1);
				}

			}

			ImageIO.write(outImage, "jpg", new File("bld_recover.jpg"));

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

		ImageRecover mm = new ImageRecover();

		mm.getImageProperties("bld.jpg");

		mm.recover("bld_tear1.jpg", "bld_tear2.jpg");
	}

}