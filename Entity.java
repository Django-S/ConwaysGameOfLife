import java.awt.Color;
import java.awt.Graphics;

/**
 * @version 4/05/2020
 * @author Django Scrivener (463015)
 * 
 * Entity Class
 * Holds information and functionionality for a conway entity
 */

public class Entity {
	//constants
	final int GRID_SIZE = 5; //width and height of the grid. (Also width and height of entity)
	
	//variables
	private Vector2D position;		//position of the entity on the grid
	private Vector2D startPosition;	//start position of the entity in the screen
	private boolean isAlive;		//boolean to state whether this entity is alive or not
	private int neighbourNum;		//number of neighbours this entity has
	
	public GameManager gm;			//provides access to the game manager
	
	/*
	 * Constructor handles init code.
	 * Sets the gridX and gridY variables and the start position
	 */
	public Entity(Vector2D position, GameManager gm) {
		
		this.isAlive = false;
		this.neighbourNum = 0;
		
		this.gm = gm;
		
		this.position = position;
		this.startPosition = new Vector2D(gm.getWIDTH()/2, gm.getHEIGHT()/2);
	}
	
	/*
	 * Draws the entity onto the screen
	 */
	public void draw(Graphics g) {
		if (!isAlive) {
			//if the entity is not alive - do not draw it to the screen
			
			//uncomment following three lines to display dead entities
			//g.setColor(Color.lightGray);
			//g.fillRect(	startPosition.getX()+GRID_SIZE*position.getX(),
			//				startPosition.getY()+GRID_SIZE*position.getY(), GRID_SIZE, GRID_SIZE);
			
			return;
		}
		
		g.setColor(Color.darkGray);
		g.fillRect(	startPosition.getX()+GRID_SIZE*position.getX(),
					startPosition.getY()+GRID_SIZE*position.getY(), GRID_SIZE, GRID_SIZE);
	}
	
	/*
	 * updates the entities array in the game manager class
	 * implement the rules of conway's game of life
	 */
	public void update() {
		//constants
		final int birthNum = 3;		//number of neighbours required for a birth to occur
		final int isolationNum = 1;	//number of neighbours of which less than will result in death due to isolation
		final int overPopNum = 4;	//number of neighours of which more than will result in death due to over population
		
		//implement conway's game of life rules
		if (neighbourNum >= overPopNum || neighbourNum <= isolationNum) {
			isAlive = false;
		}
		if (neighbourNum == birthNum) {
			isAlive = true;
		}
	}
	
	/*
	 * Updates the neighbour num of all entities surrounding it.
	 * This means that each entity does not actually update their own neighbourNum
	 * but instead all neighbours around each entity will increment their neighbourNum.
	 * So if all alive entities perform this function by the end each will have their neighbourNum updated
	 */
	public void updateNeighbours() {
		//constants
		final int xDif = 1;	//checked radius around entity in x-direction
		final int yDif = 1;	//checked radius around entity in y-direction
		
		//do not want to update if entity is not alive
		if (!isAlive) {
			return;
		}
		
		//otherwise update the entities neighbours
		//cycle through the nine neighbors
		for (int j = -xDif; j <= xDif; j++) {
			for (int k = -yDif; k <= yDif; k++) {
				
				//(don't want to update our own neighbour num)
				if (!(k==0 && j==0)) { 
					Vector2D neighbourPosition = this.position.add(j, k);			//the position of the entity we either want to add or increment neighbourNum to
					int neighbourIndex = gm.isEntityAtPosition(neighbourPosition); 	//get the index of the entity at that grid position (return -1 if no entity)
					
					if (neighbourIndex != -1) {
						//if the entity exists then increment their neighbour num
						gm.getEntities().get(neighbourIndex).incrementNeighbourNum();
					}
					else {
						//if the entity doesn't exist add a dead entity at that position and increment their neighbourNum
						gm.addEntity(neighbourPosition, false); //add dead entity
						gm.getEntities().get(gm.getEntities().size()-1).incrementNeighbourNum();
					}
				}
			}
		}
	}
	
	/*
	 * increments the neighbourNum by one
	 */
	public void incrementNeighbourNum() {
		neighbourNum++;
	}
	
	/*
	 * resets the neighbourNum for next iteration
	 */
	public void resetNeighbourNum() {
		neighbourNum = 0;
	}
	
	/*
	 * Returns a string representation of the class
	 */
	public String toString() {
		return ("Entity at position: " + position);
	}
	
	//getters and setters
	public Vector2D getPosition() {
		return position;
	}

	public void setPosition(Vector2D position) {
		this.position = position;
	}

	public Vector2D getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(Vector2D startPosition) {
		this.startPosition = startPosition;
	}

	public int getGRID_SIZE() {
		return GRID_SIZE;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public int getNeighbourNum() {
		return neighbourNum;
	}

	public void setNeighbourNum(int neighbourNum) {
		this.neighbourNum = neighbourNum;
	}
}
