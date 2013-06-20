package soldats;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.lang.StringBuilder;

public class Node
{
    private ArrayList<Node> _sons;
    
    // 0 : Vide, 1 : Blanc, 2 : Noir
    private byte[][] _gameBoard;

    private Node _bestSon;

    private int _color;
    private int _turn;
	
    private int _whiteSoldiersCount = 0;
    public int getWhiteSoldiersCount()
    {
        if(_whiteSoldiersCount == 0)
        {
            for(int i=0;i<_gameBoard.length;++i)
            {
                for(int j=0;j<_gameBoard[i].length;++j)
                {
                    if(_gameBoard[i][j] == BestSoldier.WHITE) ++_whiteSoldiersCount;
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
            for(int i=0;i<_gameBoard.length;++i)
            {
                for(int j=0;j<_gameBoard[i].length;++j)
                {
                    if(_gameBoard[i][j] == BestSoldier.BLACK) ++_blackSoldiersCount;
                }
            }
        }

        return _blackSoldiersCount;
    }

    public int getSoldiersCount()
    {
        if(this._color == BestSoldier.WHITE)
            return getWhiteSoldiersCount() - getBlackSoldiersCount();
        else
            return getBlackSoldiersCount() - getWhiteSoldiersCount();
    }
	
	public static HashMap<String, Node> NodesMap;
	static {
        NodesMap = new HashMap<String, Node>();
    }
	
    public String identity() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < this._gameBoard.length; ++i)
		{
			for(int j = 0; j < this._gameBoard[i].length; ++j)
			{
				sb.append(this._gameBoard[i][j]);
			}
		}
        return sb.toString();
    }

    public Node(byte[][] gameBoard, int color, int turn, int generationsCount) throws IllegalArgumentException
    {
		if(generationsCount == BestSoldier.MAX_GENERATIONS)
		{
			// System.out.println("Existe déjà");
			NodesMap.clear();
		}
	
        if(color != BestSoldier.WHITE && color != BestSoldier.BLACK)
            throw new IllegalArgumentException("Couleur inexistante");

        this._color = color;
        this._sons = new ArrayList<Node>();
        this._gameBoard = new byte[gameBoard.length][gameBoard.length];
                                
        for(int l = 0; l < gameBoard.length; ++l)
        {
            for(int m = 0; m < gameBoard.length; ++m)
            {
                this._gameBoard[l][m] = gameBoard[l][m];
            }
        }

        if(generationsCount > 0)
        {
			// System.out.println(this.hashCode());
		
			if(NodesMap.containsKey(this.identity()))
			{
				// System.out.println("Existe déjà");
				this._sons = NodesMap.get(this.identity())._sons;
				return;
			}
			else
			{
				NodesMap.put(this.identity(), this);
			}
		
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
                                byte[][] sonGameBoard = new byte[gameBoard.length][gameBoard.length];
                                for(int l = 0; l < sonGameBoard.length; ++l)
                                    for(int m = 0; m < sonGameBoard.length; ++m)
                                        sonGameBoard[l][m] = gameBoard[l][m];
                                
                                sonGameBoard[i + nextCol][j + nextLine] = sonGameBoard[i][j];
                                sonGameBoard[i][j] = 0;
                                _sons.add(new Node(sonGameBoard, _color, (turn == BestSoldier.WHITE ? BestSoldier.BLACK : BestSoldier.WHITE), generationsCount - 1));
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
                                    byte[][] sonGameBoard = new byte[gameBoard.length][gameBoard.length];
                                    
                                    for(int l = 0; l < sonGameBoard.length; ++l)
                                        for(int m = 0; m < sonGameBoard.length; ++m)
                                            sonGameBoard[l][m] = gameBoard[l][m];
                                    
                                    sonGameBoard[y][x] = sonGameBoard[i][j];
                                    sonGameBoard[i + nextCol][j + nextLine] = 0;
                                    sonGameBoard[i][j] = 0;
                                    _sons.add(new Node(sonGameBoard, _color, (turn == BestSoldier.WHITE ? BestSoldier.BLACK : BestSoldier.WHITE), generationsCount - 1));
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
            return node.getSoldiersCount();
        
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
            return node.getSoldiersCount();
        
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
        
        for(int i = 0; i < _gameBoard.length; ++i)
        {
            for(int j = 0; j < _gameBoard.length; ++j)
            {
                if(_gameBoard[i][j] != _bestSon._gameBoard[i][j]) // Différence entre l'ancien et le nouveau plateau de jeu
                {                    
                    if(_gameBoard[i][j] == _color) // L'ancien plateau contenait notre pion : ancienne case
                    {
                        oldPlace = (i+1)+" "+(j+1)+" ";
                    }
                    
                    else if(_bestSon._gameBoard[i][j] == _color) // Le nouveau plateau contient notre pion : nouvelle case
                    {
                        newPlace = (i+1)+" "+(j+1)+'\0';
                    }
                    
                    // else : un pion noir a disparu : osef
                }
            }
        }
        
        if(oldPlace == "" || newPlace == "") return "0 0 0 0"+'\0';
        
        return oldPlace + newPlace;
        // FORMAT COLONNE / LIGNE
    }
    
    public byte[][] BestSonGameBoard() throws IllegalArgumentException
    {
        if(_bestSon == null && _bestSon._gameBoard == null)
            throw new IllegalArgumentException("Plateau de jeu du prochain coup == null");
            
        return _bestSon._gameBoard;
    }
}