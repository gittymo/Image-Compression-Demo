/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.mevanspn.imgext;

import java.io.File;

/**
 *
 * @author morganevans
 */
public class ImageFileFilter extends javax.swing.filechooser.FileFilter {

    public ImageFileFilter() {
	filters = new Filter[2];
	filters[0] = new Filter("JPEG JFIF Lossy Compressed Image Format", ".jpg",
		"jpeg");
	filters[1] = new Filter("PNG Lossless Image Format", ".png", "png");
    }

    public boolean accept(File pathname) {
	if (pathname.isDirectory()) return true;
	String filename = pathname.getName().toLowerCase();
	for (Filter f : filters) {
	    if (filename.endsWith(f.getExtension())) return true;
	}
	return false;
    }

    @Override
    public String getDescription() {
	return "Compatible Image File Formats";
    }

    private Filter filters[];

    class Filter {
	public Filter(String _title, String _extension, String _mime_type) {
	     title = _title;
	     extension = _extension;
	     mime_type = _mime_type;
	}

	public String getTitle() {
	    return title;
	}

	public String getExtension() {
	    return extension;
	}

	public String getMime() {
	    return mime_type;
	}

	private String title, extension, mime_type;
    }
}
