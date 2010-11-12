package com.secureast.passpicture.basic;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import org.w3c.dom.Node;

/**
 * ch5ImageReader.java -- this class provides the functionality to read an image
 * of format ch5.
 */
public class ch5ImageReader extends ImageReader {
  private ImageInputStream iis;

  private ch5ImageMetadata[] imagemd;

  private ch5StreamMetadata streammd;

  public ch5ImageReader(ImageReaderSpi originatingProvider) {
    super(originatingProvider);
  }

  /**
   * return the ch5StreamMetadata object instantiated in the setStreamMetadata
   * method
   */
  public IIOMetadata getStreamMetadata() {
    return streammd;
  }

  /**
   * return the ch5ImageMetadata object instantiated in the setImageMetadata
   * method
   */
  public IIOMetadata getImageMetadata(int imageIndex) {
    return imagemd[imageIndex];
  }

  /**
   * this method sets the input for this ImageReader and also calls the
   * setStreamMetadata method so that the numberImages field is available
   */
  public void setInput(Object object, boolean seekForwardOnly) {
    super.setInput(object, seekForwardOnly);
    if (object == null)
      throw new IllegalArgumentException("input is null");

    if (!(object instanceof ImageInputStream)) {
      String argString = "input not an ImageInputStream";
      throw new IllegalArgumentException(argString);
    }
    iis = (ImageInputStream) object;
    setStreamMetadata(iis);
  }

  /**
   * this method provides suggestions for possible image types that will be
   * used to decode the image specified by index imageIndex. By default, the
   * first image type returned by this method will be the image type of the
   * BufferedImage returned by the ImageReader's getDestination method. In
   * this case, we are suggesting using an 8 bit grayscale image with no alpha
   * component.
   */
  public Iterator getImageTypes(int imageIndex) {
    java.util.List l = new java.util.ArrayList();
    ;
    int bits = 8;

    /*
     * can convert ch5 format into 8 bit grayscale image with no alpha
     */
    l.add(ImageTypeSpecifier.createGrayscale(bits, DataBuffer.TYPE_BYTE,
        false));
    return l.iterator();
  }

  /**
   * read in the input image specified by index imageIndex using the
   * parameters specified by the ImageReadParam object param
   */
  public BufferedImage read(int imageIndex, ImageReadParam param) {

    checkIndex(imageIndex);

    if (isSeekForwardOnly())
      minIndex = imageIndex;
    else
      minIndex = 0;

    BufferedImage bimage = null;
    WritableRaster raster = null;

    /*
     * this method sets the image metadata so that we can use the getWidth
     * and getHeight methods
     */
    setImageMetadata(iis, imageIndex);

    int srcWidth = getWidth(imageIndex);
    int srcHeight = getHeight(imageIndex);

    // initialize values to -1
    int dstWidth = -1;
    int dstHeight = -1;
    int srcRegionWidth = -1;
    int srcRegionHeight = -1;
    int srcRegionXOffset = -1;
    int srcRegionYOffset = -1;
    int xSubsamplingFactor = -1;
    int ySubsamplingFactor = -1;
    if (param == null)
      param = getDefaultReadParam();

    Iterator imageTypes = getImageTypes(imageIndex);
    try {
      /*
       * get the destination BufferedImage which will be filled using the
       * input image's pixel data
       */
      bimage = getDestination(param, imageTypes, srcWidth, srcHeight);

      /*
       * get Rectangle object which will be used to clip the source
       * image's dimensions.
       */
      Rectangle srcRegion = param.getSourceRegion();
      if (srcRegion != null) {
        srcRegionWidth = (int) srcRegion.getWidth();
        srcRegionHeight = (int) srcRegion.getHeight();
        srcRegionXOffset = (int) srcRegion.getX();
        srcRegionYOffset = (int) srcRegion.getY();

        /*
         * correct for overextended source regions
         */
        if (srcRegionXOffset + srcRegionWidth > srcWidth)
          dstWidth = srcWidth - srcRegionXOffset;
        else
          dstWidth = srcRegionWidth;

        if (srcRegionYOffset + srcRegionHeight > srcHeight)
          dstHeight = srcHeight - srcRegionYOffset;
        else
          dstHeight = srcRegionHeight;
      } else {
        dstWidth = srcWidth;
        dstHeight = srcHeight;
        srcRegionXOffset = srcRegionYOffset = 0;
      }
      /*
       * get subsampling factors
       */
      xSubsamplingFactor = param.getSourceXSubsampling();
      ySubsamplingFactor = param.getSourceYSubsampling();

      /**
       * dstWidth and dstHeight should be equal to bimage.getWidth() and
       * bimage.getHeight() after these next two instructions
       */
      dstWidth = (dstWidth - 1) / xSubsamplingFactor + 1;
      dstHeight = (dstHeight - 1) / ySubsamplingFactor + 1;
    } catch (IIOException e) {
      System.err.println("Can't create destination BufferedImage");
    }
    raster = bimage.getWritableTile(0, 0);

    /*
     * using the parameters specified by the ImageReadParam object, read the
     * image image data into the destination BufferedImage
     */
    byte[] srcBuffer = new byte[srcWidth];
    byte[] dstBuffer = new byte[dstWidth];
    int jj;
    int index;
    try {
      for (int j = 0; j < srcHeight; j++) {
        iis.readFully(srcBuffer, 0, srcWidth);

        jj = j - srcRegionYOffset;
        if (jj % ySubsamplingFactor == 0) {
          jj /= ySubsamplingFactor;
          if ((jj >= 0) && (jj < dstHeight)) {
            for (int i = 0; i < dstWidth; i++) {
              index = srcRegionXOffset + i * xSubsamplingFactor;
              dstBuffer[i] = srcBuffer[index];
            }
            raster.setDataElements(0, jj, dstWidth, 1, dstBuffer);
          }
        }
      }
    } catch (IOException e) {
      bimage = null;
    }
    return bimage;
  }

