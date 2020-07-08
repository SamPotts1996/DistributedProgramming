
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.io.*;
import javax.swing.JLabel;
import sun.audio.*;
import java.lang.Thread.*;





public class Application extends JFrame 
{	
   //Constructor
   public Application()
   {
      //Setup the JFrame
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setSize(900,700);        
      
      //Setup a Contaner
      Container Con = new Container();
      this.add(Con);         
   }  
  
    public static void main( String args[] )
    {
	//Create the JFrame	
	Application jFrame = new Application();
	jFrame.setVisible(true);
        
        //Add the JPanel to the frame 
	Palette jPanel = new Palette();
	jFrame.add(jPanel);                   
    } 
    
    
}
    
//Create the class used to paint the animations 
class Palette extends JPanel
               implements ActionListener, KeyListener {

// Create the two image paths used to load the images 
private final String raceTrackPath = "RaceTrack.png";
private final String startBtnPath = "startBtn.png";

// init the raceTrack image 
private ImageIcon raceTrack; 

// Create the two image icon arrays 
private final ImageIcon[] carOne = new ImageIcon[15];
private final ImageIcon[] carTwo = new ImageIcon[15];

// Used to idenify which image should be loaded within the imageIcon arrays 
private int carOneIndex = 0, carTwoIndex = 0;

// Create the textfield used to log the players movement 
private final JTextField keyListenerBuffer = new JTextField(20);
private int carOneSpeed = 0, carTwoSpeed = 0;

// Init the cars starting points 
private int carOneX = 50, carOneY = 100;
private int carTwoX = 100, carTwoY = 150;

// Create the two vaiables used to idenify the cars individual rotation axis
private double carOneRotationAxis = 0;
private double carTwoRotationAxis = 0;

// Set the raceTrack boundaries
private Rectangle outerBorder = new Rectangle(50, 100, 750, 500);
private Rectangle midBorder = new Rectangle(100, 150, 650, 400);
private Rectangle innerBorder = new Rectangle(150, 200, 550, 300);

//Set the timer to refresh (1 seconds)
private Timer refreshPalette = new Timer(1000, this);

// Set the start button 
private JButton btnStart = new JButton(new ImageIcon(getClass().getResource(startBtnPath)));

// Set the JLabels used to display the car speed 
private JLabel carOneSpeedDisplay = new JLabel("");
private JLabel carTwoSpeedDisplay = new JLabel("");

//Constructor
public Palette(){	
    
        // Set the size of the panel and the objects to the palette
        this.setSize(900,700);
        this.add(keyListenerBuffer);
        this.add(btnStart);
        
        // Store all images into two seperate imageIcon arrays. 
        populateImageIcon();
           
        // Setup the JButton
        btnStart.setVisible(true);
        btnStart.setSize(60,40);
      
        // Setup the textfield used to read the users movement choices 
        keyListenerBuffer.addKeyListener(this);           
        keyListenerBuffer.setVisible(true);
             
        // Set the timer to reset on each interval period 
        refreshPalette.setRepeats(true);
        refreshPalette.start(); 
                            
        /// Anonymous inner class used for the start button, Example taken from Stackoverflow : https://stackoverflow.com/questions/284899/how-do-you-add-an-actionlistener-onto-a-jbutton-in-java
        btnStart.addActionListener(new ActionListener() { 
    
        public void actionPerformed(ActionEvent e) { 
        startButton();
        }});
}	

private void startButton()
{    
    // Focus the JTextField 
    keyListenerBuffer.requestFocus(true);    
    
    // Display the car speed labels 
    this.add(carOneSpeedDisplay);
    this.add(carTwoSpeedDisplay);
    
// Hide the button 
    btnStart.setVisible(false);   
}

@Override
public void actionPerformed(ActionEvent e) 
    {
      
     //Calculate the mid points of the image. (based on the 50x50 px image of car)              
     Point carOne = new Point(carOneX + 50/2, carOneY + 50/2); 
     Point carTwo = new Point(carTwoX + 50/2,carTwoY + 50/2);
        
     // Check if the cars speed is more than 0, calculate the positions 
    if(carOneSpeed > 0)
    {
        carOneX = getPositionX( carOneX, carOneSpeed, getRotationAxis(carOneIndex,carOneRotationAxis));
        carOneY = getPositionY( carOneY, carOneSpeed, getRotationAxis(carOneIndex,carOneRotationAxis));
    }
    
    if(carTwoSpeed > 0)
    {
        carTwoX = getPositionX( carTwoX, carTwoSpeed, getRotationAxis(carTwoIndex,carTwoRotationAxis) );
        carTwoY = getPositionY( carTwoY, carTwoSpeed, getRotationAxis(carTwoIndex,carTwoRotationAxis) );
    }
          
    // Check if carOne has left the track
    if(!outerBorder.contains(carOne) || midBorder.contains(carOne))
    {      
        // Slow the car down
        carOneSpeed = 5;
    }
    // Check if carTwo has left the track
    if (!midBorder.contains(carTwo) || innerBorder.contains(carTwo))
    {
        // Slow the car down 
        carTwoSpeed = 5;   
    }
    // Update the JLabels
    carOneSpeedDisplay.setText("Car One Speed : " + Integer.toString(carOneSpeed));
    carTwoSpeedDisplay.setText("Car Two Speed : " + Integer.toString(carTwoSpeed));
    
    
    // Create two rectangles that represent the cars collision box (50 px x 50px)
    Rectangle carOneRect = new Rectangle(carOneX, carOneY, 50, 50);
    Rectangle carTwoRect = new Rectangle(carTwoX, carTwoY, 50, 50);
  
    // Check if the cars have collided 
    if(carOneRect.contains(carTwo) || carTwoRect.contains(carOne)){
        
        /// Example inspired by the Alvin Alexander website : (https://alvinalexander.com/java/java-audio-example-java-au-play-sound)
        // Play the car crash sound
        try{
        
            // open the sound file as a Java input stream            
            InputStream in = new FileInputStream("src/carCrashEffect.wav");

            // create an audiostream from the inputstream
            AudioStream audioStream = new AudioStream(in);

            // play the audio clip with the audioplayer class
            AudioPlayer.player.start(audioStream);
        
    } catch(Exception p){p.printStackTrace();}
      
        // Prompt the user with a message
      JOptionPane.showMessageDialog(null, "The car has crashed!");
      
      // Close the application 
      System.exit(0);
    }   
     //refresh the panel
       this.repaint();
    }

private void populateImageIcon()
{		       
    // Populate the ImageIcon arrays
    for (int i = 0; i <= 14;)
    {	    	
        // Locate and store the image path(s) for each car. 
        String carOnePath = "carOne/Car_1_r" + i + ".png",
	    carTwoPath = "carTwo/Car_2_r" + i + ".png";
	    	  	    	  
	// Store the images within each imageIcon array
        carOne[i] = new ImageIcon(getClass().getResource(carOnePath));
	carTwo[i] = new ImageIcon(getClass().getResource(carTwoPath));
	
        i++;               
    }		  

    //Load the raceTrack
    raceTrack = new ImageIcon(getClass().getResource(raceTrackPath));    
} 

@Override
public void paintComponent(Graphics g) {
      	
        super.paintComponent(g);      
                   
        //Paint the image of the raceTrack       
        raceTrack.paintIcon(this, g, 50, 100);
        
        //paint the cars 
        carOne[carOneIndex].paintIcon(this, g, carOneX, carOneY);
	carTwo[carTwoIndex].paintIcon(this, g, carTwoX, carTwoY);
}

public void performKeyAction(KeyEvent e)
{
    // Perform the appropriate action for each key pressed. (non case sensitive) 
    switch(e.getKeyChar()){
        
        case 'w' : carOneSpeed = increaseCarSpeed(carOneSpeed); 
            break;
        case 'W' : carOneSpeed = increaseCarSpeed(carOneSpeed);
            break;
        case 's' : carOneSpeed = decreaseCarSpeed(carOneSpeed);
            break;
        case 'S' : carOneSpeed = decreaseCarSpeed(carOneSpeed);
            break;
        case 'u' : carTwoSpeed = increaseCarSpeed(carTwoSpeed); 
            break;
        case 'U' : carTwoSpeed = increaseCarSpeed(carTwoSpeed);
            break;
        case 'j' : carTwoSpeed = decreaseCarSpeed(carTwoSpeed);
            break;
        case 'J' : carTwoSpeed = decreaseCarSpeed(carTwoSpeed);
            break;
        case 'a' : carOneIndex = rotateCarAntiClockwise(carOneIndex);
            break;
        case 'A' : carOneIndex = rotateCarAntiClockwise(carOneIndex); 
            break;
        case 'd' : carOneIndex = rotateCarClockwise(carOneIndex);
            break;
        case 'D' : carOneIndex = rotateCarClockwise(carOneIndex);
            break;
        case 'h' : carTwoIndex = rotateCarAntiClockwise(carTwoIndex);
            break;
        case 'H' : carTwoIndex = rotateCarAntiClockwise(carTwoIndex);
            break;
        case 'k' : carTwoIndex = rotateCarClockwise(carTwoIndex);
            break;
        case 'K' : carTwoIndex = rotateCarClockwise(carTwoIndex);
         
    }  
}

private int rotateCarClockwise(int carIndex)
{
    //reset the rotation
    if (carIndex >= 14)
    {
        carIndex = 0;
        return carIndex;
    }
    //rotate the car clockwise
    carIndex += 1;
    return carIndex; 
}

private int rotateCarAntiClockwise(int carIndex)
{
    //reset the rotation
    if (carIndex <= 0)
    {
        carIndex = 14;
        return carIndex;
    }
    //rotate the car anticlockwise
    carIndex -= 1;
    return carIndex;
}

private int increaseCarSpeed(int carSpeed)
{
    //Check if the maximum car speed has speed reached
    if(carSpeed > 90)
    {
       return carSpeed;  
    }
    //Increase the carSpeed by 10
    carSpeed += 10;
    
    return carSpeed;
}

private int decreaseCarSpeed(int carSpeed)
{
    //Check if the minimum car speed has been reached
    if (carSpeed < 10)
    {
        return carSpeed; 
    }
    //Decrease the carSpeed by 10
    carSpeed -= 10;
    
    return carSpeed;
}

private double getRotationAxis(int arrayIndex, double rotationAxis)
{
    //Get the rotational axis in radians 
    switch(arrayIndex){
        case 0: 
            rotationAxis = 0;
            break;
        case 1:
            rotationAxis = 0.3926991;          
            break;
        case 2: 
            rotationAxis = 0.785398;
            break;
        case 3:
            rotationAxis = 1.178097;
            break;
        case 4:
            rotationAxis = 1.5708;
            break;
        case 5:
            rotationAxis = 1.9634954;
            break;
        case 6:
            rotationAxis = 2.35619;
            break;
        case 7:
            rotationAxis = 2.7488936;
            break;
        case 8:
            rotationAxis = 3.14159;
            break;
        case 9: 
            rotationAxis = 3.5342917;
            break;
        case 10: 
            rotationAxis = 3.92699;
            break;
        case 11:
            rotationAxis = 4.3196899;
            break;
        case 12: 
            rotationAxis = 4.71239;
            break;
        case 13:
            rotationAxis = 5.1050881;
            break;
        case 14: 
            rotationAxis = 5.49779;
            break;
        case 15: 
            rotationAxis = 5.8904862;           
}
    //return the correct axis in correspondence with the car's imageIcon array
    return rotationAxis;           
}

private int getPositionX(int x, int carSpeed, double rotationAxis)
{
    //Calculate the X posistion based on carSpeed and the angle of direction. 
    x += (carSpeed * Math.cos(rotationAxis));
    
    return x;
}

private int getPositionY(int y, int carSpeed, double rotationAxis)
{
    //Calculate the Y posistion based on carSpeed and the angle of direction. 
    y += (carSpeed * Math.sin(rotationAxis));
    
    return y; 
}

//Used to check the users key entries  
@Override
    public void keyPressed(KeyEvent e) 
    {
        
            performKeyAction(e);
                       
    }
    
    ///IDE genertated code associated with the keyListenerObject the class is inheriting from. 
    public void keyTyped(KeyEvent e){}

        ///IDE genertated code
    public void keyReleased(KeyEvent e){}
   
}


