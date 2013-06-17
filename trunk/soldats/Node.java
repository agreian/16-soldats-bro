package soldats;

public class Node
{
    private List<Node> _sons;
	
	// 0 : Vide, 1 : Blanc, 2 : Noir
	private byte[][] _gameBoard;
	
	private int _whiteSoldiersCount = 0;	
	public int getWhiteSoldiersCount()
	{
		if(_whiteSoldiersCount == 0)
		{
			for(int i=0;i<gameBoard.Length;++i)
			{
				for(int j=0;j<gameBoard[i].Length;++j)
				{
					if(gameBoard[i][j] == BestSoldier.WHITE) ++_whiteSoldiersCount;
				}
			}
		}
		
		return _whiteSoldiersCount;
	}
	
	private int _blackSoldiersCount = 0;	
	public int getBlackSoldiersCount()
	{
		if(_blackSoldiersCount == 0)
		{
			for(int i=0;i<gameBoard.Length;++i)
			{
				for(int j=0;j<gameBoard[i].Length;++j)
				{
					if(gameBoard[i][j] == BestSoldier.WHITE) ++_blackSoldiersCount;
				}
			}
		}
		
		return _blackSoldiersCount;
	}

	public Node(byte[][] gameBoard, int generationsNumber = 5)
	{
		this._gameBoard = Arrays.copyOf(gameBoard, gameBoard.length);
		
		if(generationsNumber > 0)
		{
			sons = new List<Node>();
			
			for(int i=0;i<gameBoard.Length;++i)
			{
				for(int j=0;j<gameBoard[i].Length;++j)
				{
					
				}
			}
		}
			
	}
}