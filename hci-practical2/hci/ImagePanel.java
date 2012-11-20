package hci;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import hci.utils.*;

/**
 * Handles image editing panel
 * @author Michal
 *
 */
public class ImagePanel extends JPanel implements MouseListener, MouseMotionListener {
	/**
	 * some java stuff to get rid of warnings
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * image to be tagged
	 */
	BufferedImage image = null;
	
	/**
	 * list of current polygon's vertices 
	 */
	ArrayList<Point> currentPolygon = null;
	
	/**
	 * list of polygons
	 */
	ArrayList<ArrayList<Point>> polygonsList = null;

	public ArrayList<Point> hilighted = null;
	
	public ArrayList<String> labelsList = null;
	
	public boolean drawMode = true;

	public boolean editMode = false;

	public Point selected;

	private ImageLabeller parent;

	//private Color currentColor;
	
	/**
	 * default constructor, sets up the window properties
	 */
	public ImagePanel(ImageLabeller caller) {
		parent = caller;
		currentPolygon = new ArrayList<Point>();
		polygonsList = new ArrayList<ArrayList<Point>>();
		labelsList = new ArrayList<String>();

		this.setVisible(true);

		Dimension panelSize = new Dimension(800, 600);
		this.setSize(panelSize);
		this.setMinimumSize(panelSize);
		this.setPreferredSize(panelSize);
		this.setMaximumSize(panelSize);
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	/**
	 * extended constructor - loads image to be labelled
	 * @param imageName - path to image
	 * @throws Exception if error loading the image
	 */
	public ImagePanel(String imageName, ImageLabeller caller) throws Exception{
		this(caller);
		this.setImage(imageName);
	}
	
	public void setImage(String imageName) throws Exception{
		this.
		image = ImageIO.read(new File(imageName));
		if (image.getWidth() > 800 || image.getHeight() > 600) {
			int newWidth = image.getWidth() > 800 ? 800 : (image.getWidth() * 600)/image.getHeight();
			int newHeight = image.getHeight() > 600 ? 600 : (image.getHeight() * 800)/image.getWidth();
			System.out.println("SCALING TO " + newWidth + "x" + newHeight );
			Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
			image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			image.getGraphics().drawImage(scaledImage, 0, 0, this);
		}
	}

	/**
	 * Displays the image
	 */
	public void ShowImage() {
		Graphics g = this.getGraphics();
		
		if (image != null) {
			g.drawImage(
					image, 0, 0, null);
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		//display iamge
		ShowImage();
		
		//display all the completed polygons
		for(ArrayList<Point> polygon : polygonsList) {
			drawPolygon(polygon);
			finishPolygon(polygon);
		}
		
		//display current polygon
		drawPolygon(currentPolygon);
		if (hilighted != null){
			hilightPolygon(hilighted );
		}
		//this.updateUI();
	}
	
	/**
	 * displays a polygon without last stroke
	 * @param polygon to be displayed
	 */
	public void drawPolygon(ArrayList<Point> polygon) {
		Graphics2D g = (Graphics2D)this.getGraphics();
		g.setColor(Color.GREEN);
		for(int i = 0; i < polygon.size(); i++) {
			Point currentVertex = polygon.get(i);
			if (i != 0) {
				Point prevVertex = polygon.get(i - 1);
				g.drawLine(prevVertex.getX(), prevVertex.getY(), currentVertex.getX(), currentVertex.getY());
			}
			g.fillOval(currentVertex.getX() - 5, currentVertex.getY() - 5, 10, 10);
		}
	}
	/*
	private void nextColor() {
		if (currentColor== Color.RED) {
				currentColor = Color.GREEN;
		} else if (currentColor== Color.GREEN) {
			currentColor = Color.BLUE;
		} else {
			currentColor = Color.RED;
		}
		// TODO Auto-generated method stub
	}
*/
	/**
	 * displays last stroke of the polygon (arch between the last and first vertices)
	 * @param polygon to be finished
	 */
	public void finishPolygon(ArrayList<Point> polygon) {
		Graphics2D g = (Graphics2D)this.getGraphics();
		g.setColor(Color.GREEN);
		Point lastVertex = polygon.get(polygon.size() - 1);
		g.drawString(labelsList.get(polygonsList.indexOf(polygon)), lastVertex.getX() + 10, lastVertex.getY() + 10);
		//if there are less than 3 vertices than nothing to be completed
		if (polygon.size() >= 3) {
			Point firstVertex = polygon.get(0);
			g.drawLine(firstVertex.getX(), firstVertex.getY(), lastVertex.getX(), lastVertex.getY());
			
		}
	}
	
	public void hilightPolygon(ArrayList<Point> polygon) {
		Graphics2D g = (Graphics2D)this.getGraphics();
		g.setColor(Color.RED);
		for(int i = 0; i < polygon.size(); i++) {
			Point currentVertex = polygon.get(i);
			if (i != 0) {
				Point prevVertex = polygon.get(i - 1);
				g.drawLine(prevVertex.getX(), prevVertex.getY(), currentVertex.getX(), currentVertex.getY());
			}
			g.fillOval(currentVertex.getX() - 5, currentVertex.getY() - 5, 10, 10);
			if (i == polygon.size()-1){
				g.drawString(labelsList.get(polygonsList.indexOf(polygon)), currentVertex.getX() + 10, currentVertex.getY() + 10);
			}
		}
		if (polygon.size() >= 3) {
			Point firstVertex = polygon.get(0);
			Point lastVertex = polygon.get(polygon.size() - 1);
			g.drawLine(firstVertex.getX(), firstVertex.getY(), lastVertex.getX(), lastVertex.getY());
			
		}
	}
	
	
	/**
	 * moves current polygon to the list of polygons and makes pace for a new one
	 */
	public void addNewPolygon() {
		//finish the current polygon if any
		if (currentPolygon != null ) {
			polygonsList.add(currentPolygon);
			finishPolygon(currentPolygon);
		}
		
		currentPolygon = new ArrayList<Point>();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		//check if the cursor's within image area
		if (x > image.getWidth() || y > image.getHeight()) {
			//if not do nothing
			return;
		}

		//if the left button than we will add a vertex to poly
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (drawMode){
				newPoint(x, y);
				parent.newPolyButton.setEnabled(drawMode);
			} else {
				for (ArrayList<Point> poly : polygonsList){
					for (Point p : poly){
						if (near(x, p.getX()) && near(y, p.getY())){
							hilight(poly);
							return;
						}
					}
				}
			}
		} 
	}

	public void newPoint(int x, int y) {
		Graphics2D g = (Graphics2D)this.getGraphics();
		g.setColor(Color.GREEN);
		if (currentPolygon.size() != 0) {
			Point lastVertex = currentPolygon.get(currentPolygon.size() - 1);
			g.drawLine(lastVertex.getX(), lastVertex.getY(), x, y);
		}
		g.fillOval(x-5,y-5,10,10);
		
		currentPolygon.add(new Point(x,y));
		System.out.println(new Point(x,y));
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}
	long lastDrawn = 0;
	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (x > image.getWidth() || y > image.getHeight() || x < 0 || y < 0) {
			//if not do nothing
			return;
		}
		if(selected != null && editMode && !drawMode){
			selected.setX(x);
			selected.setY(y);
			if (System.currentTimeMillis() - lastDrawn > 16){
				lastDrawn = System.currentTimeMillis();
				this.paint(this.getGraphics());
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (editMode){
			int x = e.getX();
			int y = e.getY();
			
			//check if the cursor's within image area
			if (x > image.getWidth() || y > image.getHeight()) {
				//if not do nothing
				return;
			}

			//if the left button than we will add a vertex to poly
			if (e.getButton() == MouseEvent.BUTTON1 && hilighted != null) {
				for (Point p : hilighted){
					if (near(x, p.getX()) && near(y, p.getY())){
						selected = p;
					}
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		selected = null;
		this.paint(this.getGraphics());
	}

	private boolean near(int a, int b) {
		int d = 5;
		return ((a > (b -d)) && (a <= (b + d)));
	}

	public void hilight (ArrayList<Point> polygon){
		hilighted = polygonsList.get(polygonsList.indexOf(polygon));
		this.paint(this.getGraphics());
	}
	
	public void hilight() {
		if (polygonsList.isEmpty()){
			return;
		}
		if (hilighted == null) { 
			hilighted = polygonsList.get(0);
		} else {
			hilighted = polygonsList.get((polygonsList.indexOf(hilighted) + 1) % polygonsList.size());
		}
		this.repaint();
		
	}

	public void deleteHilighted() {
		if (hilighted != null){
			labelsList.remove(polygonsList.indexOf(hilighted));
			polygonsList.remove(hilighted);
			hilighted = null;
			this.paint(this.getGraphics());
		}
	}

	public void renameHilighted() {
		if (hilighted != null){
			String id = JOptionPane.showInputDialog("Enter new label for this polygon", labelsList.get(polygonsList.indexOf(hilighted)));
			if (id != null){
				labelsList.remove(polygonsList.indexOf(hilighted));
				labelsList.add(polygonsList.indexOf(hilighted), id);
				this.paint(this.getGraphics());
			}
		}
	}

	public void editHilighted() {
		if (hilighted != null){
			editMode  = !editMode;
		} else {
			editMode = false;
		}
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("I like to move it move it");
		
	}

	public void wipe() {
		currentPolygon = new ArrayList<Point>();
		polygonsList = new ArrayList<ArrayList<Point>>();
		labelsList = new ArrayList<String>();
		hilighted = null;
		
	}
	
}
