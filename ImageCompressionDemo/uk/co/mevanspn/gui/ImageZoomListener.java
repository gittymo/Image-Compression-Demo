/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.mevanspn.gui;

/**
 * Classes which implement the ImageZoomListener interface can register with
 * ImagePreviewPanel updates in order to ascertain when the magnification for
 * the displayed image has been changed via one of ImagePreviewPanel's UI
 * methods (mouse-wheel zoom or the internal icon-based zoom controls).
 *
 * Usually this method would be used to update other application-resident user
 * interface elements such as a combobox or label to give the user a text-based
 * visual indication of the current magnification.
 * @author morganevans
 */
public interface ImageZoomListener {
    /**
     * Indicates that an object (_sender) implementing the ZoomManager 
     * interface has changed the magnification level used to display the image
     * to _new_zoom.
     * @param _new_zoom
     * @param _sender
     */
    public void zoomChanged(float _new_zoom, ZoomManager _sender);
}
