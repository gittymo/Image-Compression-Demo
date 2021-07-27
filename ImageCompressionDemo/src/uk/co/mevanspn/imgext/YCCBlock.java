/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.mevanspn.imgext;

/**
 *
 * @author morganevans
 */
final public class YCCBlock {

    public YCCBlock(java.awt.image.BufferedImage _image, int _x, int _y,
	    int _w, int _h) {
	x = (_x < 0) ? (-_x >= _image.getWidth()) ? _image.getWidth() - 1 : -_x : _x;
	y = (_y < 0) ? (-_y >= _image.getHeight()) ? _image.getHeight() - 1 : -_y : _y;
	w = (_w < 1) ? (_w == 0) ? _image.getWidth() - x : (-_w + x >= _image.getWidth()) ? _image.getWidth() - x : -_w : (_w + x >= _image.getWidth()) ? _image.getWidth() - x : _w;
	h = (_h < 1) ? (_h == 0) ? _image.getHeight() - y : (-_h + y >= _image.getHeight()) ? _image.getHeight() - y : -_h : (_h + y >= _image.getHeight()) ? _image.getHeight() - y : _h;
	final int area = w * h;
	cr = cb = 0;
	lum = new int[area];
	generateBlock(_image.getRGB(x, y, w, h, null, 0, w));
    }

    public int getX() {
	return x;
    }

    public int getY() {
	return y;
    }

    public int getWidth() {
	return w;
    }

    public int getHeight() {
	return h;
    }

    public float getCr() {
	return cr;
    }

    public float getCb() {
	return cb;
    }

    public int[] getDecompressedSamples(int _lum_bits, int _chr_bits) {
	_lum_bits = (_lum_bits < 1) ? (_lum_bits == 0) ? 8 : (-_lum_bits > 8) ? _lum_bits = 8 : -_lum_bits : _lum_bits;
	_chr_bits = (_chr_bits < 1) ? (_chr_bits == 0) ? 8 : (-_chr_bits > 8) ? _chr_bits = 8 : -_chr_bits : _chr_bits;

	/* Determine the number of bits required to hold the difference between
	 * the highest and lowest luminance values. */
	int bit_val = 1, bit;
	for (bit = 1; bit < 8 && bit_val < high_lum - low_lum; bit++) {
	    bit_val += 1 << bit;
	}
	if (bit < _lum_bits) {
	    _lum_bits = bit;
	}

	// Determine bit shifts for compression
	final int lum_shift = bit - _lum_bits;
	final int chr_shift = 8 - _chr_bits;

	// Convert chrominance value pair to byte scaled equivalent.
	int cr_byte = cr >> chr_shift;
	int cb_byte = cb >> chr_shift;

	// Convert luminance values to byte scaled equivalents.
	final int area = w * h;
	int lum_byte[] = new int[area];
	for (int i = 0; i < area; i++) {
	    lum_byte[i] = lum[i] >> lum_shift;
	}

	// Revert byte scaled values back to float YCC values again...
	int dec_cr = (cr_byte << chr_shift) - 128;
	int dec_cb = (cb_byte << chr_shift) - 128;
	int[] dec_lum = new int[area];
	for (int i = 0; i < area; i++) {
	    dec_lum[i] = (lum_byte[i] << lum_shift) + low_lum;
	}

	// .. and then back to RGB values.
	int[] return_values = new int[area * 3];
	final int i1cbcr = (int) ((0.344 * dec_cb) + (0.714f * dec_cr));
	final int icr = (int) (1.403f * dec_cr);
	final int i2cb = (int) (1.773f * dec_cb);
	int in = 0;
	for (int i = 0; i < area * 3; i += 3) {
	    return_values[i] = dec_lum[in] + icr;
	    return_values[i + 1] += dec_lum[in] - i1cbcr;
	    return_values[i + 2] += dec_lum[in] + i2cb;
	    in++;
	}

	for (int i = 0; i < area * 3; i++) {
	    if (return_values[i] < 0) {
		return_values[i] = 0;
	    }
	    if (return_values[i] > 255) {
		return_values[i] = 255;
	    }
	}

	return return_values;
    }

    private void generateBlock(int[] _samples) {
	if (lum_pre == null) {
	    lum_pre = new int[3][256];
	    cr_pre = new int[3][256];
	    cb_pre = new int[3][256];
	    for (int i = 0; i < 256; i++) {
		lum_pre[0][i] = (int) (0.299 * i);
		lum_pre[1][i] = (int) (0.587 * i);
		lum_pre[2][i] = (int) (0.114 * i);
		cb_pre[0][i] = (int) (-0.169 * i);
		cb_pre[1][i] = (int) (-0.331 * i);
		cb_pre[2][i] = (int) (0.5 * i);
		cr_pre[0][i] = (int) (0.5 * i);
		cr_pre[1][i] = (int) (-0.419 * i);
		cr_pre[2][i] = (int) (-0.081 * i);
	    }
	}

	int total_cr = 0, total_cb = 0;
	final int sc = _samples.length;
	int red[] = new int[sc];
	int green[] = new int[sc];
	int blue[] = new int[sc];
	for (int i = 0; i < sc; i++) {
	    red[i] = (_samples[i] & RED_MASK) >> 16;
	    green[i] = (_samples[i] & GREEN_MASK) >> 8;
	    blue[i] = _samples[i] & BLUE_MASK;
	    total_cb += cb_pre[0][red[i]] + cb_pre[1][green[i]] +
		    cb_pre[2][blue[i]];
	    total_cr += cr_pre[0][red[i]] + cr_pre[1][green[i]] +
		    cr_pre[2][blue[i]];

	}

	cr = (total_cr / sc) + 128;
	cb = (total_cb / sc) + 128;
	if (cr > 255) {
	    cr = 255;
	}
	if (cb > 255) {
	    cb = 255;
	}

	low_lum = 255;
	high_lum = 0;
	for (int i = 0; i < sc; i++) {
	    lum[i] = lum_pre[0][red[i]] + lum_pre[1][green[i]] +
		    lum_pre[2][blue[i]];
	    if (lum[i] > 255) {
		lum[i] = 255;
	    }
	    if (lum[i] > high_lum) {
		high_lum = lum[i];
	    }
	    if (lum[i] < low_lum) {
		low_lum = lum[i];
	    }
	}

	for (int i = 0; i < sc; i++) {
	    lum[i] -= low_lum;
	}
    }

    public void setPixels(java.awt.image.BufferedImage _image,
	    int _lum_bits, int _chr_bits) {
	_image.getRaster().setPixels(x, y, w, h,
		getDecompressedSamples(_lum_bits, _chr_bits));
    }

    private int lum[], low_lum, high_lum, cr, cb;
    private int x, y, w, h;
    final static int RED_MASK = 255 << 16;
    final static int GREEN_MASK = 255 << 8;
    final static int BLUE_MASK = 255;
    private static int[][] lum_pre = null;
    private static int[][] cb_pre = null;
    private static int[][] cr_pre = null;
}
