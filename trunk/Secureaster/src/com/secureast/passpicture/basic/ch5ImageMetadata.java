package com.secureast.passpicture.basic;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.Node;

/**
 * ch5ImageMetadata.java -- holds image metadata for the ch5 format.
 * The internal tree for holding this metadata is read only
 */
class ch5ImageMetadata extends IIOMetadata {
    static final String
        nativeMetadataFormatName = "ch5.imageio.ch5image_1.00";
    static final String
        nativeMetadataFormatClassName = "ch5.imageio.ch5image";

    static final String[] extraMetadataFormatNames = null;
    static final String[] extraMetadataFormatClassNames = null;

    static final boolean standardMetadataFormatSupported = false;

    public int imageWidth;
    public int imageHeight;

    public ch5ImageMetadata() {
  super(standardMetadataFormatSupported,
        nativeMetadataFormatName, 
        nativeMetadataFormatClassName,
        extraMetadataFormatNames,
        extraMetadataFormatClassNames
        );
  imageWidth = -1;
  imageHeight = -1;
    }
    public boolean isReadOnly() {
        return true;
    }

    /**
     * IIOMetadataFormat objects are meant to describe the structure of
     * metadata returned from the getAsTree method.  In this case,
     * no such description is available
     */
    public IIOMetadataFormat getMetadataFormat(String formatName) {
        if (formatName.equals(nativeMetadataFormatName)) {
            return null;
        } else {
            throw new IllegalArgumentException("Unrecognized format!");
        }
    }

    /**
     * returns the image metadata in a tree corresponding to the
     * provided formatName
     */
    public Node getAsTree(String formatName) {
        if (formatName.equals(nativeMetadataFormatName)) {
            return getNativeTree();
        } else {
            throw new IllegalArgumentException("Unrecognized format!");
        }
    }

    /**
     * returns the image metadata in a tree using the following format
     * <!ELEMENT ch5.imageio.ch5image_1.00 (imageDimensions)>
     * <!ATTLIST imageDimensions
     *      imageWidth   CDATA  #REQUIRED
     *      imageHeight  CDATA  #REQUIRED
     */
    private Node getNativeTree() {
        IIOMetadataNode root =
            new IIOMetadataNode(nativeMetadataFormatName);

        IIOMetadataNode node = new IIOMetadataNode("imageDimensions");
        node.setAttribute("imageWidth", Integer.toString(imageWidth));
        node.setAttribute("imageHeight", Integer.toString(imageHeight));
        root.appendChild(node);

        return root;
    }

    public void setFromTree(String formatName, Node root) {
        throw new IllegalStateException("Metadata is read-only!");
    }

    public void mergeTree(String formatName, Node root) {
        throw new IllegalStateException("Metadata is read-only!");
    }

    public void reset() {
        throw new IllegalStateException("Metadata is read-only!");
    }

    /**
     * initialize the image metadata elements width and height
     */
    public void initialize(int width, int height) {
  imageWidth = width;
  imageHeight = height;
    }
}
