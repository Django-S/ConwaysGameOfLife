import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * @version 4/05/2020
 * @author Django Scrivener (463015)
 * 
 * Main function where program starts.
 * Initialises swing frame in seperate thread
 */

public class Life {

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Frame frame = new Frame("Life");						//frame inside window
				frame.setSize(1000, 600);								//size of frame is 1000 x 600 pixels
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//close when pressing the red cross on window
				frame.setVisible(true);									//can see the frame
			}
		});
	}
}