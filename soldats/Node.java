package soldats;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.lang.StringBuilder;

public class Node
{
    private ArrayList<Node> _sons;

    private byte _color;
    private byte _whiteSoldiersCount = 0;
    private byte _blackSoldiersCount = 0;
	private int _heuristique = 0;
	
	private byte[] _startPoint;
	private byte[] _finalPoint;
	private byte[] _ennemyPoint;
	
    private Node _bestSon;
	
    public byte getWhiteSoldiersCount(byte[][] gameBoard)
    {
        if(_whiteSoldiersCount == 0)
        {
            for(int i=0;i<gameBoard.length;++i)
            {
                for(int j=0;j<gameBoard[i].length;++j)
                {
                    if(gameBoard[i][j] == BestSoldier.WHITE) ++_whiteSoldiersCount;
                }
            }
        }

        return _whiteSoldiersCount;
    }

    public byte getBlackSoldiersCount(byte[][] gameBoard)
    {
        if(_blackSoldiersCount == 0)
        {
            for(int i=0;i<gameBoard.length;++i)
            {
                for(int j=0;j<gameBoard[i].length;++j)
                {
                    if(gameBoard[i][j] == BestSoldier.BLACK) ++_blackSoldiersCount;
                }
            }
        }

        return _blackSoldiersCount;
    }

    public int getHeuristique(byte[][] gameBoard)
    {
		if(_heuristique == 0)
        {
			if(this._color == BestSoldier.WHITE)
				_heuristique = getWhiteSoldiersCount(gameBoard) - getBlackSoldiersCount(gameBoard);
			else
				_heuristique = getBlackSoldiersCount(gameBoard) - getWhiteSoldiersCount(gameBoard);
		}
		
		return _heuristique;
    }
	
	public int getHeuristique()
	{
		return _heuristique;
	}

    public Node(byte[][] gameBoard, int color, int turn, int generationsCount) throws IllegalArgumentException
    {
        if(color != BestSoldier.WHITE && color != BestSoldier.BLACK)
            throw new IllegalArgumentException("Couleur inexistante");
        if(turn != BestSoldier.WHITE && turn != BestSoldier.BLACK)
            throw new IllegalArgumentException("Couleur inexistante");

        this._color = (byte)color;
        this._sons = new ArrayList<Node>();
        byte[][] copyGameBoard = new byte[gameBoard.length][gameBoard.length];
                                
        for(int l = 0; l < gameBoard.length; ++l)
        {
            for(int m = 0; m < gameBoard.length; ++m)
            {
                copyGameBoard[l][m] = gameBoard[l][m];
            }
        }
		this.getHeuristique(copyGameBoard);
		
		this.generateSons(copyGameBoard, turn, generationsCount);
	}
	
	
	// Constructeur sans prise de pion
	private Node(byte[][] gameBoard, int color, int turn, int generationsCount, byte[] startPoint, byte[] finalPoint)
	{
		this._color = (byte)color;
        this._sons = new ArrayList<Node>();
		this._startPoint = startPoint;
		this._finalPoint = finalPoint;
		
		// Bouger les pions nécessaires
		gameBoard[finalPoint[0]][finalPoint[1]] = gameBoard[startPoint[0]][startPoint[1]];
		gameBoard[startPoint[0]][startPoint[1]] = BestSoldier.EMPTY;
		this.getHeuristique(gameBoard);
		
		this.generateSons(gameBoard, turn, generationsCount);
		
		// Revenir à l'état initial
		gameBoard[startPoint[0]][startPoint[1]] = gameBoard[finalPoint[0]][finalPoint[1]];
		gameBoard[finalPoint[0]][finalPoint[1]] = BestSoldier.EMPTY;
	}
	
	// Constructeur avec prise de pion
	private Node(byte[][] gameBoard, int color, int turn, int generationsCount, byte[] startPoint, byte[] finalPoint, byte[] ennemyPoint)
	{
		this._color = (byte)color;
        this._sons = new ArrayList<Node>();
		this._startPoint = startPoint;
		this._finalPoint = finalPoint;
		this._ennemyPoint = ennemyPoint;
		
		// Bouger les pions nécessaires
		gameBoard[finalPoint[0]][finalPoint[1]] = gameBoard[startPoint[0]][startPoint[1]];
		gameBoard[ennemyPoint[0]][ennemyPoint[1]] = BestSoldier.EMPTY;
		gameBoard[startPoint[0]][startPoint[1]] = BestSoldier.EMPTY;
		this.getHeuristique(gameBoard);
		
		this.generateSons(gameBoard, turn, generationsCount);
		
		// Revenir à l'état initial
		gameBoard[startPoint[0]][startPoint[1]] = gameBoard[finalPoint[0]][finalPoint[1]];
		gameBoard[ennemyPoint[0]][ennemyPoint[1]] = (turn == BestSoldier.WHITE ? BestSoldier.WHITE : BestSoldier.BLACK);
		gameBoard[finalPoint[0]][finalPoint[1]] = BestSoldier.EMPTY;
	}
	