  /**
   * this method sets the image metadata for the image indexed by index
   * imageIndex. This method is specific for the ch5 format and thus only sets
   * the image width and image height
   */
  private void setImageMetadata(ImageInputStream iis, int imageIndex) {
    imagemd[imageIndex] = new ch5ImageMetadata();
    try {
      String s;
      s = iis.readLine();
      while (s.length() == 0)
        s = iis.readLine();
      imagemd[imageIndex].imageWidth = Integer.parseInt(s.trim());
      s = iis.readLine();
      imagemd[imageIndex].imageHeight = Integer.parseInt(s.trim());
    } catch (IOException exception) {
    }
  }

  /**
   * this method sets the stream metadata for the images represented by the
   * ImageInputStream iis. This method is specific for the ch5 format and thus
   * only sets the numberImages field.
   */
  private void setStreamMetadata(ImageInputStream iis) {
    streammd = new ch5StreamMetadata();
    try {
      String magicNumber = iis.readLine();
      int numImages = Integer.parseInt(iis.readLine().trim());
      streammd.numberImages = numImages;
      imagemd = new ch5ImageMetadata[streammd.numberImages];
    } catch (IOException exception) {
    }
  }

  /**
   * This method can only be used after the stream metadata has been set
   * (which occurs in the setInput method). Else it will return a -1
   */
  public int getNumImages(boolean allowSearch) {
    return streammd.numberImages;
  }

  /**
   * This method can only be used after the stream metadata has been set
   * (which occurs in the setInput method). Else it will return a -1
   */
  public int getHeight(int imageIndex) {
    if (imagemd == null)
      return -1;
    checkIndex(imageIndex);

    return imagemd[imageIndex].imageHeight;
  }

  /**
   * This method can only be used after the stream metadata has been set
   * (which occurs in the setInput method). Else it will return a -1
   */
  public int getWidth(int imageIndex) {
    if (imagemd == null)
      return -1;
    checkIndex(imageIndex);

    return imagemd[imageIndex].imageWidth;
  }

  private void checkIndex(int imageIndex) {
    if (imageIndex >= streammd.numberImages) {
      String argString = "imageIndex >= number of images";
      throw new IndexOutOfBoundsException(argString);
    }
    if (imageIndex < minIndex) {
      String argString = "imageIndex < minIndex";
      throw new IndexOutOfBoundsException(argString);
    }
  }
}


