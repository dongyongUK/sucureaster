package com.secureast.passpicture.basic;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ImageFileHandler {

	static public BufferedImage loadImage(String fileName) throws IOException {
		BufferedImage bf = ImageIO.read(new File(fileName));
		bf.flush();
		return bf;
	}

	static public BufferedImage loadImage(URL imageSrc) throws IOException{
		BufferedImage bf = ImageIO.read(imageSrc);
		bf.flush();
		return bf;
	}

	static public URL fileNameToURL(String imageFileName)throws Exception{
		URL imageSrc = null;
        try {
             imageSrc = ((new File(imageFileName)).toURI()).toURL();
        } catch (MalformedURLException e) {
        	throw new Exception(e);
        }
        return imageSrc;
	}
	
	static public URL URIToURL(URI uriSrc)throws Exception{
		URL imageSrc = null;
        try {
             imageSrc = uriSrc.toURL();
        } catch (MalformedURLException e) {
        	throw new Exception(e);
        }
        return imageSrc;
	}
	
	// static public void saveImage(String fileName, BufferedImage imageBuff,
	// String imageType) throws Exception {
	// FileOutputStream fos = new FileOutputStream(new File(fileName));
	// fos.write(bufferedToBytes(imageBuff, imageType));
	//	
	// }

	// static public void saveImage(String fileName, byte[] imageInByte) throws
	// Exception {
	// FileOutputStream fos = new FileOutputStream(new File(fileName));
	// fos.write(imageInByte);
	// }

	static public void saveImage(byte[] imageInByte, String fileName)
			throws Exception {
		FileOutputStream fos = new FileOutputStream(new File(fileName));
		fos.write(imageInByte);
	}

	static public boolean saveImage(RenderedImage im, String formatName,
			String fileName) throws Exception {

		return ImageIO.write(im, formatName, new File(fileName));
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

	static int[] toRawImage(byte[] iamgeInByte) throws Exception {
		int size = iamgeInByte.length;
		int[] image = new int[size];
		for (int i = 0; i < size; i++)
			image[i] = (int) iamgeInByte[i];
		return image;
	}

	static BufferedImage bytesToBuffered(byte[] imageInByte) throws IOException {

		InputStream in = new ByteArrayInputStream(imageInByte);
		return ImageIO.read(in);
	}

	static public List<IIOMetadata> getMetadata(String fileName) {
		try {
			FileInputStream fis = new FileInputStream(new File(fileName));
			Iterator<ImageReader> it = ImageIO.getImageReaders(fis);
			List<IIOMetadata> mts = new ArrayList<IIOMetadata>();

			while (it.hasNext()) {
				mts.add(it.next().getStreamMetadata());
			}

			return mts;
		} catch (Exception e) {
			return null;
		}
	}

	static private void indent(int level) {
		for (int i = 0; i < level; i++) {
			System.out.print("  ");
		}
	}

	static public void displayMetadata(Node node, int level) {
		indent(level); // emit open tag
		System.out.print("<" + node.getNodeName());
		NamedNodeMap map = node.getAttributes();
		if (map != null) { // print attribute values
			int length = map.getLength();
			for (int i = 0; i < length; i++) {
				Node attr = map.item(i);
				System.out.print(" " + attr.getNodeName() + "=\""
						+ attr.getNodeValue() + "\"");
			}
		}

		Node child = node.getFirstChild();
		if (child != null) {
			System.out.println(">"); // close current tag
			while (child != null) { // emit child tags recursively
				displayMetadata(child, level + 1);
				child = child.getNextSibling();
			}
			indent(level); // emit close tag
			System.out.println("</" + node.getNodeName() + ">");
		} else {
			System.out.println("/>");
		}
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