	private void generateSons(byte[][] gameBoard, int turn, int generationsCount)
	{
		if(generationsCount > 0)
        {
            for(int i=0;i<gameBoard.length;++i)
            {
                for(int j=0;j<gameBoard[i].length;++j)
                {
                    if((gameBoard[i][j] == turn) && (BestSoldier.movements[i][j].length > 1))
                    {                        
                        for(int k = 0; k<BestSoldier.movements[i][j].length; ++k)
                        {
                            // Case vide
                            int nextCol = BestSoldier.colMov[BestSoldier.movements[i][j][k]-1];
                            int nextLine = BestSoldier.rowMov[BestSoldier.movements[i][j][k]-1];
                            nextCol = (BestSoldier.movements[i + nextCol][j + nextLine].length == 1 ? BestSoldier.colMov[BestSoldier.movements[i][j][k]-1]*2 : nextCol);
                            nextLine = (BestSoldier.movements[i + nextCol][j + nextLine].length == 1 ? BestSoldier.rowMov[BestSoldier.movements[i][j][k]-1]*2 : nextLine);
                            
                            if(gameBoard[i + nextCol][j + nextLine] ==  0)
                            {
                                // On créé un nouveau fils
								byte[] startPoint = new byte[] { (byte)i , (byte)j };
								byte[] finalPoint = new byte[] { (byte)(i + nextCol) , (byte)(j + nextLine) };
                                _sons.add(new Node(gameBoard, _color, (turn == BestSoldier.WHITE ? BestSoldier.BLACK : BestSoldier.WHITE), generationsCount - 1, startPoint, finalPoint));
                            }
                            else if(gameBoard[i + nextCol][j + nextLine] !=  gameBoard[i][j]) // Ennemi
                            {                                
                                // Vérifier qu'on peut manger l'ennemi
                                int y = i + nextCol * 2, x = j + nextLine * 2;
                                
                                if((x >= 0 && x < gameBoard.length) && (y >= 0 && y < gameBoard.length) && BestSoldier.movements[y][x].length != 1 && gameBoard[y][x] == 0)
                                {
									boolean possibleMovement = false;
								
									for(int l = 0; l < BestSoldier.movements[i + nextCol][j + nextLine].length; ++l)
										if(BestSoldier.movements[i + nextCol][j + nextLine][l] == BestSoldier.movements[i][j][k])
											possibleMovement = true;
											
									if(possibleMovement == false)
										continue;
									
                                    // On créé un nouveau fils
									byte[] startPoint = new byte[] { (byte)i , (byte)j };
									byte[] finalPoint = new byte[] {(byte)y , (byte)x };
									byte[] ennemyPoint = new byte[] { (byte)(i + nextCol) , (byte)(j + nextLine) };
									
                                    _sons.add(new Node(gameBoard, _color, (turn == BestSoldier.WHITE ? BestSoldier.BLACK : BestSoldier.WHITE), generationsCount - 1, startPoint, finalPoint, ennemyPoint));
                                }
                            }
                        }
                    }
                }
            }
        }
	}

    public void Evaluation()
    {
        // Tous les fils ont été crées (leurs fils aussi) : lancer aB
        MaxValue(this, Integer.MIN_VALUE, Integer.MAX_VALUE, BestSoldier.MAX_GENERATIONS);
    }
    
    private static int MaxValue(Node node, int alpha, int beta, int generationsCount)
    {
        // aB : MaxValue
        if(node._sons.size() == 0 || generationsCount == 0)
            return node.getHeuristique();
        
        for(int i=0;i<node._sons.size();++i)
        {
            int newAlpha = MinValue(node._sons.get(i), alpha, beta, generationsCount - 1);
            
            if(alpha < newAlpha)
            {
                alpha = newAlpha;
                
                if(generationsCount == BestSoldier.MAX_GENERATIONS)
                {
                    node._bestSon = node._sons.get(i);
                }
            }

            if(alpha >= beta)
                return beta;
        }

        return alpha;
    }

    private static int MinValue(Node node, int alpha, int beta, int generationsCount)
    {
        // aB : MaxValue
        if(node._sons.size() == 0 || generationsCount == 0)
            return node.getHeuristique();
        
        for(int i=0;i<node._sons.size();++i)
        {
            int newBeta = MaxValue(node._sons.get(i), alpha, beta, generationsCount - 1);
            beta = (beta < newBeta ? beta : newBeta);

            if(alpha >= beta)
                return alpha;
        }

        return beta;
    }
    
    
    // Appelé après l'instantiation d'un Node et l'exécution d'alphaBeta. Fait un diff entre <noeud courant> et "bestSon" pour donner le meilleur mouvement
    public String BestMovement()
    {
        String oldPlace = "", newPlace = "";
        
		if(this._bestSon != null)
		{
			oldPlace = (this._bestSon._startPoint[0] + 1) + " " + (this._bestSon._startPoint[1] + 1) + " ";
			newPlace = (this._bestSon._finalPoint[0] + 1) + " " + (this._bestSon._finalPoint[1] + 1) + "\0";
		}
        
        return oldPlace + newPlace;
        // FORMAT COLONNE / LIGNE
    }
    
    public void UpdateGameBoard(byte[][] gameBoard) throws IllegalArgumentException
    {
        if(_bestSon == null)
            throw new IllegalArgumentException("Plateau de jeu du prochain coup == null");
		
		gameBoard[this._bestSon._finalPoint[0]][this._bestSon._finalPoint[1]] = gameBoard[this._bestSon._startPoint[0]][this._bestSon._startPoint[1]];
		gameBoard[this._bestSon._startPoint[0]][this._bestSon._startPoint[1]] = BestSoldier.EMPTY;
		if(this._bestSon._ennemyPoint != null)
			gameBoard[this._bestSon._ennemyPoint[0]][this._bestSon._ennemyPoint[1]] = BestSoldier.EMPTY;
    }
}