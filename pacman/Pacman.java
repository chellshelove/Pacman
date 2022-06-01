package pacman;

// the imports that is used in this file
import javax.swing.JFrame;

public class Pacman extends JFrame{ // the class that is used in this file with JFrame

	public Pacman() { 
		add(new Model()); // add the model to the frame
	}
	
	
	public static void main(String[] args) { 
		Pacman pac = new Pacman(); // create a new pacman
		pac.setVisible(true); // set the frame to visible
		pac.setTitle("Pacman"); // set the title of the frame
		pac.setSize(360, 420); // set the size of the frame
		pac.setDefaultCloseOperation(EXIT_ON_CLOSE); // set the default close operation of the frame
		pac.setLocationRelativeTo(null); // window position on the center of the screen
		
	}

}