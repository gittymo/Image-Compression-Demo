/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.mevanspn.imgext;

import java.awt.image.BufferedImage;
import uk.co.mevanspn.gui.ImagePreviewPanel;

/**
 *
 * @author morganevans
 */
final public class Bitmap extends Thread {

    public Bitmap(BufferedImage _image, ImagePreviewPanel _ipp) {
		source_image = _image;
		ipp = _ipp;
    }

    public void getCompressPreview(int _lum_bits, int _chr_bits, 
    	int _block_width, int _block_height) {
    	block_width = _block_width;
    	block_height = _block_height;
    	lum_bits = _lum_bits;
    	chr_bits = _chr_bits;
		start();
    }

	public void run() {
		try {
			return_image = new BufferedImage(source_image.getWidth(),
				source_image.getHeight(), source_image.getType());
			YCCBlock ycc = null;
			lowCr = 256; highCr = 0;
			lowCb = 256; highCb = 0;
			for (int y = 0; y < source_image.getHeight(); y += block_height) {
				for (int x = 0; x < source_image.getWidth(); x += block_width) {
					ycc = new YCCBlock(source_image, x, y, block_width,
						block_height);
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
					int[] samples = ycc.getDecompressedSamples(lum_bits,
						chr_bits);
					return_image.getRaster().setPixels(x, y, ycc.getWidth(),
						ycc.getHeight(), samples);
				}
				sleep(1);
			} 
		} catch (InterruptedException iex) {
				return_image = null;
		}
		
		ipp.setImage(return_image);
	}

    public int getCrDiff(int _shift) {
		return (highCr >> _shift) - (lowCr >> _shift);
    }

    public int getCbDiff(int _shift) {
		return (highCb >> _shift) - (lowCb >> _shift);
    }
    
    private BufferedImage source_image, return_image;
    private ImagePreviewPanel ipp;
    private int lowCr, lowCb, highCr, highCb;
	private int block_width, block_height, lum_bits, chr_bits;
}
