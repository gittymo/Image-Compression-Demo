/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.mevanspn.gui;

/**
 * The ZoomManager interface provides most of the methods used to control the
 * display of the image within an ImagePreviewPanel.  It also provides facilities
 * for the calling application to notify other objects - usually other UI
 * components of changes to the magnification level used to display the image.
 * @author morganevans
 */
public interface ZoomManager {
    /**
     * Set the required magnification to the display the image.
     * @param _zoom
     * Magnification in human readable form.  i.e. 100 = 100%
     */
    public void setZoom(float _zoom);
    /**
     * Returns the magnification used to display the image.
     * @return
     * The current magnification.
     */
    public float getZoom();
    /**
     * Will set the magnification of the image so that it fits exactly inside
     * its parent component.
     */
    public void zoomToFit();
    /**
     * Will increase the magnification of the image.  If the original
     * magnification is less than 100%, the magnification is doubled.  If the
     * magnification is above 100%, it is increased by 100% each call thereafter.
     */
    public void zoomIn();
    /**
     * Will decrease the magnification of the image.  If the magnification is
     * above 100%, it is decreased by 100% each call thereafter, once the
     * magnification is less than 100%, the magnification is halved to a limit
     * of 1% of the original size.
     */
    public void zoomOut();
    /**
     * Will enable / disable zooming in/out of the image using the wheel mouse,
     * wheel scrolling is disabled / enabled as the alternative usage.
     * @param _wheel_zoom_enabled
     * (true) = Wheel zoom enabled, wheel scroll disabled.
     * (False) = Wheel zoom disabled, wheel scroll enabled.
     *
     */
    public void setWheelZoomEnabled(boolean _wheel_zoom_enabled);
    /**
     * Indicates if wheel zooming is enabled.
     * @return
     */
    public boolean getWheelZoomEnabled();
    /**
     * Adds a zoom icon control to the ImagePreviewPanel.
     * @param _icon
     */
    public void registerZoomIcon(ZoomIconControl _icon);
    /**
     * Sets the corner used to display the zoom icon controls.  Must be one of
     * the four constants defined by this interface.  Spurious values will
     * default to TOP_LEFT;
     * @param _corner
     */
    public void setZoomIconsCorner(int _corner);
    /**
     * Returns the corner used to display the zoom icon controls.
     * @return
     */
    public int getZoomIconsCorner();
    /**
     * Used to control the display and function of the zoom icon controls.
     * @param _enabled
     * (true) = icons visible and active, (false) = no icons.
     */
    public void setZoomIconsEnabled(boolean _enabled);
    /**
     * Indicates whether zoom icon controls are to be active.
     * @return
     */
    public boolean getZoomIconsEnabled();
    
    /**
     * Indicates that zoom icons should appear from the top-left corner of the
     * container component.
     */
    public int TOP_LEFT = 0;
    /**
     * Indicates that zoom icons should appear from the top-right corner of the
     * container component.
     */
    public int TOP_RIGHT = 1;
    /**
     * Indicates that zoom icons should appear from the bottom-right corner of
     * the container component.
     */
    public int BOTTOM_RIGHT = 2;
    /**
     * Indicates that zoom icons should appear from the bottom-left corner of
     * the container component.
     */
    public int BOTTOM_LEFT = 3;
}
