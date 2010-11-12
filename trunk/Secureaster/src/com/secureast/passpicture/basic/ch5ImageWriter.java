package com.secureast.passpicture.basic;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

import org.w3c.dom.Node;

/**
 * ch5ImageWriter.java -- this class provides the functionality to write an
 * image of format ch5.
 */
public class ch5ImageWriter extends ImageWriter {

  public ch5ImageWriter(ImageWriterSpi originatingProvider) {
    super(originatingProvider);
    streamMetadataWritten = false;
  }

  /**
   * this method is used to convert an ImageReader's image metadata which is
   * in a particular format into image metadata that can be used for this
   * ImageWriter. Primarily this is used for transcoding (format conversion).
   * This ImageWriter does not support such conversions
   */
  public IIOMetadata convertImageMetadata(IIOMetadata metadata,
      ImageTypeSpecifier specifier, ImageWriteParam param) {
    return null;
  }

  /**
   * this method is used to convert an ImageReader's stream metadata which is
   * in a particular format into stream metadata that can be used for this
   * ImageWriter. Primarily this is used for transcoding (format conversion).
   * This ImageWriter does not support such conversions
   */
  public IIOMetadata convertStreamMetadata(IIOMetadata metadata,
      ImageWriteParam param) {
    return null;
  }

  /**
   * provide default values for the image metadata
   */
  public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier specifier,
      ImageWriteParam param) {
    ch5ImageMetadata imagemd = new ch5ImageMetadata();
    int width = raster.getWidth();
    int height = raster.getHeight();
    imagemd.initialize(width, height); // default image size
    return imagemd;
  }

  /**
   * provide default values for the stream metadata
   */
  public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
    ch5StreamMetadata streammd = new ch5StreamMetadata();
    streammd.initialize(1); // default number of images
    return streammd;
  }

  /**
   * write out the output image specified by index imageIndex using the
   * parameters specified by the ImageWriteParam object param
   */
  public void write(IIOMetadata metadata, IIOImage iioimage,
      ImageWriteParam param) {
    Node root = null;
    Node dimensionsElementNode = null;

    if (iioimage.getRenderedImage() != null)
      raster = iioimage.getRenderedImage().getData();
    else
      raster = iioimage.getRaster();

    /*
     * since this format allows you to write multiple images, the
     * streamMetadataWritten variable makes sure the stream metadata is
     * written only once
     */
    if (streamMetadataWritten == false) {
      if (metadata == null)
        metadata = getDefaultStreamMetadata(param);
      root = metadata.getAsTree("ch5.imageio.ch5stream_1.00");
      dimensionsElementNode = root.getFirstChild();
      Node numberImagesAttributeNode = dimensionsElementNode
          .getAttributes().getNamedItem("numberImages");
      String numberImages = numberImagesAttributeNode.getNodeValue();
      try {
        ios.writeBytes("5\n");
        ios.writeBytes(numberImages);
        ios.flush();
      } catch (IOException ioe) {
        System.err.println("IOException " + ioe.getMessage());
      }
      streamMetadataWritten = true;
    }

    String widthString;
    String heightString;
    IIOMetadata imageMetadata = (ch5ImageMetadata) iioimage.getMetadata();
    /*
     * don't really need image metadata object here since raster knows
     * necessary information
     */
    if (imageMetadata == null)
      imageMetadata = getDefaultImageMetadata(null, param);

    root = imageMetadata.getAsTree("ch5.imageio.ch5image_1.00");
    dimensionsElementNode = root.getFirstChild();

    Node widthAttributeNode = dimensionsElementNode.getAttributes()
        .getNamedItem("imageWidth");
    widthString = widthAttributeNode.getNodeValue();

    Node heightAttributeNode = dimensionsElementNode.getAttributes()
        .getNamedItem("imageHeight");
    heightString = heightAttributeNode.getNodeValue();

    int sourceWidth = Integer.parseInt(widthString);
    int sourceHeight = Integer.parseInt(heightString);
    int destinationWidth = -1;
    int destinationHeight = -1;
    int sourceRegionWidth = -1;
    int sourceRegionHeight = -1;
    int sourceRegionXOffset = -1;
    int sourceRegionYOffset = -1;
    int xSubsamplingFactor = -1;
    int ySubsamplingFactor = -1;

    if (param == null)
      param = getDefaultWriteParam();

    /*
     * get Rectangle object which will be used to clip the source image's
     * dimensions.
     */
    Rectangle sourceRegion = param.getSourceRegion();
    if (sourceRegion != null) {
      sourceRegionWidth = (int) sourceRegion.getWidth();
      sourceRegionHeight = (int) sourceRegion.getHeight();
      sourceRegionXOffset = (int) sourceRegion.getX();
      sourceRegionYOffset = (int) sourceRegion.getY();

      /*
       * correct for overextended source regions
       */
      if (sourceRegionXOffset + sourceRegionWidth > sourceWidth)
        destinationWidth = sourceWidth - sourceRegionXOffset;
      else
        destinationWidth = sourceRegionWidth;

      if (sourceRegionYOffset + sourceRegionHeight > sourceHeight)
        destinationHeight = sourceHeight - sourceRegionYOffset;
      else
        destinationHeight = sourceRegionHeight;
    } else {
      destinationWidth = sourceWidth;
      destinationHeight = sourceHeight;
      sourceRegionXOffset = sourceRegionYOffset = 0;
    }
    /*
     * get subsampling factors
     */
    xSubsamplingFactor = param.getSourceXSubsampling();
    ySubsamplingFactor = param.getSourceYSubsampling();

    destinationWidth = (destinationWidth - 1) / xSubsamplingFactor + 1;
    destinationHeight = (destinationHeight - 1) / ySubsamplingFactor + 1;

    byte[] sourceBuffer;
    byte[] destinationBuffer = new byte[destinationWidth];

    try {
      ios.writeBytes(new String("\n"));
      ios.writeBytes(new String(destinationWidth + "\n"));
      ios.writeBytes(new String(destinationHeight + "\n"));

      int jj;
      int index;
      for (int j = 0; j < sourceWidth; j++) {
        sourceBuffer = (byte[]) raster.getDataElements(0, j,
            sourceWidth, 1, null);
        jj = j - sourceRegionYOffset;
        if (jj % ySubsamplingFactor == 0) {
          jj /= ySubsamplingFactor;
          if ((jj >= 0) && (jj < destinationHeight)) {
            for (int i = 0; i < destinationWidth; i++) {
              index = sourceRegionXOffset + i
                  * xSubsamplingFactor;
              destinationBuffer[i] = sourceBuffer[index];
            }
            ios.write(destinationBuffer, 0, destinationWidth);
            ios.flush();
          }
        }
      }
    } catch (IOException e) {
      System.err.println("IOException: " + e.getMessage());
    }
  }

  public void setOutput(Object output) {
    super.setOutput(output);

    if (output == null)
      throw new IllegalArgumentException("output is null");

    if (!(output instanceof ImageOutputStream))
      throw new IllegalArgumentException(
          "output not an ImageOutputStream");

    ios = (ImageOutputStream) output;
    streamMetadataWritten = false;
  }

  private ImageOutputStream ios;

  private boolean streamMetadataWritten;

  private Raster raster;
}
