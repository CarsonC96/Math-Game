/*
 * 
 *  */

package com.mathgame.math;

import javax.swing.*;

import com.mathgame.database.*;

import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.net.URL;


/**
 *The main class of the program
 */
public class MathGame extends JApplet implements ActionListener
{

	private static final long serialVersionUID = 412526093812019078L;
	int appWidth=900;//1300 or 900
	int appHeight=620;
	static int difficulty = 2;//from 2-5 represents how many cards to use
	
	JPanel panel2;
	JPanel panel2a;
	JPanel panel2b;
	
	//Panel Declarations
	JLayeredPane layer;//Master panel - particularly for moving cards across entire screen
	SidePanel sidePanel;//control panel on the side
	OperationPanel opPanel;//panel that holds operations + - / *
	CardPanel cardPanel;//holds cards at top
	WorkspacePanel workPanel;//center of screen where cards are morphed together
	HoldPanel holdPanel;//holds intermediate sums, differences, products, and quotients
	
	Rectangle home1;
	Rectangle home2;
	Rectangle home3;
	Rectangle home4;
	Rectangle home5;
	Rectangle home6;
	
	Point[] placesHomes = new Point[12];
	
	JLabel correct;
	int answerA;
	int answerS;
	int answerM;
	float answerD;
	
	int enterAction;//0-3
	JButton random;
	JButton clear;
	
	JCheckBox database;
	static boolean useDatabase = false;
	MySQLAccess sql;
	
	JCheckBox freeStyle;
	boolean practice = false;
	
	Double check; //stores the value that is generated by randomize

	//JComboBox operator;
	
	JLabel correction;
	
	GridBagConstraints c;
	
	JLabel[] cards = new JLabel[11];//card1, card2..opA,S...
	Rectangle[] cardHomes = new Rectangle[11];//home1, home2...opA,S...
	String[] cardVals = new String[11];
	
	
	String[] operations = {"+", "-", "*", "/"};
	
	CompMover mover;
	
