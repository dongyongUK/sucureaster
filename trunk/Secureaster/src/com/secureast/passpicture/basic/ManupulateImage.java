package com.secureast.passpicture.basic;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

import org.w3c.dom.Element;

public class ManupulateImage {

	public void halfImage(URL imageSrc) {
		try {
			BufferedImage bImage = ImageFileHandler.loadImage(imageSrc);
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

	public void touchRegion(URL imageSrc, int x, int y, int wd, int ht) {
		try {
			BufferedImage inImage = ImageIO.read(imageSrc);
			int w = inImage.getWidth();
			int h = inImage.getHeight();
			// Raster inRaster = inImage.getData(new Rectangle(x,y,wd,ht));
			Raster inRaster = inImage.getData();

			BufferedImage outImage = new BufferedImage(w, h,
					BufferedImage.TYPE_INT_RGB);
			WritableRaster outRaster = outImage.getRaster();
			// image has to be RGB colour image
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

	public void cutImage(String fileName, int x, int y, int wd, int ht) {

		try {
			BufferedImage inImage = ImageIO.read(new File(fileName));
			int width = inImage.getWidth();
			int height = inImage.getHeight();
			Raster inRaster = inImage.getData();

			BufferedImage outImage = new BufferedImage(wd, ht,
					BufferedImage.TYPE_INT_RGB);
			WritableRaster outRaster = outImage.getRaster();
			// image has to be RGB colour image
			int[] aPixel = new int[3];
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					inRaster.getPixel(j, i, aPixel);
					if (i >= y && i < ht + y && j >= x && j < wd + x)
						outRaster.setPixel(j - x, i - y, aPixel);
				}
			}

			ImageIO.write(outImage, "jpg", new File("bld_cut.jpg"));
		} catch (Exception e) {
			;
		}
	}

	public void getImageProperties(URL srcImage) {
		try {
			BufferedImage bImage = ImageFileHandler.loadImage(srcImage);
			int height = bImage.getHeight();
			int width = bImage.getWidth();
			int type = bImage.getType();
			ColorModel cm = bImage.getColorModel();
			String[] prop = bImage.getPropertyNames();
			System.out.println("Image width: " + width + " height: " + height
					+ "  type: " + type);

		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}
	
	public void setMetadata(String fileIn, String fileOut) {

		try {
			
			// read the input image
			BufferedImage bImage = ImageIO.read(new File(fileIn));
			
			// create output buffered image
			BufferedImage bufferedImage = new BufferedImage(bImage.getWidth(), bImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			// get writable raster from the buffered image
			WritableRaster wr=bufferedImage.getRaster();
			// set the raster from the input image
			wr.setRect(bImage.getRaster());

			// Image writer
			Iterator<ImageWriter> imageWriters = ImageIO
					.getImageWritersBySuffix("jpeg");
			// get the first image writer
			ImageWriter imageWriter = imageWriters.next();

			// create image out
			ImageOutputStream ios = ImageIO.createImageOutputStream(new File(
					fileOut));
			imageWriter.setOutput(ios);

			// encode parameters 
			ImageWriteParam jpegParams = imageWriter.getDefaultWriteParam();
			// MODE_EXPLICIT allows the use of a set method to provide additional parameters;
			jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			//jpegParams.setCompressionQuality(0.85f);

			// image Metadata 
			IIOMetadata metadata = imageWriter.getDefaultImageMetadata(
					new ImageTypeSpecifier(bufferedImage), jpegParams);

			
			Element tree = (Element) metadata
					.getAsTree(metadata.getNativeMetadataFormatName());
			
			
			/*
			Element jfif = (Element) tree.getElementsByTagName("app0JFIF")
					.item(0);
			jfif.setAttribute("Xdensity", Integer.toString(dpi));
			jfif.setAttribute("Ydensity", Integer.toString(dpi));
			jfif.setAttribute("resUnits", "1"); // density is dots per inch
            */
		
			IIOMetadataNode markerSeq=(IIOMetadataNode) tree.getElementsByTagName("markerSequence").item(0);
			
			IIOMetadataNode comNode = new IIOMetadataNode("com");
		    comNode.setAttribute("comment", "my  comment value");
		    markerSeq.appendChild(comNode);
		    
		    // The tree is not directly mapped to the IOMetaData. 
			// To apply data from tree, add following call 
			metadata.setFromTree("javax_imageio_jpeg_image_1.0", tree);
			
	         // when metadata!=null, the following method will ignore the mete data
			//imageWriter.write(metadata, new IIOImage(bufferedImage, null, null),
			//		jpegParams);
			imageWriter.write(null, new IIOImage(bufferedImage, null, metadata), jpegParams);

			ios.close();
			imageWriter.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	static public void main(String[] argc) throws Exception {

		ManupulateImage mm = new ManupulateImage();
		URL url = new URL("http://s3.amazonaws.com/twitpic/photos/large/224966759.jpg?AWSAccessKeyId=0ZRYP5X5F6FSMBCCSE82&Expires=1295125071&Signature=qowi%2Fk7RO9fO59Nli9mvMc%2BHjLo%3D");		
		//URL url = new URL("http://www.chinatourstailor.com/images/Guilin%20Merryland%20Golf%20Club.jpg");
		String fileName = "D://photo//p51.jpg";
		URI uri = new URI("file://Stora/MyLibrary/MyPhotos/frame_photo1/Cornwall_1.jpg");
		mm.getImageProperties(url);
		 //mm.getImageProperties(ImageFileHandler.URIToURL(uri));
		 mm.halfImage(url);
		// mm.touchRegion("bld.jpg", 20, 20, 50, 60);
		// mm.cutImage("bld.jpg", 20, 20, 40, 40);
		//mm.setMetadata("me.jpg", "me_meta.jpg");
		//MetadataHandler.readAndDisplayMetadataDemo("me_meta.jpg");
		
	}

}
