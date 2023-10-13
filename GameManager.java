import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

/**
 * @version 4/05/2020
 * @author Django Scrivener (463015)
 * 
 * Game manager class is a JPanel attached to the main frame
 * This class implements the primary game loop
 * This class controls most aspects of the game
 */

public class GameManager extends JPanel implements Runnable{
	//constants
	private static final long serialVersionUID = 1L;
	
	private final int WIDTH = 1000;	//window width
	private final int HEIGHT = 600;	//window height
	
	//variables
	private Thread thread;					//separate game thread
	private boolean isRunning;				//flag to determine whether the game loop is running
	private long startTime;					//system time at beginning of frame. used to control FPS
	private long FPS;				 		//frames per seconds (how quickly the state updates)
	private GameHud hud;					//hud handles the controls for fps, load, save etc.
	private ArrayList<Entity> entities;		//list contains all the Conway entities

	/*
	 * constructor handles all init code for the game manager
	 */
	public GameManager() {
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));	//set the size of the canvas to draw on
		entities = new ArrayList<Entity>();						//initialise the entities,
		
		FPS = 2;												//set the initial FPS
		hud = new GameHud(this);								//create heads up display. Handled by the GameHud class
		isRunning = true;										//set whether the game loop is running
		
		thread = new Thread(this);								//create a new thread for the game loop to run in
		thread.start();											//start the thread (will begin the run() function below)
	}
	
	/*
	 * run() function is called by seperate thread
	 * contains the game loop
	 */
	public void run() {
		//the game loop, runs while the isRunning flag is true
		while (isRunning) {
			//update the start time to calculate the frame period; must go at start of loop
			startTime = System.nanoTime();
			
			//perform loop operations
			repaint();
			resetEntityNeighbours();
			updateNeighbours();
			updateEntities();
			cleanUpEntities();
			
			//update GUI flags
			if (hud.isSaveStateFlag()) {
				saveStateToFile(hud.getSaveStateDirectory());
				hud.setSaveStateFlag(false);
			}
			if (hud.isLoadStateFlag()) {
				loadStateFromFile(hud.getLoadStateDirectory());
				hud.setLoadStateFlag(false);
			}
			
			//limit the FPS; must go at end of loop
			enforceFps();
		}
	}
	
	/*
	 * runs in separate thread outside of game loop
	 * draws to the screen using the Graphics entity
	 */
	public void paint(Graphics g) {
		super.paint(g);
		drawAllEntities(g);
	}
	
	/*
	 * Enforces the FPS limit of the screen by sleeping the thread.
	 */
	public void enforceFps() {
		//variables
		long elapsedTime;
		long waitTime;
		
		if (FPS!=0) {
			long framePeriod = 1000/FPS; 	//milliseconds per frame
			elapsedTime = System.nanoTime() - startTime;
			
			waitTime = framePeriod - elapsedTime / 1000000; 	/*time to wait between frames in milliseconds
																to ensure that the FPS is limited.*/
			
			if (waitTime <= 0) {
				waitTime = 5;
			}
			
			try {
				Thread.sleep(waitTime);	//pause the thread the required amount of time
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Loads state from a file
	 */
	public void loadStateFromFile(String fileDirectory) 
	{
		//variables
	    List<String> state = Collections.emptyList(); //Create a list of strings to store the input from the .state file
	    
	    entities.clear();	//clear the current entities so that the loaded state has a clear slate
	    
	    //load the list from the map file.
	    try { 
	      state = Files.readAllLines(Paths.get(fileDirectory), StandardCharsets.UTF_8); 
	    } catch (IOException e) {
	      e.printStackTrace(); 
	    }
	    
	    //iterate through each element in the list
	    Iterator<String> itr = state.iterator(); 	//to iterate through list
	    String currentItr;						 	//the current row
	    
		for (int i = 0; itr.hasNext(); i++) {
			currentItr = itr.next(); //retrieves the next line from the list
			
			for (int j = 0; j < currentItr.length(); j++) {
				if (currentItr.charAt(j) == '#') {
					//if the character read from the file is '#' this indicates that there is an entity in that position
					addEntity(new Vector2D(j, i), true); //add an alive Conway entity at row j and column i.
				}
			}
		}
		
		System.out.println("successfully loaded state from: " + fileDirectory);	//provide confirmation message
	}
	
	/*
	 * Saves the state of the game to a .txt file
	 */
	public void saveStateToFile(String fileDirectory) {
		//variables
		Rectangle entityBounds = getEntityBounds(); 		//determine the bounds of the save state file
	    ArrayList<String> state = new ArrayList<String>();	//create multidimensional array of characters
	    
	    //fill up the state list with '0' chars, the dimensions of the bounds
	    //'0' indicates a dead entity
	    String emptyRow = new String();
	    for (int i = 0; i<=entityBounds.width; i++) {
	    	emptyRow = emptyRow + "0";
	    }
	    for (int i = 0; i<=entityBounds.height; i++) {
	    	state.add(emptyRow);
	    }
	    
	    //change the character from a '0' to a '#'
	    //if there is an alive entity in the corresponding coordinates
	    for (int i = 0; i<entities.size(); i++){
	    	if (entities.get(i).isAlive()) {
	    		Vector2D entityPos = entities.get(i).getPosition();
		    	int stateColumnIndex = entityPos.getX()-entityBounds.x; //x
		    	int stateRowIndex = entityPos.getY()-entityBounds.y; 	//y
		    	
		    	StringBuilder entityRow = new StringBuilder(state.get(stateRowIndex));
		    	entityRow.setCharAt(stateColumnIndex, '#');
		    	
		    	state.set(stateRowIndex, entityRow.toString());
	    	}
	    }
	    
		//print the 2D character array developed earlier to file
		PrintWriter out = null;
		
		try {
			out = new PrintWriter(fileDirectory);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i<state.size(); i++) {
			out.println(state.get(i));
		}
		out.close();
		
		System.out.println("successfully saved state to: " + fileDirectory);	//provide confirmation message
	}
	
	/*
	 * returns the smallest Rectangle which would cover all entities.
	 */
	public Rectangle getEntityBounds() {
		//variables
		Rectangle bounds = new Rectangle();			//defines the bounds of the entities in the game
		
		//set initial bounds to the firsts element in array. This ensures that the
		//the smallest x and y values will be obtained.
		bounds.width = 0;
		bounds.height = 0;
		
		if (entities.size() == 0) {
			//if the entities array is empty then we want to return
			//a rectangle at position (0,0), with 0 dimensions.
			bounds.x = 0;
			bounds.y = 0;
			
			return bounds;
		}
		
		bounds.x = entities.get(0).getPosition().getX();
		bounds.y = entities.get(0).getPosition().getY();
		
		//iterate through all entities and determine the bounds and location of all entities
		for (int i = 0; i < entities.size(); i++) {
			Vector2D currentPos = entities.get(i).getPosition();
			
			//update the position to be at the position of the most top left entity
			if (currentPos.getX() < bounds.x) {
				bounds.x = currentPos.getX();
			}
			if (currentPos.getY() < bounds.y) {
				bounds.y = currentPos.getY();
			}
		}
		
		for (int i = 0; i < entities.size(); i++) {
			Vector2D currentPos = entities.get(i).getPosition();
			
			//update the bounds to ensure rectangle encompasses all entities
			if (currentPos.getX() - bounds.x > bounds.width) {
				bounds.width = currentPos.getX() - bounds.x;
			}
			if (currentPos.getY() - bounds.y > bounds.height) {
				bounds.height = currentPos.getY() - bounds.y;
			}
		}
		
		return bounds;
	}
	
	/*
	 * cleans up the entities with zero neighbours to save space in entities array
	 */
	public void cleanUpEntities() {
	    for (int i = 0; i < entities.size(); i++) {
	    	if (entities.get(i).getNeighbourNum() == 0) {
	    		deleteEntity(i);
	    		i--;
	    	}
	    }
	}
	
	/*
	 * adds new Conway entity to the grid at the position (x, y)
	 */
	public void addEntity(Vector2D position, boolean isAlive) {
		entities.add(new Entity(position, this));
		entities.get(entities.size()-1).setAlive(isAlive);
	}
	
	/*
	 * deletes the entity at the given index
	 */
	public void deleteEntity(int index) {
		entities.remove(index);
	}
	
	/*
	 * returns the index of an entity at the specified grid position
	 * otherwise it returns -1
	 */
	public int isEntityAtPosition(Vector2D position) {
		for (int i = 0; i < entities.size(); i++) {
				if (entities.get(i).getPosition().isEqual(position)) {
					return i;
				}
		}
		
		//return -1 if no entity exists at that position
		return -1;
	}
	
	/*
	 * calls the draw function for all entities
	 */
	public void drawAllEntities(Graphics g) {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).draw(g);
		}
	}
	
	/*
	 * updates the neighbors for all Conway entities
	 * 
	 * parses through each Conway entity and calls the entitie's updateNeighbours function
	 * each entity stores their own eight neighbours, which is then used to determine
	 * whether entities should be born or die
	 */
	public void updateNeighbours() {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).updateNeighbours();
		}
	}
	
	/*
	 * resets the number of neighbours each entity has to zero
	 */
	public void resetEntityNeighbours() {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).setNeighbourNum(0);
		}
	}
	
	/*
	 * update the conway entities based on the number of neihgbours each cell has
	 */
	public void updateEntities() {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).update();
		}
	}

	
	//getters and setters
	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getFPS() {
		return FPS;
	}

	public void setFPS(long fPS) {
		FPS = fPS;
	}

	public ArrayList<Entity> getEntities() {
		return entities;
	}

	public void setEntities(ArrayList<Entity> entities) {
		this.entities = entities;
	}

	public int getWIDTH() {
		return WIDTH;
	}

	public int getHEIGHT() {
		return HEIGHT;
	}
}