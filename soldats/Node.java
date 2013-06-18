package soldats;
import java.util.ArrayList;
import java.util.Arrays;

public class Node
{
    private static final int MAX_GENERATIONS = 5;

    private ArrayList<Node> _sons;
    // 0 : Vide, 1 : Blanc, 2 : Noir
    private int[][] _gameBoard;

    private Node _bestSon;

    private int _color;
    private int _turn = 0;

    private int _generationsCount;

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
                    if(_gameBoard[i][j] == BestSoldier.WHITE) ++_blackSoldiersCount;
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

    public Node(int[][] gameBoard, int color, int turn, int generationsCount) throws IllegalArgumentException
    {
        if(color != BestSoldier.WHITE && color != BestSoldier.BLACK)
            throw new IllegalArgumentException("Couleur inexistante");

        this._color = color;
        this._gameBoard = Arrays.copyOf(gameBoard, gameBoard.length);
        this._generationsCount = generationsCount;

        _sons = new ArrayList<Node>();

        if(generationsCount > 0)
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
                            int nextCol = BestSoldier.colMov[BestSoldier.movements[i][j][k]-1];
                            int nextLine = BestSoldier.rowMov[BestSoldier.movements[i][j][k]-1];
                            nextCol = (BestSoldier.movements[i + nextCol][j + nextLine].length == 1 ? BestSoldier.colMov[BestSoldier.movements[i][j][k]-1]*2 : nextCol);
                            nextLine = (BestSoldier.movements[i + nextCol][j + nextLine].length == 1 ? BestSoldier.rowMov[BestSoldier.movements[i][j][k]-1]*2 : nextLine);
                            if(gameBoard[i + nextCol][j + nextLine] ==  0)
                            {
                                // On créé un nouveau fils
                                int[][] sonGameBoard = Arrays.copyOf(this._gameBoard, this._gameBoard.length);
                                sonGameBoard[i + nextCol][j + nextLine] = sonGameBoard[i][j];
                                sonGameBoard[i][j] = 0;
                                _sons.add(new Node(sonGameBoard, _color, (turn == BestSoldier.WHITE ? BestSoldier.BLACK : BestSoldier.WHITE), generationsCount - 1));
                            }
                            // Ennemi
                            else if(gameBoard[i + nextCol][j + nextLine] !=  gameBoard[i][j])
                            {
                                // Vérifier qu'on peut manger l'ennemi
                                if(BestSoldier.movements[i + nextCol * 2][j + nextLine * 2].length == 1 && gameBoard[i + nextCol * 2][j + nextLine * 2] == 0)
                                {
                                    // On créé un nouveau fils
                                    int[][] sonGameBoard = Arrays.copyOf(this._gameBoard, this._gameBoard.length);
                                    sonGameBoard[i + nextCol * 2][j + nextLine * 2] = sonGameBoard[i][j];
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

        // Tous les fils ont été crées (leurs fils aussi) : lancer aB
        // ...


    }

    private int MaxValue(Node node, int alpha, int beta, int generation)
    {
        // aB : MaxValue
        if(node._sons.size() == 0)
            return getSoldiersCount();

        for(int i=0;i<node._sons.size();++i)
        {
            int newAlpha = MinValue(node._sons.get(i), alpha, beta, generation-1);
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

    private int MinValue(Node node, int alpha, int beta, int generation)
    {
        // aB : MaxValue
        if(node._sons.size() == 0)
            return getSoldiersCount();

        for(int i=0;i<node._sons.size();++i)
        {
            int newBeta = MaxValue(node._sons.get(i), alpha, beta, generation-1);
            beta = (beta < newBeta ? beta : newBeta);

            if(alpha >= beta)
                return alpha;
        }

        return beta;
    }
}