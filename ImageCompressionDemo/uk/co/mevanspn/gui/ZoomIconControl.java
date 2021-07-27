/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.mevanspn.gui;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;

/**
 * Instances of ZoomIconControl are used to represent the clickable icons used
 * by the ImagePreviewPanel in-display user interface.  This class has been made
 * public to make it simple for alternative icons to the default zoom in/out
 * pair, both in terms of being able to add fixed magnification facilities and
 * in the aesthetic sense where the default icon style may not be appropriate.
 * @author morganevans
 */
final public class ZoomIconControl {
    /**
     * Creates an instance of a ZoomIconControl whose icon images are stored in
     * the files pointed to by _active_icon_filename and _inactive_icon_filename.
     * Any image format that the J2SE RTE understands can be loaded.  By default
     * this icon will allow ImagePreviewPanel objects to apply the magnification
     * level represented by _zoom_factor incrementally, increasing/decreasing
     * the resulting image size each time the icon is clicked.
     * @param _active_icon_filename
     * String representing the filename or URL of the active icon image.
     * @param _inactive_icon_filename
     * String representing the filename of URL of the inactive icon image.
     * @param _zoom_factor
     * A floating point value representing the manification level associated with 
     * this icon.  Values of 0 (zero) or less will be ignored and be replace by
     * the default value of 1 (100%).
     * Static class constants ZOOM_IN and ZOOM_OUT can also be used.  These
     * are special flag values that allow ImagePreviewPanel to perform non-linear
     * zoom in/out operations.
     * @throws java.io.IOException
     * Thrown when one of the image files cannot be read.
     * @throws uk.co.mevanspn.gui.BadIconResourceException
     * Thrown when the dimensions of the active/inactive image files differ.
     */
    public ZoomIconControl(String _active_icon_filename,
	    String _inactive_icon_filename, float _zoom_factor)
	    throws IOException, BadIconResourceException {
	inactive_icon = getResource(_inactive_icon_filename);
	active_icon = getResource(_active_icon_filename);
	fixed_zoom = false;
	setZoom(_zoom_factor);
	checkIcons();
    }


    /**
     * Creates an instance of a ZoomIconControl whose icon images are stored in
     * the files pointed to by _active_icon_filename and _inactive_icon_filename.
     * Any image format that the J2SE RTE understands can be loaded.
     * The parameter _fixed_zoom allows the calling method to allow for fixed or
     * incremental magnification.  Fixed magnification means that the resulting
     * output will ALWAYS be of the original size multiplied by the magnitude,
     * whereas incremental will increase/decrease the output by the magnitude
     * given every time the icon is clicked.
     * @param _active_icon_filename
     * String representing the filename or URL of the active icon image.
     * @param _inactive_icon_filename
     * String representing the filename of URL of the inactive icon image.
     * @param _zoom_factor
     * A floating point value representing the manification level associated with
     * this icon.  Values of 0 (zero) or less will be ignored and be replace by
     * the default value of 1 (100%).
     * @param _fixed_zoom
     * A boolean used to indicate whether fixed (true) or incremental (false)
     * magnification should be used when the icon is clicked.
     * @throws java.io.IOException
     * Thrown when one of the image files cannot be read.
     * @throws uk.co.mevanspn.gui.BadIconResourceException
     * Thrown when the dimensions of the active/inactive image files differ.
     */
    public ZoomIconControl(String _active_icon_filename,
	    String _inactive_icon_filename, float _zoom_factor, boolean _fixed_zoom)
	    throws IOException, BadIconResourceException {
	inactive_icon = getResource(_inactive_icon_filename);
	active_icon = getResource(_active_icon_filename);
	fixed_zoom = _fixed_zoom;
	setZoom(_zoom_factor);
	checkIcons();
    }

    private void checkIcons() throws BadIconResourceException {
	if (active_icon == null || inactive_icon == null ||
		active_icon.getWidth() != inactive_icon.getWidth() ||
		active_icon.getHeight() != inactive_icon.getHeight()) {
	    throw new BadIconResourceException();
	}
    }

    private BufferedImage getResource(String _url) throws IOException {
	URL u = getClass().getResource(_url);
	if (u != null) {
	    return ImageIO.read(u);
	}
	return null;
    }

    /**
     * Returns the width of the icon.
     * @return
     * The width of the icon in pixels.
     */
    public int getWidth() {
	return active_icon.getWidth();
    }

    /**
     * Returns the height of the icon.
     * @return
     * The height of the icon in pixels.
     */
    public int getHeight() {
	return active_icon.getHeight();
    }

    /**
     * Used internally by ImagePreviewPanel to set the top-left corner for
     * displaying the image in the underlying container.
     * @param _origin
     */
    protected void setOrigin(Point _origin) {
	origin = _origin;
    }

    /**
     * Returns the top-left corner, or origin, of the icon relative to its
     * underlying container.
     * @return
     * Point object representing the origin of the icon relative to its container.
     */
    public Point getOrigin() {
	return origin;
    }


    /**
     * Used internally by ImagePreviewPanel to perform image the image rollover/
     * rollout operations for the UI.
     * @param _mouse_x
     * @param _mouse_y
     * @return
     */
    protected BufferedImage getImage(int _mouse_x, int _mouse_y) {
	if (_mouse_x >= origin.x && _mouse_x < origin.x + active_icon.getWidth() &&
		_mouse_y >= origin.y && _mouse_y <= origin.y + active_icon.getHeight()) {
	   return active_icon;
	}

	return inactive_icon;
    }

    /** Sets the magnification level represented by this icon.
     * @param _zoom_factor
     * The new magnification level that this icon will be associated with.
     */
    protected void setZoom(float _zoom_factor) {
        if (_zoom_factor == 0) {
            zoom_factor = 1;
        } else if (_zoom_factor < 0 && _zoom_factor < -2) {
            zoom_factor = -_zoom_factor;
        } else {
            zoom_factor = _zoom_factor;
        }
    }

    /**
     * Returns the magnification level this icon is associated with.
     * @return
     * The magnification level.  May equal one of the static members ZOOM_IN or
     * ZOOM_OUT which has special purpose to ImagePreviewPanel objects.
     */
    public float getZoom() {
	return zoom_factor;
    }

    /**
     * Indicates if the magnification is fixed or incremental.
     * @return
     * Returns true if magnification is fixed or false if magnification is
     * incremental.
     */
    public boolean isFixedZoom() {
        return fixed_zoom;
    }

    private BufferedImage active_icon, inactive_icon;
    private ImagePreviewPanel ipp;
    private float zoom_factor;
    private boolean fixed_zoom;
    private Point origin;

    /**
     * Can be used to define the zoom function of an icon to specifically call
     * the zoomIn() method of an implementor of the ZoomManager interface.
     */
    public static final float ZOOM_IN = -1f;
    /**
     * Can be used to define the zoom function of an icon to specifically call
     * the zoomOut() method of an implementor of the ZoomManager interface.
     */
    public static final float ZOOM_OUT = -2f;
}
