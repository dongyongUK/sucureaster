package com.secureast.passpicture.basic;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageFileHandler {

	static public BufferedImage loadImage(String fileName) throws IOException {
		 BufferedImage bf= ImageIO.read(new File(fileName));
		 bf.flush();
		 return bf;
	}

	static public void saveImage(String fileName, BufferedImage imageBuff,
			String imageType) throws Exception {
		FileOutputStream fos = new FileOutputStream(new File(fileName));
		fos.write(bufferedToBytes(imageBuff, imageType));
	}

	static public void saveImage(String fileName, byte[] imageInByte) throws Exception {
		FileOutputStream fos = new FileOutputStream(new File(fileName));
		fos.write(imageInByte);
	}
	
	static byte[] bufferedToBytes(BufferedImage bimage, String imageType)
			throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bimage, imageType, baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		return imageInByte;
	}

	static BufferedImage bytesToBuffered(byte[] imageInByte) throws IOException {

		InputStream in = new ByteArrayInputStream(imageInByte);
		return ImageIO.read(in);
	}

	/**
	 * Construct the buffered image that supports transparency
	 * 
	 * @return
	 */
	static public BufferedImage contructBufferedImage(int width, int height) {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gs.getDefaultConfiguration();
		BufferedImage bImage = gc.createCompatibleImage(width, height,
				Transparency.TRANSLUCENT);
		return bImage;
	}

}