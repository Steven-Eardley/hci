package hci;

import hci.utils.Point;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main class of the program - handles display of the main window
 * @author Michal
 *
 */
public class ImageLabeller extends JFrame {
	/**
	 * some java stuff to get rid of warnings
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * main window panel
	 */
	JPanel appPanel = null;
	
	/**
	 * toolbox - put all buttons and stuff here!
	 */
	JPanel toolboxPanel = null;
	
	/**
	 * edit - Groups the buttons for editing polygons
	 */
	JPanel editPanel = null;
	
	/**
	 * image panel - displays image and editing area
	 */
	ImagePanel imagePanel = null;


	
	/**
	 * handles New Object button action
	 */
	public void addNewPolygon(String id) {
		if (imagePanel.currentPolygon.size() > 0){
			if (id == null) {
				id = JOptionPane.showInputDialog("Add a label for this polygon");
			}
			if (id != null){
	    	imagePanel.labelsList.add(id);
			imagePanel.addNewPolygon();
			newPolyButton.setEnabled(false);
	    	
	    	System.out.print(imagePanel.labelsList);
			}
		}
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
	final JButton newPolyButton = new JButton("Finish current label");
	JButton editButton;
	public void setupGUI(String imageFilename) throws Exception {
		this.addWindowListener(new WindowAdapter() {
		  	public void windowClosing(WindowEvent event) {
		  		//here we exit the program (maybe we should ask if the user really wants to do it?)
		  		//maybe we also want to store the polygons somewhere? and read them next time
		  		System.out.println("Bye bye!");
		    	System.exit(0);
		  	}
		});

		//setup main window panel
		appPanel = new JPanel();
		this.setLayout(new BoxLayout(appPanel, BoxLayout.X_AXIS));
		this.setContentPane(appPanel);
		
		//Create and set up the image panel.
		imagePanel = new ImagePanel(imageFilename, this);
		imagePanel.setOpaque(true); //content panes must be opaque
		
		appPanel.add(imagePanel);
		
		//create toolbox panel
		toolboxPanel = new JPanel();
		toolboxPanel.setLayout(new BoxLayout(toolboxPanel, BoxLayout.Y_AXIS));
		
		//Add button
		newPolyButton.setMnemonic(KeyEvent.VK_N);
		newPolyButton.setSize(50, 20);
		newPolyButton.setEnabled(false);
		newPolyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			    	addNewPolygon(null);
			}
		});
		newPolyButton.setToolTipText("Click to add new object");
		
		
		JButton openButton = new JButton("Open");
		openButton.setMnemonic(KeyEvent.VK_O);
		openButton.setSize(50, 20);
		openButton.setEnabled(true);
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
				String newFile = fileOpenDialog(true);
					if (newFile != null) {
						imagePanel.setImage(newFile);
						wipe();
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		openButton.setToolTipText("Click to open new image");
		
		
		
		JButton saveImageButton = new JButton("Save image tags");
		saveImageButton.setMnemonic(KeyEvent.VK_S);
		saveImageButton.setSize(50, 20);
		saveImageButton.setEnabled(true);
		saveImageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String saveTagFile = fileOpenDialog(false);
				if (saveTagFile != null) {
					saveTags(saveTagFile);
				}
			}
		});
		saveImageButton.setToolTipText("Click to save image tags");
		
		
		JButton loadImageButton = new JButton("Load image tags");
		loadImageButton.setMnemonic(KeyEvent.VK_L);
		loadImageButton.setSize(50, 20);
		loadImageButton.setEnabled(true);
		loadImageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to load an image tag set? This will delete all existing tags if they have not been saved.", "Confirm load", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION){
					String loadTagFile = fileOpenDialog(true);
					if (loadTagFile != null){
						loadTags(loadTagFile);
					}
				}
			}
		});
		loadImageButton.setToolTipText("Click to load image tags");
		
		/*
		JButton hilight = new JButton("HilightNext");
		hilight.setSize(50, 20);
		hilight.setEnabled(true);
		hilight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				imagePanel.hilight();
			}
		});*/
		
		
		JButton modeToggleButton = new JButton("Toggle mode");
		modeToggleButton.setSize(50, 20);
		modeToggleButton.setMnemonic(KeyEvent.VK_T);
		modeToggleButton.setEnabled(true);
		modeToggleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				imagePanel.drawMode = !imagePanel.drawMode;
				newPolyButton.setEnabled(imagePanel.drawMode && (imagePanel.currentPolygon!= null && imagePanel.currentPolygon.size() > 0));
				editPanel.setVisible(!imagePanel.drawMode);
				imagePanel.paint(imagePanel.getGraphics());
			}
		});

		JButton deleteButton = new JButton("Delete");
		deleteButton.setSize(50, 20);
		deleteButton.setMnemonic(KeyEvent.VK_D);
		deleteButton.setEnabled(true);
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!imagePanel.drawMode && imagePanel.hilighted != null){
					String selectedLabel = imagePanel.labelsList.get(imagePanel.polygonsList.indexOf(imagePanel.hilighted));
					int confirm = JOptionPane.showConfirmDialog(null, String.format("Are you sure you want to delete label %s?", selectedLabel ), "Confirm delete", JOptionPane.YES_NO_OPTION);
					if (confirm == JOptionPane.YES_OPTION){
						imagePanel.deleteHilighted();
						if(imagePanel.editMode){
							editButton();
						}
					}
				}
			}
		});	
		
		JButton renameButton = new JButton("Rename");
		renameButton.setSize(50, 20);
		renameButton.setMnemonic(KeyEvent.VK_R);
		renameButton.setEnabled(true);
		renameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!imagePanel.drawMode){
					imagePanel.renameHilighted();
				}
			}
		});
		
		
		editButton = new JButton("Start editing");
		editButton.setSize(50, 20);
		editButton.setMnemonic(KeyEvent.VK_E);
		editButton.setEnabled(true);
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editButton();
			}
		});
			
		toolboxPanel.add(newPolyButton);
		toolboxPanel.add(openButton);
		toolboxPanel.add(saveImageButton);
		toolboxPanel.add(loadImageButton);
		toolboxPanel.add(modeToggleButton);
		//toolboxPanel.add(hilight);
		Dimension minSize = new Dimension(5, 100);
		Dimension prefSize = new Dimension(5, 100);
		Dimension maxSize = new Dimension(Short.MAX_VALUE, 100);
		toolboxPanel.add(new Box.Filler(minSize, prefSize, maxSize));
		
		editPanel = new JPanel();
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		editPanel.add(deleteButton);
		editPanel.add(renameButton);
		editPanel.add(editButton);
		
		editPanel.setVisible(false);
		
		toolboxPanel.add(editPanel);
			
		
	//add toolbox to window
	appPanel.add(toolboxPanel);
	
	//display all the stuff
	this.pack();
    this.setVisible(true);
	}
	

	protected void editButton() {
		if (!imagePanel.drawMode){
			imagePanel.editHilighted();
			if(imagePanel.editMode){
				this.editButton.setText("Stop editing");
			} else {
				this.editButton.setText("Start editing");
				imagePanel.selected = null;
			}
			imagePanel.paint(imagePanel.getGraphics());
		}
		
	}

	protected void wipe() {
		imagePanel.wipe();
		newPolyButton.setEnabled(false);
	}

	protected void loadTags(String loadTagFile) {
		try {
			wipe();
			FileReader fstream = new FileReader(loadTagFile);
			BufferedReader in = new BufferedReader(fstream);
			String line;
			int x;
			int y;
			String nextPolyName;
			while ((line = in.readLine()) != null){
				if(line.startsWith("p\t")){
					nextPolyName = line.substring(line.indexOf("\t")+1);
					System.out.println("New polygon!");
					addNewPolygon(nextPolyName);
				} else {
					x = Integer.parseInt(line.substring(0, line.indexOf("\t")));
					y = Integer.parseInt(line.substring(line.indexOf("\t")+1));
					System.out.println(line);
					imagePanel.newPoint(x,y);
				}
			}
			in.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void saveTags(String saveTagFile) {
		try {
			FileWriter fstream = new FileWriter(saveTagFile);
			BufferedWriter out = new BufferedWriter(fstream);
			for(int i = 0; i < imagePanel.polygonsList.size(); i++){
			//for(ArrayList<Point> polygon : imagePanel.polygonsList) {
				ArrayList<Point> polygon = imagePanel.polygonsList.get(i);
				for(Point p : polygon){
					out.write(String.format("%s\n", p));
				}
				out.write(String.format("p\t%s\n", imagePanel.labelsList.get(i)));
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

	public String fileOpenDialog(boolean open){
		JFileChooser chooser = new JFileChooser();
		String chosenFile;
		int returnVal;
		if (open){
			returnVal = chooser.showOpenDialog(getParent());
		} else {
			returnVal = chooser.showSaveDialog(getParent());
		}
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	chosenFile = chooser.getSelectedFile().getAbsolutePath();
        	//System.out.println("You chose " + chosenFile);
        	return  chosenFile;
        }
        return null;
		
	}
	
	/**
	 * Runs the program
	 * @param argv path to an image
	 */
	public static void main(String argv[]) {
		try {
			//create a window and display the image
			ImageLabeller window = new ImageLabeller();
			window.setTitle("ImageTagger");
			String open = null;
			if (argv.length > 0){
				open = argv[0];
			} else {
				open = window.fileOpenDialog(true);
				if (open == null){
					return;
				}
			}
			window.setupGUI(open);
		} catch (Exception e) {
			System.err.println("Image: " + argv[0]);
			e.printStackTrace();
		}
	}

	public ImageLabeller() throws HeadlessException {
		super();
	}
}
