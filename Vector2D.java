/**
 * @version 4/05/2020
 * @author Django Scrivener (463015)
 * 
 * Vector2D class used to hold position/direction information
 */

public class Vector2D {
	//variables
	private int x;	//x position on cartesian plane
	private int y;	//y position on cartesian plane
	
	/*
	 * constructor sets initial values
	 */
	public Vector2D (int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/*
	 * checks if vector2D a is equal to this vector
	 */
	public boolean isEqual(Vector2D a) {
		if(a.getX() == x && a.getY() == y) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/*
	 * returns new vector with additional xAdd and yAdd
	 */
	public Vector2D add(int xAdd, int yAdd) {
		return new Vector2D(x + xAdd, y + yAdd);
	}
	
	//getters and setters	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	/*
	 * returns a string representation of the class
	 */
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	
}