package soldats;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AppletSoldats extends JApplet

{
	final static int WHITE = 1;
	final static int BLACK = 2;
	final static int EMPTY = 0;
	final private static int TAILLE = 9;
	final private static int TAILLEPIONS = 40;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JList brdList;
	private SoldierBoard displayBoard;
	private JScrollPane scrollPane;
	private DefaultListModel listModel;
	private Frame myFrame;
	
	static int cpt = 0;
	private int[] XInc = {0,-1,0,1, +1,-1,-1,+1};
	private int[] YInc = {-1,0,1,0, -1,-1,+1,+1};
	private final static int NBDIR = 8;
	private final static int NBDIR2 = 4;
	
	private int[][] SMoves= {{0,0,83,0,134,0,15,0,0},
								{0,0,0,386,1234,157,0,0,0},
								{84,0,384,134,12345678,143,154,0,54},
								{0,486,243,12345678,1234,12345678,124,754,0},
								{243,1234,12345678,1234,12345678,1234,12345678,1234,241},
								{0,275,243,12345678,1234,12345678,124,268,0},
								{27,0,372,132,12345678,123,162,0,62},
								{0,0,0,375,1234,168,0,0,0},
								{0,0,73,0,132,0,16,0,0}};
	// 
	public void init(){
		System.out.println("Initialisation BoardApplet" + cpt++);
		buildUI(getContentPane());
	}
	
	public void buildUI(Container container) {
		setBackground(Color.white);
		
		int[][] temp = new int[TAILLE][TAILLE];
		
		for( int i = 0; i < TAILLE; i++ )
			for( int j = 0; j < TAILLE; j++ )
				temp[i][j] = -1;
		
		// 
		displayBoard = new SoldierBoard( "Coups", temp );
		
		listModel = new DefaultListModel();
		listModel.addElement(displayBoard);
		
		brdList = new JList(listModel);
		brdList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		brdList.setSelectedIndex(0);
		scrollPane = new JScrollPane(brdList);
		Dimension d = scrollPane.getSize();
		scrollPane.setPreferredSize(new Dimension( 110, d.height ));
		
		brdList.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				brdList_keyPressed(e);
			}
		});
		brdList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				brdList_mouseClicked(e);
			}
		});
		container.add(displayBoard, BorderLayout.CENTER);
		container.add(scrollPane, BorderLayout.EAST);	
	}
	
	// d
	public void update(Graphics g, Insets in)
	{
		Insets tempIn = in;
		g.translate( tempIn.left, tempIn.top);
		paint(g);
	}
	
	public void paint(Graphics g)
	{
		displayBoard.paint(g);
	}
	

	public void addBoard( String move, int[][] board ){
		SoldierBoard tempEntrop = new SoldierBoard( move, board );
		listModel.addElement(new SoldierBoard( move, board ));
		brdList.setSelectedIndex(listModel.getSize()-1);
		brdList.ensureIndexIsVisible(listModel.getSize()-1);
		displayBoard = tempEntrop;
		update(myFrame.getGraphics(), myFrame.getInsets() );
	}
	
	//
	public void setMyFrame( Frame f ){
		myFrame = f;
	}
	
	// 
	void brdList_keyPressed( KeyEvent e ){
		int index = brdList.getSelectedIndex(); 
		if( e.getKeyCode() == KeyEvent.VK_UP && index > 0 )
			displayBoard = (SoldierBoard)listModel.getElementAt(index-1);
		
		if( e.getKeyCode() == KeyEvent.VK_DOWN && index < (listModel.getSize()-1) )
			displayBoard = (SoldierBoard)listModel.getElementAt(index+1);
		
		update(myFrame.getGraphics(), myFrame.getInsets() );
		
		
	}
	
	// 
	void brdList_mouseClicked( MouseEvent e ){
		displayBoard = (SoldierBoard)listModel.getElementAt(brdList.getSelectedIndex());
		update(myFrame.getGraphics(), myFrame.getInsets());
	}
	
//	Sous classe qui dessine le plateau de jeu
	class SoldierBoard extends JPanel{
		
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int[][] boardState;
		String move;
		
		// The string will be the move details
		// and the array the details of the board
		// after the move has been applied.
		public SoldierBoard( String mv, int[][] bs ){
			boardState = bs;
			move = mv;
		}
		
		
		public void drawBoard( Graphics g ){
			// First draw the lines
			// Board
			int bx=30; int by=30;
			boolean alt = true;
			
			g.setColor(new Color(0,0,0));
			for (int i = 1; i <= TAILLE; i ++ ){
				g.drawString(""+i, i*TAILLEPIONS+10, 20);
				g.drawString(""+i, 5, i*TAILLEPIONS+10);
			}
				
			Color c1 = new Color(200,200,200);
			Color c2 = new Color(240,240,240);
			for( int i = 0; i < TAILLEPIONS * TAILLE; i += TAILLEPIONS ){
				for( int j = 0; j < TAILLEPIONS * TAILLE; j += TAILLEPIONS ){
						g.setColor(alt?c1:c2); alt=!alt;
						g.fillRect(bx+i, by+j, TAILLEPIONS,  TAILLEPIONS);
				}
			}
            // Lines on board
			g.setColor( new Color(255, 255, 255) );
			for( int i = 0; i <= TAILLEPIONS * TAILLE; i += TAILLEPIONS ){
				g.drawLine( bx+i, by+0, bx+i, by+TAILLEPIONS*TAILLE);
				g.drawLine( bx+0, by+i, bx+TAILLEPIONS*TAILLE, by+ i);
			}
			
			// Small ovals for authorized places
			for( int i = 0; i < TAILLE; i++ ){
				for( int j = 0; j < TAILLE; j++ ){
					if (SMoves[i][j] == 0)
						 g.setColor( new Color( 220, 180, 180 ) );
						else 
						  g.setColor(new Color(100,100,100));
					g.fillOval( bx+TAILLEPIONS*i+15, by+TAILLEPIONS*j+15, 10, 10 );
				}
			}
			
			// You can only move along those lines
			g.setColor(new Color(20,20,20));
			for( int i = 0; i < TAILLE; i++ ){
				for( int j = 0; j < TAILLE; j++ ){
					int directions = SMoves[i][j];
					while (directions > 0) {
						int tmp = directions % 10 - 1;
						directions = directions / 10;
						g.drawLine(bx+TAILLEPIONS*i+20, by+TAILLEPIONS*j+20, 
								bx+TAILLEPIONS*(i+XInc[tmp])+20, by+TAILLEPIONS*(j+YInc[tmp])+20);
					}
				}
			}
	
			// Draw the pieces by referencing boardState array
			for( int i = 0; i < TAILLE; i++ ){
				for( int j = 0; j < TAILLE; j++ ){
					switch( boardState[i][j] ){
					case(WHITE):
						g.setColor( new Color( 255, 255, 255 ) );
						g.fillOval( bx+TAILLEPIONS*i+4, by+TAILLEPIONS*j+4, TAILLEPIONS-6, TAILLEPIONS-6 );
						break;
					case(BLACK):
						g.setColor( new Color( 0, 0, 0 ) );
						g.fillOval( bx+TAILLEPIONS*i+4, by+TAILLEPIONS*j+4, TAILLEPIONS-6, TAILLEPIONS-6 );
					}
				}
			}
		}
		
		public void paint(Graphics g){
			drawBoard( g );
		}
		
		public void update(Graphics g){
			drawBoard(g);
		}
		
		public String toString(){
			return move;
		}
	}
	
}
