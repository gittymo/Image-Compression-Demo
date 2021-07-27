/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.mevanspn.gui;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author morganevans
 */
final class ImagePanel extends JPanel implements MouseListener,
	MouseMotionListener, MouseWheelListener, Scrollable, ZoomManager,
	ComponentListener {

    protected ImagePanel(ImageZoomListener _client) throws IOException {
	super();
	client = _client;
	image = null;
	drag_scroll_active = false;
	zoom_icons = new Vector<ZoomIconControl>();
	trans_man = new TransitionManager(this);
	max_icon_height = -1;
	zoom = 100;
        last_view_rectangle = null;
        scroll_bar_active = false;
	initComponent();
	trans_man.start();
    }

    protected void setScrollBarActive(boolean _active) {
        scroll_bar_active = _active;
        if (image != null && !zoom_icons_disabled) {
            if (_active && zoom_icons_visible) {
                trans_man.setControlOpacity(0f);
            } else {
                trans_man.beginTransition(0.1f);
            }
        }
    }

    private void initComponent() throws IOException {
	setBorder(new EmptyBorder(4, 4, 4, 4));
	setWheelZoomEnabled(true);
	addMouseListener(this);
	addMouseMotionListener(this);
	addComponentListener(this);
    }

    void setImage(BufferedImage _image) {
	if (_image != null) {
	    image = _image;
	    setZoom(zoom);
	}
    }

    BufferedImage getImage() {
	return image;
    }

    boolean dragScrollActive() {
	return drag_scroll_active;
    }

    void configureZoomIcons() {
	final Rectangle vpr = ((JViewport) getParent()).getViewRect();
        if (!vpr.equals(last_view_rectangle)) {
            switch (zoom_icons_corner) {
                case TOP_LEFT: {
                    corner_origin = new Point(vpr.x + 8, vpr.y + 8);
                }
                break;

                case TOP_RIGHT: {
                    corner_origin = new Point((vpr.x + vpr.width) - 8, vpr.y + 8);
                }
                break;

                case BOTTOM_RIGHT: {
                    corner_origin = new Point((vpr.x + vpr.width) - 8,
                            (vpr.y + vpr.height) - (8 + max_icon_height));
                }
                break;

                case BOTTOM_LEFT: {
                    corner_origin = new Point(vpr.x + 8, (vpr.y + vpr.height) - (8 + max_icon_height));
                }
            }

            if (zoom_icons.size() > 0) {
                int icon_x = corner_origin.x;
                int icon_y = corner_origin.y;
                for (ZoomIconControl zic : zoom_icons) {
                    switch (zoom_icons_corner) {
                        case TOP_LEFT : {
                            zic.setOrigin(new Point(icon_x, icon_y));
                            icon_x += zic.getWidth() + 4;
                            if (icon_x >= (vpr.x + vpr.width) - 8) {
                                icon_x = corner_origin.x;
                                icon_y += max_icon_height + 4;
                            }
                        } break;

                        case BOTTOM_LEFT : {
                            zic.setOrigin(new Point(icon_x, icon_y));
                            icon_x += zic.getWidth() + 4;
                            if (icon_x >= (vpr.x + vpr.width) - 8) {
                                icon_x = corner_origin.x;
                                icon_y -= max_icon_height + 4;
                            }
                        } break;

                        case TOP_RIGHT : {
                            icon_x -= zic.getWidth();
                            zic.setOrigin(new Point(icon_x, icon_y));
                            icon_x -= 4;
                            if (icon_x <= vpr.x + 4) {
                                icon_x = corner_origin.x;
                                icon_y += max_icon_height + 4;
                            }
                        } break;

                        case BOTTOM_RIGHT : {
                            icon_x -= zic.getWidth();
                            zic.setOrigin(new Point(icon_x, icon_y));
                            icon_x -= 4;
                            if (icon_x < vpr.x + 4) {
                                icon_x = corner_origin.x;
                                icon_y -= max_icon_height + 4;
                            }
                        }
                    }
                }
            }
            last_view_rectangle = vpr;
        }
    }

    /* Methods derived from ZoomManager interface. */
    public void setZoom(float _zoom) {
		final float old_zoom = zoom / 100f;
		zoom = (_zoom == 0) ? 100 : ((_zoom < 0) ? -_zoom : _zoom);
        if (zoom < 1) {
            zoom = 1;
        }
		if (image != null) {
			final float scaled_zoom = zoom / 100f;
			JViewport vp = (JViewport) getParent();
			Rectangle r = vp.getViewRect();
			int scaled_x, scaled_y;
			scaled_x = (int) ((r.x + (r.width / 2)) * (1 / old_zoom));
			scaled_y = (int) ((r.y + (r.height / 2)) * (1 / old_zoom));
			scaled_x = (int) (scaled_x * scaled_zoom) - (r.width / 2);
			scaled_y = (int) (scaled_y * scaled_zoom) - (r.height / 2);
			int new_width = (int) (image.getWidth() * scaled_zoom) + 8;
			int new_height = (int) (image.getHeight() * scaled_zoom) + 8;
			if (new_width < r.width) {
				new_width = r.width;
				scaled_x = 0;
			}
			if (new_height < r.height) {
				new_height = r.height;
				scaled_y = 0;
			}
			setPreferredSize(new Dimension(new_width, new_height));
			vp.setViewSize(new Dimension(new_width, new_height));
			vp.setViewPosition(new Point(scaled_x, scaled_y));
		}

        client.zoomChanged(zoom, this);
    }

    public float getZoom() {
	return zoom;
    }

    public void zoomToFit() {
	final float image_width = image.getWidth();
	final float image_height = image.getHeight();
	final float viewport_width = getWidth() - 8;
	final float viewport_height = getHeight() - 8;
	setZoom((image_width > image_height) ? ((image_width > viewport_width) ? viewport_width / image_width : 1) : ((image_height > viewport_height) ? viewport_height /
		image_height : 1) * 100);
    }
    
    public void zoomIn() {
	if (zoom < 100) {
	    setZoom(zoom * 2);
	} else {
	    setZoom(getZoom() + 100);
	}
    }

    public void zoomOut() {
	if (zoom >= 200) {
	    setZoom(zoom - 100);
	} else {
	    if (zoom > 1) {
		setZoom(zoom / 2);
	    }
	}
    }

    public void setWheelZoomEnabled(boolean _wheel_zoom) {
	if (_wheel_zoom) {
	    if (!wheel_zoom_enabled) {
		addMouseWheelListener(this);
	    }
	} else {
	    if (wheel_zoom_enabled) {
		removeMouseWheelListener(this);
	    }
	}

	wheel_zoom_enabled = _wheel_zoom;
    }

    public boolean getWheelZoomEnabled() {
	return wheel_zoom_enabled;
    }

    public void registerZoomIcon(ZoomIconControl _icon) {
	zoom_icons.add(_icon);
	if (_icon.getHeight() > max_icon_height) {
	    max_icon_height = _icon.getHeight();
	}
    }

    public void setZoomIconsCorner(int _corner) {
	if (_corner != zoom_icons_corner) {
	    _corner = (_corner < 0) ? -_corner : _corner;
	    _corner = (_corner > 3) ? 3 : _corner;
	    zoom_icons_corner = _corner;
	}

        last_view_rectangle  = null;
	configureZoomIcons();
    }

    public int getZoomIconsCorner() {
	return zoom_icons_corner;
    }

    public void setZoomIconsEnabled(boolean _enabled) {
	zoom_icons_disabled = !_enabled;
    }

    public boolean getZoomIconsEnabled() {
	return !zoom_icons_disabled;
    }

    /* Methods overwriting those of JPanel class. */
    @Override
    public void paint(Graphics g) {
	super.paint(g);
	Graphics2D g2 = (Graphics2D) g;

	final float scaled_zoom = zoom / 100;
	if (image != null) {
	    final int image_width = (int) (image.getWidth() * scaled_zoom);
	    final int image_height = (int) (image.getHeight() * scaled_zoom);
	    final int x_offset = (getWidth() - image_width) / 2;
	    final int y_offset = (getHeight() - image_height) / 2;
	    g2.drawImage(image, x_offset, y_offset, image_width, image_height,
		    getBackground(), null);
	    if (!zoom_icons_disabled && zoom_icons_visible &&
		    zoom_icons.size() > 0) {
		AlphaComposite ac = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, trans_man.getControlOpacity());
		g2.setComposite(ac);
		for (ZoomIconControl zic : zoom_icons) {
		    final Point p = zic.getOrigin();
		    g2.drawImage(zic.getImage(mouse_x, mouse_y), p.x, p.y, null);
		}
	    }
	}
    }

    @Override
    public Dimension getPreferredSize() {
	final float scaled_zoom = zoom / 100;
	Dimension d = new Dimension(32, 32);
	if (image != null) {
	    JViewport vp = (JViewport) getParent();
	    Rectangle r = vp.getViewRect();
	    final int width = (image.getWidth() * scaled_zoom > r.width) ? (int) (image.getWidth() * scaled_zoom) : r.width;
	    final int height = (image.getHeight() * scaled_zoom > r.height) ? (int) (image.getHeight() * scaled_zoom) : r.height;
	    d = new Dimension(width, height);
	}
	return d;
    }

    /*  Methods derived from Scrollable interface. */
    public Dimension getPreferredScrollableViewportSize() {
	return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
	return 4;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
	return 16;
    }

    public boolean getScrollableTracksViewportWidth() {
	return false;
    }

    public boolean getScrollableTracksViewportHeight() {
	return false;
    }

    /* Methods derived from MouseListener, MouseMotionListener and
       MouseWheelListener interfaces. */
    public void mouseClicked(MouseEvent e) {
	if (image != null && !zoom_icons_disabled && zoom_icons_visible) {
	    for (ZoomIconControl zic : zoom_icons) {
		Point p = zic.getOrigin();
		if (e.getX() >= p.x && e.getX() < p.x + zic.getWidth() &&
			e.getY() >= p.y && e.getY() < p.y + zic.getHeight()) {
		    float zf = zic.getZoom();
		    if (zf == ZoomIconControl.ZOOM_IN) {
                        zoomIn();
                    } else if (zf == ZoomIconControl.ZOOM_OUT) {
                        zoomOut();
                    } else {
                        if (zic.isFixedZoom()) {
                            setZoom(zic.getZoom() * 100);
                        } else {
                            setZoom(getZoom() * zic.getZoom());
                        }
                    }
		}
	    }
	}
    }

    public void mousePressed(MouseEvent e) {
	if (image != null) {
	    last_x = e.getX();
	    last_y = e.getY();
	}
    }

    public void mouseReleased(MouseEvent e) {
	if (image != null) {
	    drag_scroll_active = false;
	    if (!mouse_outside) {
                trans_man.beginTransition(0.1f);
            }
	}
    }

    public void mouseEntered(MouseEvent e) {
	if (image != null && !scroll_bar_active) {
	    zoom_icons_visible = true;
	    mouse_outside = false;
            trans_man.beginTransition(0.1f);
	}
    }

    public void mouseExited(MouseEvent e) {
	if (image != null) {
	    trans_man.beginTransition(-0.1f);
	}
    }

    public void mouseDragged(MouseEvent e) {
        if (!drag_scroll_active) {
            drag_scroll_active = true;
            trans_man.beginTransition(-1f);
        }

	if (image != null) {
	    JViewport jv = (JViewport) getParent();
	    Point p = jv.getViewPosition();
	    int newx = p.x - (e.getX() - last_x);
	    int newy = p.y - (e.getY() - last_y);
	    int maxx = (getWidth() - jv.getWidth());
	    int maxy = (getHeight() - jv.getHeight());
	    if (newx < 0) {
		newx = 0;
	    }
	    if (newx > maxx) {
		newx = maxx;
	    }
	    if (newy < 0) {
		newy = 0;
	    }
	    if (newy > maxy) {
		newy = maxy;
	    }
	    jv.setViewPosition(new Point(newx, newy));
	}
    }

    public void mouseMoved(MouseEvent e) {
	if (!drag_scroll_active) {
	    mouse_x = e.getX();
	    mouse_y = e.getY();
            if (!zoom_icons_disabled && !zoom_icons_visible) {
                trans_man.beginTransition(0.1f);
            }
	}
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
	int mouse_roll = e.getWheelRotation();
	if (mouse_roll < 0) {
	    zoomIn();
	} else {
	    zoomOut();
	}
    }

    /*  Methods derived from ComponentListener interface. */
    public void componentShown(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
	configureZoomIcons();
    }

    public void componentMoved(ComponentEvent e) {
    }

    /*  Class members. */
    private BufferedImage image;
    private float zoom;
    private boolean wheel_zoom_enabled, zoom_icons_visible;
    private boolean drag_scroll_active, mouse_outside;
    private boolean drag_scroll_disabled, zoom_icons_disabled;
    private int last_x, last_y, zoom_icons_corner, max_icon_height;
    private int mouse_x = 0, mouse_y = 0;
    private ImageZoomListener client;
    private Vector<ZoomIconControl> zoom_icons;   
    private boolean scroll_bar_active;
    private Point corner_origin;
    private TransitionManager trans_man;
    private Rectangle last_view_rectangle;

    /*  Internal class TransitionManager deals with zoom icon transition effects.*/
    class TransitionManager extends Thread {

	TransitionManager(ImagePanel _imp) {
	    control_opacity = 0;
	    forced_to_die = false;
	    imp = _imp;
	}

	@Override
	public void run() {
	    while (!forced_to_die) {
		try {
		    if (update) {
			zoom_icons_visible = true;
			if (transition_speed > 0 && control_opacity < 1) {
			    control_opacity += transition_speed;
			    if (control_opacity > 1) {
				control_opacity = 1;
				transition_speed = 0;
				update = false;
			    }
			} else if (transition_speed < 0 && control_opacity > 0) {
			    control_opacity += transition_speed;
			    if (control_opacity < 0) {
				control_opacity = 0;
				transition_speed = 0;
				update = false;
			    }
			}
			sleep(40);
                        repaint();
		    } else {
			if (control_opacity == 0 && zoom_icons_visible) {
			    zoom_icons_visible = false;
			}
			sleep(80);
		    }
		} catch (InterruptedException iex) {
		}
	    }
	}

	float getControlOpacity() {
	    return control_opacity;
	}

        void setControlOpacity(float _new_opacity) {
            control_opacity = _new_opacity;
            if (control_opacity == 0) {
                zoom_icons_visible = false;
            }
            repaint();
        }
        
	void beginTransition(float _transition_speed) {
	    update = true;
	    transition_speed = _transition_speed;
	}

	void killProcess() {
	    forced_to_die = true;
	}

	private boolean update, forced_to_die;
	private float control_opacity, transition_speed;
	private ImagePanel imp;
    }
}
