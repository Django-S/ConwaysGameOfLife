import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @version 4/05/2020
 * @author Django Scrivener (463015)
 * 
 * GameHud implements the on-screen controls (fps, load, save etc.)
 * Updates parameters or flags in main loop
 */

public class GameHud {
	//variables
	GameManager gm;							//the game manager, used so the buttons can update parameters
	
	private boolean saveStateFlag;			//flag to tell main thread to save the state
	private boolean loadStateFlag;			//flag to tell main thread to load a new state
	private String loadStateDirectory;		//directory location of state to be loaded
	private String saveStateDirectory;		//directory location of state to be loaded
	
	public GameHud(GameManager gm) {
		this.gm = gm;
		
		//load the on-screen controls
		addLoadButton();
		addSaveButton();
		addFpsSlider();
	}
	
	/*
	 * Adds a button to save states to file
	 */
	public void addSaveButton() {
		JButton buttonSaveState = new JButton("save state");
		gm.add(buttonSaveState);
		
		buttonSaveState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new java.io.File("./states"));
				fc.setDialogTitle("save state");
				int result = fc.showSaveDialog(buttonSaveState);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					saveStateDirectory = fc.getSelectedFile().getPath(); 
					saveStateFlag = true;
				}
				else if (result == JFileChooser.CANCEL_OPTION){
					System.out.println("failed to save state: cancelled by user");
				}
				else if (result == JFileChooser.ERROR_OPTION){
					System.out.println("failed to save state: error");
				}
			}
		});
	}
	
	/*
	 * Adds a button to load states from file
	 */
	public void addLoadButton() {
		JButton buttonLoadState = new JButton("load state");
		gm.add(buttonLoadState);
		
		buttonLoadState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new java.io.File("./states"));
				fc.setDialogTitle("load state");
				int result = fc.showOpenDialog(buttonLoadState);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					loadStateDirectory = fc.getSelectedFile().getPath(); 
					loadStateFlag = true;
				}
				else if (result == JFileChooser.CANCEL_OPTION){
					System.out.println("failed to load state: cancelled by user");
				}
				else if (result == JFileChooser.ERROR_OPTION){
					System.out.println("failed to load state: error");
				}
			}
		});
	}
	
	/*
	 * Adds a slider to control the FPS
	 */
	public void addFpsSlider() {
		JSlider sliderFpsSelector = new JSlider(1, 50, (int)gm.getFPS());
		gm.add(sliderFpsSelector);
		
		sliderFpsSelector.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                gm.setFPS(((JSlider)e.getSource()).getValue());
            }
        });
	}

	//getters and setters
	public boolean isSaveStateFlag() {
		return saveStateFlag;
	}

	public void setSaveStateFlag(boolean saveStateFlag) {
		this.saveStateFlag = saveStateFlag;
	}

	public boolean isLoadStateFlag() {
		return loadStateFlag;
	}

	public void setLoadStateFlag(boolean loadStateFlag) {
		this.loadStateFlag = loadStateFlag;
	}

	public String getLoadStateDirectory() {
		return loadStateDirectory;
	}

	public void setLoadStateDirectory(String loadStateDirectory) {
		this.loadStateDirectory = loadStateDirectory;
	}

	public String getSaveStateDirectory() {
		return saveStateDirectory;
	}

	public void setSaveStateDirectory(String saveStateDirectory) {
		this.saveStateDirectory = saveStateDirectory;
	}
	
}