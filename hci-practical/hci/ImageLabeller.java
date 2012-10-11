package hci;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.JOptionPane;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.ObjectOutputStream;

import hci.ImageFileFilter;
import java.util.ArrayList;
import hci.utils.Point;
import hci.SaveObjects;
import hci.ReadObjects;
import hci.XMLOutput;

/**
 * Main class of the program - handles display of the main window
 * @author Michal
 *
 */
public class ImageLabeller extends JFrame {
	
	BufferedImage newImage = null;
	XMLOutput xOut = null;
	/**
	 * some java stuff to get rid of warnings
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * main window panel
	 */
	JPanel appPanel = null;
	JPanel bigassPanel = null;
	/**
	 * toolbox - put all buttons and stuff here!
	 */
	JPanel toolboxPanel = null;
	
	/**
	 * image panel - displays image and editing area
	 */
	ImagePanel imagePanel = null;
	SaveObjects objectSaver = new SaveObjects();
	ReadObjects objectReader = new ReadObjects();
	
	/**
	 * handles New Object button action
	 */
	public void addNewPolygon() {
		imagePanel.addNewPolygon();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		imagePanel.paint(g); //update image panel
	}
	
	
	/**
	 * sets up application window
	 * @param imageFilename image to be loaded for editing
	 * @throws Exception
	 */
	public void setupGUI(String imageFilename) throws Exception {
		//setup main window panel
		bigassPanel = new JPanel();
		bigassPanel.setLayout(new BoxLayout(bigassPanel, BoxLayout.LINE_AXIS));
		appPanel = new JPanel();
		appPanel.setLayout(new BoxLayout(appPanel, BoxLayout.PAGE_AXIS));
		this.setContentPane(bigassPanel);
		
		
        //Create and set up the image panel.
		imagePanel = new ImagePanel(imageFilename);
		this.addWindowListener(new WindowAdapter() {
		  	public void windowClosing(WindowEvent event) {
		  		//here we exit the program (maybe we should ask if the user really wants to do it?)
		  		//maybe we also want to store the polygons somewhere? and read them next time
		  		if (imagePanel.edited){
		  			int response = JOptionPane.showConfirmDialog(null, "You have unsaved changes.  Would you like to save before closing.");
		  			if (response == JOptionPane.YES_OPTION){
		  				objectSaver.buildXML(imagePanel.polygonsList, imagePanel.labelList);
		  			}
		  		}
		  		System.out.println("Bye bye!");
		    	System.exit(0);
		  	}
		});
		imagePanel.setOpaque(true); //content panes must be opaque
		
        appPanel.add(imagePanel);

        //create toolbox panel
        toolboxPanel = new JPanel();
        toolboxPanel.setLayout(new BoxLayout(toolboxPanel, BoxLayout.LINE_AXIS));
        
		JButton openFileButton = new JButton("Open Image");
		openFileButton.setEnabled(true);
		openFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageFileFilter filter = new ImageFileFilter();
				JFileChooser imageChooser = new JFileChooser();
				imageChooser.setFileFilter(filter);
				int returnVal = imageChooser.showOpenDialog(appPanel);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	System.out.println("You chose to open this file: " +
			    		   imageChooser.getSelectedFile().getName());
			    	String imageName = imageChooser.getSelectedFile().getAbsolutePath();
			    	System.out.println(imageName);
			    	try{
			    		newImage = ImageIO.read(new File(imageName));
			    		imagePanel.image = newImage;
			    		imagePanel.polygonsList = new ArrayList<ArrayList<Point>>();
			    	} catch (Exception a) {
			    		a.printStackTrace();
			    	}
			    }
			}
		});
		toolboxPanel.add(openFileButton);
		
		JButton saveButton = new JButton("Save");
		saveButton.setEnabled(true);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				objectSaver.buildXML(imagePanel.polygonsList, imagePanel.labelList);
				JOptionPane.showMessageDialog(null, "Session saved");
				imagePanel.edited = false;
			}
		});
		toolboxPanel.add(saveButton);
		
		JButton loadButton = new JButton("Load");
		loadButton.setEnabled(true);
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				xOut = objectReader.loadFile();
				imagePanel.polygonsList = xOut.getObjects();
				imagePanel.labelList = xOut.getLabels();
				imagePanel.drawLabels();
				JOptionPane.showMessageDialog(null, "Session loaded");
			}
		});
		toolboxPanel.add(loadButton);
		//add toolbox to window
		appPanel.add(toolboxPanel);
		
		bigassPanel.add(appPanel);
		
		bigassPanel.add(imagePanel.labelPanel);
		
		//display all the stuff
		this.pack();
        this.setVisible(true);
	}
	
	/**
	 * Runs the program
	 * @param argv path to an image
	 */
	public static void main(String[] argv) {
		try {
			//create a window and display the image
			ImageLabeller window = new ImageLabeller();
			window.setupGUI(argv[0]);
		} catch (Exception e) {
			System.err.println("Image: " + argv[0]);
			e.printStackTrace();
		}
	}
}