	/**
	 * Initializes the window & game
	 */
	@Override
	public void init(){
		System.out.println("initing");
		setSize(appWidth, appHeight);
		setLayout(null);
		//((JComponent) getContentPane()).setBorder(new LineBorder(Color.yellow));
		//setBorder(new LineBorder(Color.yellow));
		
		//Initiation of panels
		layer = new JLayeredPane();
		layer.setLayout(null);
		layer.setBounds(5, 0, getSize().width, getSize().height);
		
		
		sidePanel = new SidePanel();//control bar
		sidePanel.setBounds(750, 0, 150, 620);//x, y, width, height
		sidePanel.init(this);
		
		cardPanel = new CardPanel(this);//top card panel
		cardPanel.setBounds(0, 0, 750, 150);
		cardPanel.init(layer);
		cardPanel.randomize( cardPanel.randomValues() );
		
		opPanel = new OperationPanel();//operation panel
		opPanel.setBounds(0, 150, 750, 60);
		opPanel.init(this, mover);
		
		workPanel = new WorkspacePanel();
		workPanel.setBounds(0, 210, 750, 260);
		workPanel.init(this);
		
		holdPanel = new HoldPanel();
		holdPanel.setBounds(0, 470, 750, 150);
		holdPanel.init(this);
		
		//adding panels to the game
		add(layer);
		add(sidePanel);
		layer.add(opPanel);
		layer.add(cardPanel);
		layer.add(workPanel);
		layer.add(holdPanel);
		
		database = new JCheckBox("Use Database");
		database.setMnemonic(KeyEvent.VK_D);
		
		sql = new MySQLAccess();
		
		freeStyle = new JCheckBox("Practice Mode");
		freeStyle.setMnemonic(KeyEvent.VK_P);
	
		home1 = new Rectangle(cardPanel.card1.getBounds());
		home2 = new Rectangle(cardPanel.card2.getBounds());
		home3 = new Rectangle(cardPanel.card3.getBounds());
		home4 = new Rectangle(cardPanel.card4.getBounds());
		home5 = new Rectangle(cardPanel.card5.getBounds());
		home6 = new Rectangle(cardPanel.card6.getBounds());
		
		cardHomes[0] = cardPanel.card1.getBounds();
		cardHomes[1] = cardPanel.card2.getBounds();
		cardHomes[2] = cardPanel.card3.getBounds();
		cardHomes[3] = cardPanel.card4.getBounds();
		cardHomes[4] = cardPanel.card5.getBounds();
		cardHomes[5] = cardPanel.card6.getBounds();
		cardHomes[6] = cardPanel.ans.getBounds();
		cardHomes[7] = opPanel.add.getBounds();
		cardHomes[8] = opPanel.subtract.getBounds();
		cardHomes[9] = opPanel.multiply.getBounds();
		cardHomes[10] = opPanel.divide.getBounds();
		
		cards[0] = cardPanel.card1;
		cards[1] = cardPanel.card2;
		cards[2] = cardPanel.card3;
		cards[3] = cardPanel.card4;
		cards[4] = cardPanel.card5;
		cards[5] = cardPanel.card6;
		cards[6] = cardPanel.ans;
		cards[7] = opPanel.add;
		cards[8] = opPanel.subtract;
		cards[9] = opPanel.multiply;
		cards[10] = opPanel.divide;

		cardPanel.card1.setTransferHandler(new TransferHandler("text"));
		cardPanel.card2.setTransferHandler(new TransferHandler("text"));
		cardPanel.card3.setTransferHandler(new TransferHandler("text"));
		cardPanel.card4.setTransferHandler(new TransferHandler("text"));
		cardPanel.card5.setTransferHandler(new TransferHandler("text"));
		cardPanel.card6.setTransferHandler(new TransferHandler("text"));
		//cardPanel.ans.setTransferHandler(new TransferHandler("text"));
		
		DropTarget dt = new DropTarget();
		dt.setActive(false);
		cardPanel.card1.setDropTarget(dt);
		cardPanel.card2.setDropTarget(dt);
		cardPanel.card3.setDropTarget(dt);
		cardPanel.card4.setDropTarget(dt);
		cardPanel.card5.setDropTarget(dt);
		cardPanel.card6.setDropTarget(dt);
		//cardPanel.ans.setDropTarget(dt);

		//ACTION LISTENERS
		mover = new CompMover(this);
		//handles 6 cards
		cardPanel.card1.addMouseListener(mover);
		cardPanel.card2.addMouseListener(mover);
		cardPanel.card3.addMouseListener(mover);
		cardPanel.card4.addMouseListener(mover);
		cardPanel.card5.addMouseListener(mover);
		cardPanel.card6.addMouseListener(mover);
		//cardPanel.ans.addMouseListener(mover);
		
		cardPanel.card1.addMouseMotionListener(mover);
		cardPanel.card2.addMouseMotionListener(mover);
		cardPanel.card3.addMouseMotionListener(mover);
		cardPanel.card4.addMouseMotionListener(mover);
		cardPanel.card5.addMouseMotionListener(mover);
		cardPanel.card6.addMouseMotionListener(mover);
		//cardPanel.ans.addMouseMotionListener(mover);
		
		//handles 4 operations
		opPanel.add.addMouseListener(mover);
		opPanel.subtract.addMouseListener(mover);
		opPanel.multiply.addMouseListener(mover);
		opPanel.divide.addMouseListener(mover);

		opPanel.add.addMouseMotionListener(mover);
		opPanel.subtract.addMouseMotionListener(mover);
		opPanel.multiply.addMouseMotionListener(mover);
		opPanel.divide.addMouseMotionListener(mover);
		//adds to layered pane to facilitate movement across ALL panels
		layer.add(cardPanel.card1, new Integer(1));//adding new integer ensures card is on top
		layer.add(cardPanel.card2, new Integer(1));
		layer.add(cardPanel.card3, new Integer(1));
		layer.add(cardPanel.card4, new Integer(1));
		layer.add(cardPanel.card5, new Integer(1));
		layer.add(cardPanel.card6, new Integer(1));
		
		layer.add(cardPanel.ans, new Integer(1));//holds the answer		
		layer.add(opPanel.add, new Integer(1));
		layer.add(opPanel.subtract, new Integer(1));
		layer.add(opPanel.multiply, new Integer(1));
		layer.add(opPanel.divide, new Integer(1));
		
		Items itemListener = new Items(this);
		database.addItemListener(itemListener);
		freeStyle.addItemListener(itemListener);
		
		/*//Code for a different Cursor
		Toolkit toolkit = getToolkit();
		Image cursorImage = toolkit.getImage("images/epsilon.png");
		Point cursorHotSpot = new Point(25, 25);
		Cursor imageCursor = toolkit.createCustomCursor(cursorImage, cursorHotSpot, "customCursor");

		setCursor(lightPenCursor);
		layer.setCursor(imageCursor);
		 */
		System.out.println("init done");
	}

	
	@Override
	public void actionPerformed(ActionEvent evt)
	{
		
	}
	
	/**
	 * Sets the difficulty of the game.
	 * 
	 * Difficulty increases based on the number of cards used. If 5 cards are used, it will be much harder
	 * to figure out the right order to use the cards.
	 * 
	 * @param diff Number from 2- #of cards in game 
	 */
	public void setDifficulty(int diff){
		difficulty = diff;
		System.out.println("DIFFICULTY FROM MAIN SET: " + difficulty);
	}
	
	/**
	 * Gets the difficulty of the game
	 */
	public int getDifficulty(){
		System.out.println("DIFFICULTY FROM MAIN GET: " + difficulty);
		return difficulty;
	}
	
	/**
	 * Booleans are immutable, so the value of practice can't be set by referencing it in the Items class
	 * @param prac Whether practice mode should be enabled or not
	 */
	public void setPractice(boolean prac){
		practice = prac;
	}
	
	
	public void setCheck(Double tCheck){
		check = tCheck;
	}
	/**
	 * Specifies whether or not to use the database
	 * @param database true or false
	 */
	public void setDatabase(boolean database){
		useDatabase = database;
		System.out.println("SET DATABASE " + useDatabase);
	}
	
	public boolean getDatabase(){
		System.out.println("GET DATABASE " + useDatabase);
		return useDatabase;
	}
	
	
	public URL getDocBase(){
		return getDocumentBase();
	}
	
	/*
	public Container getCompParent(int i){
		return cards[i].getParent();
	}
	
	public JLabel getComps(int i){
		return cards[i];
	}
	*/
	
//	public void error(){
	//	sidePanel.error.setText(sql.sqlError);
	//}
	
}//class file