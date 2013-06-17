package soldats;

public class Node
{
    private List<Node> _sons;
	
	// 0 : Vide, 1 : Blanc, 2 : Noir
	private byte[][] _gameBoard;
	private byte _turn = 0;
	
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

	public Node(byte[][] gameBoard, byte turn = BestSoldier.WHITE, int generationsNumber = 5)
	{
		this._gameBoard = Arrays.copyOf(gameBoard, gameBoard.length);
		
		if(generationsNumber > 0)
		{
			sons = new List<Node>();
			
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
								sons.add(new Node(sonGameBoard, (turn == BestSoldier.WHITE ? BestSoldier.BLACK : BestSoldier.WHITE), generationsNumber - 1));
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
									sons.add(new Node(sonGameBoard, (turn == BestSoldier.WHITE ? BestSoldier.BLACK : BestSoldier.WHITE), generationsNumber - 1));
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
}