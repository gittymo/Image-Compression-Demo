/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.mevanspn.gui;

import java.awt.event.AdjustmentEvent;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import javax.swing.*;
import java.util.Vector;
import javax.swing.event.*;
import java.net.*;

/**
 * Creates instances of a scrollable panel that can be used to view a bitmap
 * image.  The panel allows for optional mouse-wheel scrolling or zooming as
 * well as drag and drop scrolling and in-frame zoom controls.
 * @author morganevans
 */
public class ImagePreviewPanel extends JScrollPane implements ImageZoomListener,
        ZoomManager {

    /**
     * Creates an instance of ImagePreviewPanel with no image.  The image can
     * be set programatically using the setImage method.  This constructor
     * defaults to wheel zoom enabled.
     * @throws java.io.IOException
     * @throws BadIconResourceException 
     */
    public ImagePreviewPanel() throws IOException, BadIconResourceException {
        super();
        zoom_listeners = new Vector<ImageZoomListener>();
        initComponent(true);
        registerZoomIcon(new ZoomIconControl("/uk/co/mevanspn/gui/resources/ZoomInActive.png",
                "/uk/co/mevanspn/gui/resources/ZoomIn.png", ZoomIconControl.ZOOM_IN));
        registerZoomIcon(new ZoomIconControl("/uk/co/mevanspn/gui/resources/ZoomOutActive.png",
                "/uk/co/mevanspn/gui/resources/ZoomOut.png", ZoomIconControl.ZOOM_OUT));
        setZoomIconsCorner(TOP_LEFT);
    }

    /**
     * Creates an instance of ImagePreviewPanel with no image.  The image can
     * be set programatically using the setImage method.  This constructor takes
     * an addition boolean parameter _wheel_scroll which determines if mouse-
     * wheel scrolling (=true) or mouse-wheel zooming (=false) is enabled.
     * @param _wheel_scroll
     * @throws java.io.IOException
     * @throws BadIconResourceException
     */
    public ImagePreviewPanel(boolean _wheel_scroll) throws IOException,
            BadIconResourceException {
        super();
        zoom_listeners = new Vector<ImageZoomListener>();
        initComponent(_wheel_scroll);
registerZoomIcon(new ZoomIconControl("/uk/co/mevanspn/gui/resources/ZoomInActive.png",
                "/uk/co/mevanspn/gui/resources/ZoomIn.png", ZoomIconControl.ZOOM_IN));
        registerZoomIcon(new ZoomIconControl("/uk/co/mevanspn/gui/resources/ZoomOutActive.png",
                "/uk/co/mevanspn/gui/resources/ZoomOut.png", ZoomIconControl.ZOOM_OUT));
        setZoomIconsCorner(TOP_LEFT);
    }

    /**
     * Creates an instance of ImagePreviewPanel that displays the image
     * referenced by the parameter _image.  Wheel zoom is enabled by default.
     * @param _image
     * @throws java.io.IOException
     * @throws BadIconResourceException 
     */
    public ImagePreviewPanel(BufferedImage _image) throws IOException,
            BadIconResourceException {
        super();
        zoom_listeners = new Vector<ImageZoomListener>();
        initComponent(false, _image);
registerZoomIcon(new ZoomIconControl("/uk/co/mevanspn/gui/resources/ZoomInActive.png",
                "/uk/co/mevanspn/gui/resources/ZoomIn.png", ZoomIconControl.ZOOM_IN));
        registerZoomIcon(new ZoomIconControl("/uk/co/mevanspn/gui/resources/ZoomOutActive.png",
                "/uk/co/mevanspn/gui/resources/ZoomOut.png", ZoomIconControl.ZOOM_OUT));
        setZoomIconsCorner(TOP_LEFT);
    }

    /**
     * Creates an instance of ImagePreviewPanel that displays the image
     * referenced by the parameter _image.  Zoom in/out via the mouse-wheel
     * can be enabled by setting the _wheel_scroll paramter to false, otherwise
     * mouse-wheel scrolling is enabled.
     * @param _wheel_scroll
     * @param _image
     * @throws java.io.IOException
     * @throws BadIconResourceException
     */
    public ImagePreviewPanel(BufferedImage _image, boolean _wheel_scroll)
            throws IOException, BadIconResourceException {
        super();
        zoom_listeners = new Vector<ImageZoomListener>();
        initComponent(_wheel_scroll, _image);
registerZoomIcon(new ZoomIconControl("/uk/co/mevanspn/gui/resources/ZoomInActive.png",
                "/uk/co/mevanspn/gui/resources/ZoomIn.png", ZoomIconControl.ZOOM_IN));
        registerZoomIcon(new ZoomIconControl("/uk/co/mevanspn/gui/resources/ZoomOutActive.png",
                "/uk/co/mevanspn/gui/resources/ZoomOut.png", ZoomIconControl.ZOOM_OUT));
        setZoomIconsCorner(TOP_LEFT);
    }

    public ImagePreviewPanel(ZoomIconControl _zoom_in_icon,
            ZoomIconControl _zoom_out_icon, int _icon_corner) throws IOException,
            BadIconResourceException {
        super();
        zoom_listeners = new Vector<ImageZoomListener>();
        initComponent(false);
        if (_zoom_in_icon.getZoom() != 9) {
            _zoom_in_icon.setZoom(9f);
        }
        registerZoomIcon(_zoom_in_icon);
        if (_zoom_out_icon.getZoom() != 0) {
            _zoom_out_icon.setZoom(0f);
        }
        registerZoomIcon(_zoom_out_icon);
        setZoomIconsCorner(_icon_corner);
    }

    public ImagePreviewPanel(ZoomIconControl _zoom_icons[], int _icon_corner)
            throws IOException, BadIconResourceException {
        super();
        zoom_listeners = new Vector<ImageZoomListener>();
        initComponent(false);
        for (int i = 0; i < _zoom_icons.length; i++) {
            registerZoomIcon(_zoom_icons[i]);
        }
        setZoomIconsCorner(_icon_corner);
    }

    public ImagePreviewPanel(BufferedImage _image, boolean _wheel_scroll,
            ZoomIconControl _zoom_in_icon, ZoomIconControl _zoom_out_icon,
            int _icon_corner) throws IOException, BadIconResourceException {
        super();
        zoom_listeners = new Vector<ImageZoomListener>();
        initComponent(_wheel_scroll, _image);
        if (_zoom_in_icon.getZoom() != 9) {
            _zoom_in_icon.setZoom(9f);
        }
        registerZoomIcon(_zoom_in_icon);
        if (_zoom_out_icon.getZoom() != 0) {
            _zoom_out_icon.setZoom(0f);
        }
        registerZoomIcon(_zoom_out_icon);
        setZoomIconsCorner(_icon_corner);
    }

    public ImagePreviewPanel(BufferedImage _image, boolean _wheel_scroll,
            ZoomIconControl _zoom_icons[], int _icon_corner) throws IOException,
            BadIconResourceException {
        super();
        zoom_listeners = new Vector<ImageZoomListener>();
        initComponent(_wheel_scroll, _image);
        for (int i = 0; i < _zoom_icons.length; i++) {
            registerZoomIcon(_zoom_icons[i]);
        }
        setZoomIconsCorner(_icon_corner);
    }

    private void initComponent(boolean _wheel_scroll) throws IOException,
            BadIconResourceException {
        setPreferredSize(new Dimension(320, 240));
        image_panel = new ImagePanel(this);
        setViewportView(image_panel);
        getViewport().addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                image_panel.configureZoomIcons();
            }
        });

        getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {

            public void adjustmentValueChanged(AdjustmentEvent arg0) {
                if (!image_panel.dragScrollActive()) {
                    image_panel.setScrollBarActive(arg0.getValueIsAdjusting());
                }
            }
        });

        getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

            public void adjustmentValueChanged(AdjustmentEvent arg0) {
                if (!image_panel.dragScrollActive()) {
                    image_panel.setScrollBarActive(arg0.getValueIsAdjusting());
                }
            }
        });

        setWheelZoomEnabled(!_wheel_scroll);
    }

    private void initComponent(boolean _wheel_scroll, BufferedImage _image)
            throws IOException, BadIconResourceException {
        initComponent(_wheel_scroll);
        image_panel.setImage(_image);
    }

    /**
     * Sets the image to be displayed in the ImagePreviewPanel component.
     * @param _image
     */
    public void setImage(BufferedImage _image) {
        image_panel.setImage(_image);
    }

    /**
     * Returns the image being displayed in the ImagePreviewPanel component.
     * @return
     */
    public BufferedImage getImage() {
        return image_panel.getImage();
    }

    public void setZoom(float _zoom) {
        image_panel.setZoom(_zoom);
    }

    public float getZoom() {
        return image_panel.getZoom();
    }

    public void setWheelZoomEnabled(boolean _wheel_zoom) {
        if (_wheel_zoom) {
            setWheelScrollingEnabled(false);
        } else {
            setWheelScrollingEnabled(true);
        }

        image_panel.setWheelZoomEnabled(_wheel_zoom);
    }

    public boolean getWheelZoomEnabled() {
        return image_panel.getWheelZoomEnabled();
    }

    /**
     * Adds an implementor of the ImageZoomListener to the components list of
     * listeners to be notified of changes to the magnification level.
     * @param _izl
     */
    public void addImageZoomListener(ImageZoomListener _izl) {
        boolean found = false;
        for (ImageZoomListener izl : zoom_listeners) {
            if (izl == _izl) {
                found = true;
                break;
            }
        }

        if (!found) {
            zoom_listeners.add(_izl);
        }
    }

    /**
     * Removes the given ImageZoomListener from the list of magnification change
     * listeners.
     * @param _izl
     */
    public void removeImageZoomListener(ImageZoomListener _izl) {
        for (ImageZoomListener izl : zoom_listeners) {
            if (izl == _izl) {
                zoom_listeners.remove(_izl);
                break;
            }
        }
    }

    public void setZoomIconsEnabled(boolean _zce) {
        image_panel.setZoomIconsEnabled(_zce);
    }

    public boolean getZoomIconsEnabled() {
        return image_panel.getZoomIconsEnabled();
    }

    public void setZoomIconsCorner(int _corner) {
        image_panel.setZoomIconsCorner(_corner);
    }

    public int getZoomIconsCorner() {
        return image_panel.getZoomIconsCorner();
    }

    public void registerZoomIcon(ZoomIconControl _zic) {
        image_panel.registerZoomIcon(_zic);
    }

    public void zoomChanged(float _new_zoom, ZoomManager _sender) {
        for (ImageZoomListener izl : zoom_listeners) {
            izl.zoomChanged(_new_zoom, this);
        }
    }

     public void zoomToFit() {
        image_panel.zoomToFit();
    }

    public void zoomIn() {
        image_panel.zoomIn();
    }

    public void zoomOut() {
        image_panel.zoomOut();
    }

    private ImagePanel image_panel;
    private Vector<ImageZoomListener> zoom_listeners;
    
    public static final int MOUSE_BUTTON_LEFT = 1;
    public static final int MOUSE_BUTTON_MIDDLE = 2;
    public static final int MOUSE_BUTTON_RIGHT = 3;
    public static final int MOUSE_BUTTON_ANY = 0;
}
