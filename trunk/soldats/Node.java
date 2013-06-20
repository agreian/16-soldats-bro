package soldats;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.lang.StringBuilder;
import java.util.Random;

public class Node
{
    private byte _color;
    private byte _whiteSoldiersCount = 0;
    private byte _blackSoldiersCount = 0;
	private int _heuristique = 0;
	
	private int _alpha;
	private int _beta;
	private int _return;
	
	private byte[] _startPoint;
	private byte[] _finalPoint;
	private byte[] _ennemyPoint;
	
    private Node _bestSon = null;
	
	private static Random _rnd;
	static
	{
		_rnd = new Random();
	}
	
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
	
    public Node(byte[][] gameBoard, int color, int turn, int generationsCount) throws IllegalArgumentException
    {
        if(color != BestSoldier.WHITE && color != BestSoldier.BLACK)
            throw new IllegalArgumentException("Couleur inexistante");
        if(turn != BestSoldier.WHITE && turn != BestSoldier.BLACK)
            throw new IllegalArgumentException("Couleur inexistante");
		
		this._alpha = Integer.MIN_VALUE;
		this._beta = Integer.MAX_VALUE;

        this._color = (byte)color;
		
        byte[][] copyGameBoard = new byte[gameBoard.length][gameBoard.length];
                                
        for(int l = 0; l < gameBoard.length; ++l)
        {
            for(int m = 0; m < gameBoard.length; ++m)
            {
                copyGameBoard[l][m] = gameBoard[l][m];
            }
        }
		// this.getHeuristique(copyGameBoard);
		
		this.AlphaBeta(copyGameBoard, turn, generationsCount);
	}
	
	private Node(byte[][] gameBoard, int color, int turn, int generationsCount, byte[] startPoint, byte[] finalPoint, byte[] ennemyPoint, int alpha, int beta)
	{
		this._color = (byte)color;
		this._startPoint = startPoint;
		this._finalPoint = finalPoint;
		if(ennemyPoint != null)
			this._ennemyPoint = ennemyPoint;
		
		this._alpha = alpha;
		this._beta = beta;
		
		// Bouger les pions nécessaires
		gameBoard[finalPoint[0]][finalPoint[1]] = gameBoard[startPoint[0]][startPoint[1]];
		gameBoard[startPoint[0]][startPoint[1]] = BestSoldier.EMPTY;
		if(ennemyPoint != null)
			gameBoard[ennemyPoint[0]][ennemyPoint[1]] = BestSoldier.EMPTY;
		//this.getHeuristique(gameBoard);
		
		this.AlphaBeta(gameBoard, turn, generationsCount);
		
		// Revenir à l'état initial
		gameBoard[startPoint[0]][startPoint[1]] = gameBoard[finalPoint[0]][finalPoint[1]];
		gameBoard[finalPoint[0]][finalPoint[1]] = BestSoldier.EMPTY;
		if(ennemyPoint != null)
			gameBoard[ennemyPoint[0]][ennemyPoint[1]] = (turn == BestSoldier.WHITE ? BestSoldier.WHITE : BestSoldier.BLACK);
	}
	
	private void AlphaBeta(byte[][] gameBoard, int turn, int generationsCount)
	{
		if(generationsCount == 0)
		{
			////System.out.println("Heuristique en cours");
			this._return = getHeuristique(gameBoard);
			////System.out.println("Heuristique " + this._return );
			return;
		}
		else if(generationsCount > 0)
        {
			int random = _rnd.nextInt(4);
			switch(random)
			{
				case 0:
				{
					for(int i=0;i<gameBoard.length;++i)
						for(int j=0;j<gameBoard.length;++j)
							if(AlphaBeta(i, j, gameBoard, turn, generationsCount) == true)
								return;
					break;
				}
				case 1:
				{
					for(int i = (gameBoard.length - 1); i >= 0; --i)
						for(int j = (gameBoard.length - 1); j >= 0; --j)
							if(AlphaBeta(i, j, gameBoard, turn, generationsCount) == true)
								return;
					break;
				}
				case 2:
				{
					for(int j=0;j<gameBoard.length;++j)
						for(int i=0;i<gameBoard.length;++i)
							if(AlphaBeta(i, j, gameBoard, turn, generationsCount) == true)
								return;
					break;
				}
				case 3:
				{
					for(int j = (gameBoard.length - 1); j >= 0; --j)
						for(int i = (gameBoard.length - 1); i >= 0; --i)
							if(AlphaBeta(i, j, gameBoard, turn, generationsCount) == true)
								return;
					break;
				}
				default:
				{
					System.out.println("ERROR");
					break;
				}
			}
			
			if(turn == _color)
			{
				// MaxValue
				this._return = _alpha;
				return;
			}
			else
			{
				// MinValue
				this._return = _beta;
				return;
			}
        }
	}
	
	private boolean AlphaBeta(int i, int j, byte[][] gameBoard, int turn, int generationsCount)
	{
		if((gameBoard[i][j] == turn) && (BestSoldier.movements[i][j].length > 1))
		{                        
			for(int k = 0; k < BestSoldier.movements[i][j].length; ++k)
			{
				int nextCol = BestSoldier.colMov[BestSoldier.movements[i][j][k]-1];
				int nextLine = BestSoldier.rowMov[BestSoldier.movements[i][j][k]-1];
				nextCol = (BestSoldier.movements[i + nextCol][j + nextLine].length == 1 ? BestSoldier.colMov[BestSoldier.movements[i][j][k]-1]*2 : nextCol);
				nextLine = (BestSoldier.movements[i + nextCol][j + nextLine].length == 1 ? BestSoldier.rowMov[BestSoldier.movements[i][j][k]-1]*2 : nextLine);
				
				byte[] startPoint = null;
				byte[] finalPoint = null;
				byte[] ennemyPoint = null;
				
				// Case vide
				if(gameBoard[i + nextCol][j + nextLine] ==  0)
				{
					////System.out.println("VIDE");
					// On créé un nouveau fils
					startPoint = new byte[] { (byte)i , (byte)j };
					finalPoint = new byte[] { (byte)(i + nextCol) , (byte)(j + nextLine) };
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
						startPoint = new byte[] { (byte)i , (byte)j };
						finalPoint = new byte[] {(byte)y , (byte)x };
						ennemyPoint = new byte[] { (byte)(i + nextCol) , (byte)(j + nextLine) };
					}
					else
					{
						continue;
					}
				}
				else
				{
					continue;
				}
				////System.out.println("LOL");
				Node son = new Node(gameBoard, 
					_color, 
					(turn == BestSoldier.WHITE ? BestSoldier.BLACK : BestSoldier.WHITE), 
					generationsCount - 1, 
					startPoint, finalPoint, ennemyPoint, 
					_alpha, _beta);
				////System.out.println("LOL2");
				if(turn == _color)
				{
					// MaxValue
					int newAlpha = son._return;
					if(_alpha < newAlpha)
					{
						_alpha = newAlpha;
						
						if(generationsCount == BestSoldier.MAX_GENERATIONS)
						{
							this._bestSon = son;
						}
					}

					if(_alpha >= _beta)
					{
						this._return = _beta;
						return true;
					}
				}
				else
				{
					// MinValue
					int newBeta = son._return;
					_beta = (_beta < newBeta ? _beta : newBeta);

					if(_alpha >= _beta)
					{
						this._return = _alpha;
						return true;
					}
				}
			}
		}
		
		return false;
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