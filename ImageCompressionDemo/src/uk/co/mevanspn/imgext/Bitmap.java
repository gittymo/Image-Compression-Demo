/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.mevanspn.imgext;

import java.awt.image.BufferedImage;

/**
 *
 * @author morganevans
 */
final public class Bitmap {

    public Bitmap(java.awt.image.BufferedImage _image) {
	source_image = _image;
    }

    public java.awt.image.BufferedImage getCompressPreview(int _lum_bits,
	    int _chr_bits, int _block_width, int _block_height) {
	java.awt.image.BufferedImage return_image =
		new java.awt.image.BufferedImage(source_image.getWidth(),
		source_image.getHeight(), source_image.getType());
	YCCBlock ycc = null;
	lowCr = 256; highCr = 0;
	lowCb = 256; highCb = 0;
	for (int y = 0; y < source_image.getHeight(); y += _block_height) {
	    for (int x = 0; x < source_image.getWidth(); x += _block_width) {
		ycc = new YCCBlock(source_image, x, y, _block_width,
			_block_height);
		final int Cr = (int) ycc.getCr();
		final int Cb = (int) ycc.getCb();
		if (Cr < lowCr) {
		    lowCr = Cr;
		}
		if (Cb < lowCb) {
		    lowCb = Cb;
		}
		if (Cr > highCr) {
		    highCr = Cr;
		}
		if (Cb > highCb) {
		    highCb = Cb;
		}
		int[] samples = ycc.getDecompressedSamples(_lum_bits,
			_chr_bits);
		return_image.getRaster().setPixels(x, y, ycc.getWidth(),
			ycc.getHeight(), samples);
	    }
	}

	return return_image;
    }

    public int getCrDiff(int _shift) {
	return (highCr >> _shift) - (lowCr >> _shift);
    }

    public int getCbDiff(int _shift) {
	return (highCb >> _shift) - (lowCb >> _shift);
    }
    
    private BufferedImage source_image;
    private int lowCr, lowCb, highCr, highCb;

}