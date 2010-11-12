package com.secureast.passpicture.basic;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ManupulateImage {

	public void halfImage(String fileName) {
		try {
			BufferedImage bImage = ImageFileHandler.loadImage(fileName);
			int w = bImage.getWidth();
			int h = bImage.getHeight();
			BufferedImage nimage = new BufferedImage(w / 2, h / 2, bImage
					.getType());
			Graphics2D g = nimage.createGraphics();
			g.drawImage(bImage, 0, 0, w / 2, h / 2, 0, 0, w / 2, h / 2, null);
			g.dispose();
			ImageIO.write(nimage, "jpg", new File("bld_half.jpg"));

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void touchRegion(String fileName, int x, int y, int wd, int ht) {
		try {
			BufferedImage inImage = ImageIO.read(new File(fileName));
			int w = inImage.getWidth();
			int h = inImage.getHeight();
			// Raster inRaster = inImage.getData(new Rectangle(x,y,wd,ht));
			Raster inRaster = inImage.getData();

			BufferedImage outImage = new BufferedImage(w, h,
					BufferedImage.TYPE_INT_RGB);
			WritableRaster outRaster = outImage.getRaster();
			//image has to be RGB colour image
			int[] aPixel = new int[3];
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					inRaster.getPixel(j, i, aPixel);
					if (i >= y && i < ht + y && j >= x && j < wd + x) {
						aPixel[0] = aPixel[0] / 2;
						aPixel[1] = aPixel[1] / 2;
						aPixel[2] = aPixel[2] / 2;
					}
					outRaster.setPixel(j, i, aPixel);
				}
			}

			ImageIO.write(outImage, "jpg", new File("bld_touch.jpg"));
		} catch (Exception e) {
			;
		}
	}

	public void cutImage(String fileName, int x, int y, int wd, int ht ){
		
		try {
			BufferedImage inImage = ImageIO.read(new File(fileName));
			int width = inImage.getWidth();
			int height = inImage.getHeight();
			Raster inRaster = inImage.getData();

			BufferedImage outImage = new BufferedImage(wd, ht,
					BufferedImage.TYPE_INT_RGB);
			WritableRaster outRaster = outImage.getRaster();
			//image has to be RGB colour image
			int[] aPixel = new int[3];
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					inRaster.getPixel(j, i, aPixel);
					if (i >= y && i < ht + y && j >= x && j < wd + x)
						outRaster.setPixel(j-x, i-y, aPixel);
				}
			}

			ImageIO.write(outImage, "jpg", new File("bld_cut.jpg"));
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

		ManupulateImage mm = new ManupulateImage();

		mm.getImageProperties("bld.jpg");
		mm.halfImage("bld.jpg");
		mm.touchRegion("bld.jpg", 20, 20, 50, 60);
		//mm.cutImage("bld.jpg", 20, 20, 40, 40);
	}

}
