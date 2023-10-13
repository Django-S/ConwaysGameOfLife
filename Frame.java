import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;

/**
 * @version 4/05/2020
 * @author Django Scrivener (463015)
 * 
 * Custom frame. This is where functionality is added to the window
 */

public class Frame extends JFrame{
	//constants
	private static final long serialVersionUID = 1L;
	
	//variables
	private GameManager gm;					//controls the functionality of the game 
	
	/*
	 * Constructor handles the init code for Frame class
	 * Sets the title and adds the game manager as a JPanel
	 */
	public Frame(String title) {
		super(title);						//sets the title of the window
		
		//create swing component
		gm = new GameManager();	//create the game manager. Controls functionality of game of Life
		
		//Add swing components to content pane
		Container c = getContentPane();		//create the content pane to add swing components to
		c.add(gm, BorderLayout.WEST);		//add the game manager to the frame
	}
}