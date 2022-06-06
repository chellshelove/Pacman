package pacman;

// below is the list of all the packages that are used in this file
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Model extends JPanel implements ActionListener { // JPanel is a class that is used to draw on the screen and create a window + action listener is a class that is used to listen for events so that the game reacts when pressing the buttons

	private Dimension d; //dimension describes the height and width of the window/playing field
    private final Font smallFont = new Font("PacFont Good", Font.BOLD, 14); // font is used to dispay the text in the game with your chosen font and size
    private final Font smallFont2 = new Font("Arial", Font.BOLD, 14); // font is used to dispay the text in the game with your chosen font and size
    private boolean inGame = false; // inGame checks if the game is running or not 
    private boolean dying = false; // dying checks if pacman is alive or not

    private final int BLOCK_SIZE = 24; // BLOCK_SIZE descirbes the size of the blocks in the game
    private final int N_BLOCKS = 15; // N_BLOCKS indicates the number of blocks in the game
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE; // SCREEN_SIZE is the size of the number of blocks and block size
    private final int MAX_GHOSTS = 12; // maximum number of the ghosts in the game
    private final int PACMAN_SPEED = 6; // the speed of the pacman

    private int N_GHOSTS = 5; // the number of ghosts in the begining of the game
    private int lives, score; // simple integer countdowns for lives and score. it is the number of lives and score of the player
    private int[] dx, dy; // for the position of the ghosts
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed; // used to detemine the number and position of the ghosts

    private Image heart, ghost; // images for the heart lives and the ghosts
    private Image up, down, left, right; // images for the pacman animation up, down, left and right

    private int pacman_x, pacman_y, pacmand_x, pacmand_y; // pacman_x and pacman_y are the coordinates of the pacman sprite, the last two variable are the coordinates of the pacman when it is moving
    private int req_dx, req_dy; // req_dx and req_dy are the determant in the TAdapter in the class, these variables are controlled by the arrow keys

    private final short levelData[] = { // the level data
    	19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
        17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
        0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
        19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
        17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
        17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
        17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,         // 225 is the number of the blocks in the level
        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
        17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
        21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 16, 16, 16, 20,
        17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
    };

    
    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8}; //an array of the valid speeds for the speed of the game 
    private final int maxSpeed = 6; //the maximum speed

    private int currentSpeed = 3; // the current speed of the game
    private short[] screenData; // the array screen data will take the data from the level data and will be used to draw the game
    private Timer timer; // the timer allows anitmations to happen

    public Model() { // the constructor should call different funtions 

        loadImages(); // loads the images
        initVariables(); // all variables are initialized
        addKeyListener(new TAdapter()); // adds the key listener to control the game
        setFocusable(true); // sets the focus to the game
        initGame(); // starts the game
    }
    
    
    public Font getSmallFont2() { // returns the font
        return smallFont2;
    }

    private void loadImages() { // loads the images
    	down = new ImageIcon("/Users/chellshelove/Documents/GitHub/Pacman/images/down.gif").getImage(); // loads the image for the pacman when it is moving down
        up = new ImageIcon("/Users/chellshelove/Documents/GitHub/Pacman/images/up.gif").getImage(); // loads the image for the pacman when it is moving up
        left = new ImageIcon("/Users/chellshelove/Documents/GitHub/Pacman/images/left.gif").getImage(); // loads the image for the pacman when it is moving left
        right = new ImageIcon("/Users/chellshelove/Documents/GitHub/Pacman/images/right.gif").getImage(); // loads the image for the pacman when it is moving right
        ghost = new ImageIcon("/Users/chellshelove/Documents/GitHub/Pacman/images/ghost.gif").getImage(); // loads the image for the ghost
        heart = new ImageIcon("/Users/chellshelove/Documents/GitHub/Pacman/images/heart.gif").getImage(); // loads the image for the heart

    }
      
       private void initVariables() { // initializes all the variables

        screenData = new short[N_BLOCKS * N_BLOCKS]; // the screen data is the array of the level data
        d = new Dimension(400, 400); // the dimension of the game
        ghost_x = new int[MAX_GHOSTS]; // the ghost x is the array of the ghost x
        ghost_dx = new int[MAX_GHOSTS]; // the ghost dx is the array of the ghost dx 
        ghost_y = new int[MAX_GHOSTS]; // the ghost y is the array of the ghost y
        ghost_dy = new int[MAX_GHOSTS]; // the ghost dy is the array of the ghost dy
        ghostSpeed = new int[MAX_GHOSTS]; // the ghost speed is the array of the ghost speed
        dx = new int[4]; // the dx is the array of the dx
        dy = new int[4]; // the dy is the array of the dy
        
        timer = new Timer( 50, this); //the timer takes care of the animation because it determines how often the image are redrawn, the 40 is in milliseconds ergo it means the game is redrawn every 40 milliseconds, this shows the speed and movement of the ghost and pacman
        timer.start(); // starts the timer
    }

    private void playGame(Graphics2D g2d) { // the function playGame is just a collection of other functions that are called to display the graphics

        if (dying) { // when pacman dies the death function is called 
            death(); 

        } else {
            movePacman(); // the movePacman function is called
            drawPacman(g2d); // the drawPacman function is called
            moveGhosts(g2d); // the moveGhosts function is called
            checkMaze(); // the checkMaze function is called
        }
    }

    private void showIntroScreen(Graphics2D g2d) { // this function is used to draw the intro screen
        g2d.setFont(smallFont); // sets the font
    	String start = "Press SPACE to start"; // define the text that is displayed on the intro screen
        g2d.setColor(Color.yellow); // the color of the text is yellow
        g2d.drawString(start, (int) ((SCREEN_SIZE)/4.5), 175); // the text is displayed in the middle of the screen
    }

    private void drawScore(Graphics2D g) { // this function is used to draw the score
        g.setFont(smallFont2); // sets the font
        g.setColor(new Color(5, 181, 79)); // the color of the text is green
        String s = "Score: " + score; // define the text for the score
        g.drawString(s, SCREEN_SIZE / 2 + 100, SCREEN_SIZE + 20); // the text is displayed in the bottom right of the screen

        for (int i = 0; i < lives; i++) { // the loop is used to check how many heart lives the pacman has left and displays them
            g.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 5, this); // the heart is displayed in the bottom left of the screen
        }
    }

    private void checkMaze() { // the function checkMaze checks if there are any dot points left for the pacman to eat

        int i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {

            if ((screenData[i]) != 0) {
                finished = false;
            }

            i++;
        }

        if (finished) { // if there are no dot points left for the pacman to eat the game is won and the game restarts

            score += 50; // the score is increased by 50

            if (N_GHOSTS < MAX_GHOSTS) { // the ghost is increased by 1
                N_GHOSTS++; 
            }

            if (currentSpeed < maxSpeed) { // the speed is increased by 1
                currentSpeed++;
            }

            initLevel(); // the level is initialized
        }
    }
    

    private void death() { // if the pacman dies then one heart lives is deducted and the game continues until the pacman has no more heart lives

    	lives--;

        if (lives == 0) {
            inGame = false;
        }

        continueLevel(); // the pacman and ghosts are put back to their starting positions
    }

    private void moveGhosts(Graphics2D g2d) { // this function allows the ghosts to move automatically

        int pos; // the position of the ghost
        int count; // the count of the ghost

        for (int i = 0; i < N_GHOSTS; i++) { // set the position of all 6 ghosts again using block size and the number of ghosts 
            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) { // the ghost will move on one square then they will decide if they want to change directions 
                pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE); // the position of the ghost is the ghost_x divided by the block size plus the number of blocks times the ghost_y divided by the block size

                count = 0; // the count is set to 0

                if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) { // use the border information (1, 2, 4, 8) to determine how the ghosts can move
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) { // use the border information (1, 2, 4, 8) to determine how the ghosts can move
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) { // use the border information (1, 2, 4, 8) to determine how the ghosts can move
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) { // use the border information (1, 2, 4, 8) to determine how the ghosts can move
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) { // if the ghost is on a corner then the ghost will not move
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else { 
                        ghost_dx[i] = -ghost_dx[i]; // this detemines where the ghost is located, in which position or square
                        ghost_dy[i] = -ghost_dy[i]; // this detemines where the ghost is located, in which position or square
                    }

                } else { 

                    count = (int) (Math.random() * count); // there are 225 positions, the ghost cannot move over the borders, if their is no obstacles on the left and the ghost is already not moving to the right then the ghost will move to the left

                    if (count > 3) {
                        count = 3;
                    }

                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }

            }

            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1); // with the drawGhost function it loads the image of the ghost that is needed to be drawn

            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12) // if the pacman touches one of the ghosts then the pacman looses a heart life
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                    && inGame) {

                dying = true; 
            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y) { // this function loads the image of the ghost that is needed to be drawn
    	g2d.drawImage(ghost, x, y, this); // the ghost is drawn
        }

    private void movePacman() { // the movePacman function is called

        int pos; // the pos is the position of the pacman
        short ch; // the ch is the character of the dots that the pacman eats

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) { // the position of the pacman is determined
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE); 
            ch = screenData[pos]; 

            if ((ch & 16) != 0) { // if the character is equal to 16 then the pacman is able to eat the dot
                screenData[pos] = (short) (ch & 15); // if the pacman is in the on the field of 16, the score will increase
                score++; // the score is increased by one
            }

            if (req_dx != 0 || req_dy != 0) { // with req_dx and req_dy the pacman is controlled by the arrow keys
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0) // checks if the pacman is in one of the borders then the pacman cannot move in the corresponding direction
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0) // checks if the pacman is in one of the borders then the pacman cannot move in the corresponding direction
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0) // checks if the pacman is in one of the borders then the pacman cannot move in the corresponding direction
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) { // checks if the pacman is in one of the borders then the pacman cannot move in the corresponding direction
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                }
            }

            // Check for standstill
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                pacmand_x = 0; // pacmand_x is set to 0
                pacmand_y = 0; // pacmand_y is set to 0
            }
        } 
        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x; // the speed is adjusted accordingly
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y; // the speed is adjusted accordingly
    }

    private void drawPacman(Graphics2D g2d) { // the drawPacman function is called

        if (req_dx == -1) { // it checks which arrow key is pressed and draws the pacman accordingly
        	g2d.drawImage(left, pacman_x + 1, pacman_y + 1, this); // the corresponding pacman image is loaded for the different directions (left)
        } else if (req_dx == 1) { // it checks which arrow key is pressed and draws the pacman accordingly
        	g2d.drawImage(right, pacman_x + 1, pacman_y + 1, this); // the corresponding pacman image is loaded for the different directions (right)
        } else if (req_dy == -1) { // it checks which arrow key is pressed and draws the pacman accordingly
        	g2d.drawImage(up, pacman_x + 1, pacman_y + 1, this); // the corresponding pacman image is loaded for the different directions (up)
        } else { // it checks which arrow key is pressed and draws the pacman accordingly
        	g2d.drawImage(down, pacman_x + 1, pacman_y + 1, this); // the corresponding pacman image is loaded for the different directions (down)
        }
    }

    private void drawMaze(Graphics2D g2d) { // in this function the game is drawn just as it is defined above with 225 numbers

        short i = 0; // the i is the position of the pacman
        int x, y; // the x and y are the coordinates of the dots that are needed to be drawn

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) { // it is iterared in two for loops with screen_size and block_size through the whole array, this will draw the x and y axis of the array
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(new Color(0,72,251)); // the color of the border and blocks are set to blue
                g2d.setStroke(new BasicStroke(5)); // the border is set to 5 pixels thick
                
                if ((levelData[i] == 0)) { // if one field of the array is 0 it is colored blue
                	g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE); // the blue block is filled and drawn
                 }

                if ((screenData[i] & 1) != 0) { // if it is 1 the left border is drawn
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) { // if it is 2 the top border is drawn
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) { // if it is 4 the right border is drawn
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) { // if it is 8 the bottom border is drawn
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) { // if it is 16 the white pellet dot is drawn
                    g2d.setColor(new Color(255,255,255)); // the color of the pellet dot is set to white
                    g2d.fillOval(x + 10, y + 10, 6, 6); // the pellet dot is filled and drawn
               }

                i++; 
            }
        }
    }

    private void initGame() { // initialize game variables 
    	lives = 3; // starting values for the lives
        score = 0; // starting values for the score
        initLevel(); // initialize the level
        N_GHOSTS = 5; // define the number of the ghosts 
        currentSpeed = 2; // define the speed of the ghosts
    }

    private void initLevel() { // initialize the level

        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) { // to initialize the level, create  for loop and copy the whole play field form the array levelData to the new array screenData
            screenData[i] = levelData[i];
        }

        continueLevel();
    }

    private void continueLevel() { // the function continueLevel() defines the position of the ghosts, it also creates random speed for the ghosts

    	int dx = 1;
        int random;

        for (int i = 0; i < N_GHOSTS; i++) {

            ghost_y[i] = 4 * BLOCK_SIZE; //start position of the ghosts
            ghost_x[i] = 4 * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random]; // the speed may only have a value which is in the array validSpeeds
        }

        pacman_x = 7 * BLOCK_SIZE;  //start position of the pacman
        pacman_y = 11 * BLOCK_SIZE;
        pacmand_x = 0;	//reset direction move
        pacmand_y = 0;
        req_dx = 0;		// reset direction controls using the arrow keys
        req_dy = 0;
        dying = false;
    }

 
    public void paintComponent(Graphics g) { // to have the graphics component complete, the funtion paintComponent() is used
        super.paintComponent(g); // to call the paintComponent() method of the parent superclass

        Graphics2D g2d = (Graphics2D) g; // to create a graphics context for the graphics component

        g2d.setColor(Color.black); // to set the color of background
        g2d.fillRect(0, 0, d.width, d.height); // to fill the background with the color black

        drawMaze(g2d); // to draw the maze for the game information
        drawScore(g2d); // to draw the score for the game information

        if (inGame) { // to draw the pacman and the ghosts in the game
            playGame(g2d); // to play the game
        } else {
            showIntroScreen(g2d); // to draw the intro screen for the game information
        }

        Toolkit.getDefaultToolkit().sync(); // to sync the graphics component
        g2d.dispose(); // to dispose the graphics context
    }


    //controls
    class TAdapter extends KeyAdapter { // function for the arrow keys 

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (inGame) { //if game is in progress then the pacman will be controlled by the arrow keys 
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1; // the variables req_dx and req_dy are used to control the x and y position
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1; // the variables req_dx and req_dy are used to control the x and y position
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0; // the variables req_dx and req_dy are used to control the x and y position
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0; // the variables req_dx and req_dy are used to control the x and y position
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) { // if the escape key is pressed while the timer is running the game should end
                    inGame = false; // the game is no longer in progress
                } 
            } else {
                if (key == KeyEvent.VK_SPACE) { // if the space key is pressed the game should start
                    inGame = true; // the game is now in progress
                    initGame(); // the function initGame() is called
                }
            }
        }
}

	
    @Override // to override the keyReleased() method of the KeyListener interface
    public void actionPerformed(ActionEvent e) { // to define the actionPerformed() method of the ActionListener interface
        repaint(); // to repaint the graphics component
    }
		
	}