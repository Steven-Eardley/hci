package hci;

import hci.utils.Point;
import java.util.ArrayList;

public class XMLOutput {
	ArrayList<String> labels = null;
	ArrayList<ArrayList<Point>> objs = null;
	
	public ArrayList<String> getLabels(){
		return labels;
	}
	
	public void setLabels(ArrayList<String> l){
		labels = l;
	}
	
	public ArrayList<ArrayList<Point>> getObjects(){
		return objs;
	}
	
	public void setObjects(ArrayList<ArrayList<Point>> o){
		objs = o;
	}
}
