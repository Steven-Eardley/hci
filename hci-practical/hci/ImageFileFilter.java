package hci;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ImageFileFilter extends FileFilter{
	
	public boolean accept(File f) {
		if(f.isDirectory()) {
            return true;
        }
        return f.getName().endsWith(".jpg") || f.getName().endsWith(".png") || f.getName().endsWith(".gif");
    }
	
	public String getDescription()
    {
        return "Image files jpg/png/gif";
    }
}
