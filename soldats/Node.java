package soldats;

public class Node
{
	private static final int MAX_GENERATIONS = 5;

    private List<Node> _sons;
	// 0 : Vide, 1 : Blanc, 2 : Noir
	private byte[][] _gameBoard;
	
	private Node _bestSon;
	
	private byte _color;
	private byte _turn = 0;
	
	private int _generationsCount;
	
	private byte _whiteSoldiersCount = 0;	
	public byte getWhiteSoldiersCount()
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
	
	private byte _blackSoldiersCount = 0;	
	public byte getBlackSoldiersCount()
	{
		if(_blackSoldiersCount == 0)
		{
			for(int i=0;i<gameBoard.length;++i)
			{
				for(int j=0;j<gameBoard[i].length;++j)
				{
					if(gameBoard[i][j] == BestSoldier.WHITE) ++_blackSoldiersCount;
				}
			}
		}
		
		return _blackSoldiersCount;
	}
	
	public byte getSoldiersCount()
	{
		if(this._color == BestSoldier.WHITE)
			return getWhiteSoldiersCount() - getBlackSoldiersCount();
		else
			return getBlackSoldiersCount() - getWhiteSoldiersCount();
	}

	public Node(byte[][] gameBoard, byte color, byte turn = BestSoldier.WHITE, int generationsCount = MAX_GENERATIONS) throws IllegalArgumentException
	{
		if(color != BestSoldier.WHITE && color != BestSoldier.BLACK)
			throw new IllegalArgumentException("Couleur inexistante");
		
		this._color = color;		
		this._gameBoard = Arrays.copyOf(gameBoard, gameBoard.length);
		this._generationsCount = generationsCount;
		
		sons = new List<Node>();
		if(generationsNumber > 0)
		{
			for(int i=0;i<gameBoard.length;++i)
			{
				for(int j=0;j<gameBoard[i].length;++j)
				{
					if((gameBoard[i][j] == turn) && (BestSoldier.movements[i][j].length > 1))
					{
						for(int k=0;k<BestSoldier.movements[i][j].length;++k)
						{
							// Case vide
							int nextCol = colMov[BestSoldier.movements[i][j][k]-1];
							int nextLine = rowMov[BestSoldier.movements[i][j][k]-1];
							nextCol = (BestSoldier.movements[i + nextCol][j + nextLine].length == 1 ? colMov[BestSoldier.movements[i][j][k]-1]*2 : nextCol);
							nextLine = (BestSoldier.movements[i + nextCol][j + nextLine].length == 1 ? rowMov[BestSoldier.movements[i][j][k]-1]*2 : nextLine);
							if(gameBoard[i + nextCol][j + nextLine] ==  0)
							{
								// On créé un nouveau fils								
								byte[][] sonGameBoard = Arrays.copyOf(this._gameBoard, this._gameBoard.length);
								sonGameBoard[i + nextCol][j + nextLine] = sonGameBoard[i][j];
								sonGameBoard[i][j] = 0;
								sons.add(new Node(sonGameBoard, _color, (turn == BestSoldier.WHITE ? BestSoldier.BLACK : BestSoldier.WHITE), generationsNumber - 1));
							}
							// Ennemi
							else if(gameBoard[i + nextCol][j + nextLine] !=  gameBoard[i][j])
							{
								// Vérifier qu'on peut manger l'ennemi
								if(BestSoldier.movements[i + nextCol * 2][j + nextLine * 2].length == 1 && gameBoard[i + nextCol * 2][j + nextLine * 2] == 0)
								{
									// On créé un nouveau fils								
									byte[][] sonGameBoard = Arrays.copyOf(this._gameBoard, this._gameBoard.length);
									sonGameBoard[i + nextCol * 2][j + nextLine * 2] = sonGameBoard[i][j];
									sonGameBoard[i + nextCol][j + nextLine] = 0;
									sonGameBoard[i][j] = 0;
									sons.add(new Node(sonGameBoard, _color, (turn == BestSoldier.WHITE ? BestSoldier.BLACK : BestSoldier.WHITE), generationsNumber - 1));
								}
							}
						}
					}
				}
			}
		}

		// Tous les fils ont été crées (leurs fils aussi) : lancer aB
		// ...
		
		
	}
	
	private static int MaxValue(Node node, int alpha, int beta, int generation)
	{
		// aB : MaxValue
		if(node._sons.count() == 0)
			return getSoldiersCount();
			
		for(int i=0;i<node._sons.count();++i)
		{
			int newAlpha = MinValue(node._sons.get(i), alpha, beta);
			alpha = (alpha > newAlpha ? alpha : newAlpha);
			if(node._generationsCount == MAX_GENERATIONS && alpha > newAlpha)
			{
				node._bestSon = node._sons.get(i);
			}			
			
			if(alpha >= beta)
				return beta;
		}
		
		return alpha;
	}
	
	private static int MinValue(Node node, int alpha, int beta)
	{
		// aB : MaxValue
		if(node._sons.count() == 0)
			return getSoldiersCount();
			
		for(int i=0;i<node._sons.count();++i)
		{
			int newBeta = MaxValue(node._sons.get(i), alpha, beta);
			beta = (beta < newBeta ? beta : newBeta);
			
			if(alpha >= beta)
				return alpha;
		}
		
		return beta;		
	}
}