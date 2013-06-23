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
	
	// Le random permet de ne pas parcourir les fils constamment dans le même sens
	private static Random _rnd;
	static
	{
		_rnd = new Random();
	}
	
	// Revoie le nombre de pions blancs présents
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

	// Revoie le nombre de pions noirs présents
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

	// Fonction heuritisque, trop simple à améliorer
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
	
	// Constructeur du premier node et uniquement celui ci
    public Node(byte[][] gameBoard, int color, int turn, int generationsCount) throws IllegalArgumentException
    {
        if(color != BestSoldier.WHITE && color != BestSoldier.BLACK)
            throw new IllegalArgumentException("Couleur inexistante");
        if(turn != BestSoldier.WHITE && turn != BestSoldier.BLACK)
            throw new IllegalArgumentException("Couleur inexistante");
		
		// Initialisation de Alpha et Beta aux bonnes valeurs !
		this._alpha = Integer.MIN_VALUE; // équivalent à -infini
		this._beta = Integer.MAX_VALUE; // équivalent à +infini

        this._color = (byte)color;
		
		// On recopie UNE SEULE FOIS le plateau de jeu, afin de ne pas mofidier celui présent dans BestSoldier
		// On ne recopie rien d'autre par la suite
        byte[][] copyGameBoard = new byte[gameBoard.length][gameBoard.length];             
        for(int l = 0; l < gameBoard.length; ++l)
        {
            for(int m = 0; m < gameBoard.length; ++m)
            {
                copyGameBoard[l][m] = gameBoard[l][m];
            }
        }
		
		this.AlphaBeta(copyGameBoard, turn, generationsCount); // exécution d'alpha beta avec la copie du plateau de jeu initial
	}
	
	// Constructeur de tous les autres noeuds (tous sauf le premier [racine])
	private Node(byte[][] gameBoard, int color, int turn, int generationsCount, byte[] startPoint, byte[] finalPoint, byte[] ennemyPoint, int alpha, int beta)
	{
		this._color = (byte)color;
		// StartPoint permet de connaitre les coordonnées du pion qui a bougé entre le noeud pere et le noeud fils
		this._startPoint = startPoint;
		// FinalPoint permet de connaitre les coordonnées de destination du pion qui a bougé entre le noeud pere et le noeud fils
		this._finalPoint = finalPoint;
		// EnnemyPoint permet de connaitre les coordonnées du pion qui a été éliminé entre le noeud pere et le noeud fils, est non null si un pion a été mangé
		if(ennemyPoint != null)
			this._ennemyPoint = ennemyPoint; // un pion ennemi a été mangé, on stock ses coordonnées
		
		this._alpha = alpha; // alpha calculé par la fonction AlphaBeta
		this._beta = beta; // beta calculé par la fonction AlphaBeta
		
		// On bouge les pions nécessaires
		gameBoard[finalPoint[0]][finalPoint[1]] = gameBoard[startPoint[0]][startPoint[1]]; // le point de destination prend la couleur du point de départ
		gameBoard[startPoint[0]][startPoint[1]] = BestSoldier.EMPTY; // le point de départ est maintenant vide
		if(ennemyPoint != null) gameBoard[ennemyPoint[0]][ennemyPoint[1]] = BestSoldier.EMPTY; // Pion mangé => case vide
		
		// Exécution d'alphaBeta avec cette nouvelle configuration (déplacement effectué)
		this.AlphaBeta(gameBoard, turn, generationsCount);
		
		// Après les traitements on revient à l'état initial (pour le noeud père)
		gameBoard[startPoint[0]][startPoint[1]] = gameBoard[finalPoint[0]][finalPoint[1]];
		gameBoard[finalPoint[0]][finalPoint[1]] = BestSoldier.EMPTY;
		if(ennemyPoint != null) gameBoard[ennemyPoint[0]][ennemyPoint[1]] = (turn == BestSoldier.WHITE ? BestSoldier.WHITE : BestSoldier.BLACK);
	}
	
	private void AlphaBeta(byte[][] gameBoard, int turn, int generationsCount)
	{
		if(generationsCount == 0)
		{
			// Si le noeud est une feuille on renvoi l'heuristique tout simplement
			this._return = getHeuristique(gameBoard);
			return;
		}

		else if(generationsCount > 0) // Sinon on descend dans chaque fils pour atteindre une feuille et ainsi obtenir son heuristique
        {
			// Permet de parcourir différemment la grille pour trouver les fils (ceux ci représentant chacun un déplacement possible)
			// Les trouver différemment permet de parcourir l'arbre différemment entre chaque tour (et ainsi minimiser les chances de répéter infiniment deux coups menant au DEUCE)
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
					System.out.println("RANDOM ERROR");
					break;
				}
			}
			
			// Fin des fonction MaxValue et MinValue. Selon le tour (coup ami ou ennemi), met à jour alpha et beta
			if(turn == _color)
			{
				// MaxValue
				
				// this._return contient la valeur retournée par la fonction récursive
				this._return = _alpha;
				return;
			}
            
			else
			{
				// MinValue
				
				// this._return contient la valeur retournée par la fonction récursive
				this._return = _beta;
				return;
			}
        }
	}
	
	// AlphaBeta, appelé pour chaque grille du plateau de jeu, afin de le parcourir différemment
	private boolean AlphaBeta(int i, int j, byte[][] gameBoard, int turn, int generationsCount)
	{
		// Si le pion est de la couleur qui doit jouer et que des mouvements sont possibles sur la case designée
		if((gameBoard[i][j] == turn) && (BestSoldier.movements[i][j].length > 1))
		{                        
			// Pour chaque mouvement possible depuis notre case
			for(int k = 0; k < BestSoldier.movements[i][j].length; ++k)
			{
				int nextCol = BestSoldier.colMov[BestSoldier.movements[i][j][k]-1];
				int nextLine = BestSoldier.rowMov[BestSoldier.movements[i][j][k]-1];
				nextCol = (BestSoldier.movements[i + nextCol][j + nextLine].length == 1 ? BestSoldier.colMov[BestSoldier.movements[i][j][k]-1]*2 : nextCol);
				nextLine = (BestSoldier.movements[i + nextCol][j + nextLine].length == 1 ? BestSoldier.rowMov[BestSoldier.movements[i][j][k]-1]*2 : nextLine);
				
				byte[] startPoint = null;
				byte[] finalPoint = null;
				byte[] ennemyPoint = null;
				
				// La case d'arrivée est vide
				if(gameBoard[i + nextCol][j + nextLine] ==  0)
				{
					// On met les coordonnées du déplacement
					startPoint = new byte[] { (byte)i , (byte)j };
					finalPoint = new byte[] { (byte)(i + nextCol) , (byte)(j + nextLine) };
				}
                
                // La case contient un soldat ennemi
				else if(gameBoard[i + nextCol][j + nextLine] != gameBoard[i][j])
				{          
					// Ici il faut vérifier que l'on peut manger le soldat de l'ennemi :
					//	- La case derrière son soldat est vide ET c'est une case où des mouvements sont possibles
					// 	- On peut se rendre directement dans cette case (en sautant le soldat ennemi)
					int y = i + nextCol * 2, x = j + nextLine * 2;
					
					// Vérifie la première condition et prévient des effets de bord
					if((x >= 0 && x < gameBoard.length) && (y >= 0 && y < gameBoard.length) && BestSoldier.movements[y][x].length != 1 && gameBoard[y][x] == 0)
					{
						// Vérifie la deuxième condition
						boolean possibleMovement = false;
						for(int l = 0; l < BestSoldier.movements[i + nextCol][j + nextLine].length; ++l)
							if(BestSoldier.movements[i + nextCol][j + nextLine][l] == BestSoldier.movements[i][j][k])
								possibleMovement = true;
								
						if(possibleMovement == false)
							continue; // Déplacement impossible, deuxième condition non respectée : prochaine itération de k
						
						// Affecte les coordonnées du déplacement, et celles du soldat mangé
						startPoint = new byte[] { (byte)i , (byte)j };
						finalPoint = new byte[] {(byte)y , (byte)x };
						ennemyPoint = new byte[] { (byte)(i + nextCol) , (byte)(j + nextLine) };
					}
                    
					else
					{
						// Déplacement impossible 1ères conditions non respectées
						continue; // Prochaine itération sur k
					}
				}
                
				else
				{
					// Cas où c'est un ami dans la case de destination : pas d'intérêt
					continue;
				}
				
				// Crée le fils trouvé, qui va lui même créer ses fils, qui vont créer leur fils, etc...
				Node son = new Node(gameBoard, 
					_color, 
					(turn == BestSoldier.WHITE ? BestSoldier.BLACK : BestSoldier.WHITE), 
					generationsCount - 1, 
					startPoint, finalPoint, ennemyPoint, 
					_alpha, _beta);
				
				// Permet de switcher entre MaxValue et MinValue les 2 fonctions de AlphaBeta
				// MaxValue est à éxecuter quand on doit jouer
				// MinValue est à éxecuter quand l'ennemi doit jouer
				if(turn == _color)
				{
					// MaxValue (algorithme classique), _return contient la valeur de retour
					// On chercher à maximiser alpha
					int newAlpha = son._return;
                    
					if(_alpha < newAlpha)
					{
						_alpha = newAlpha;
						
						// Si on est dans la fonction du 1er noeud, celui-ci enregistre son fils (dans _bestSon), qui lui promet le meilleur résultat afin d'effectuer son mouvement
						if(generationsCount == BestSoldier.MAX_GENERATIONS)
						{
							this._bestSon = son;
						}
					}

					// élagage : on ne génère pas les prochains fils de cette génération car on considère que l'on a déjà trouvé la meilleure heuristique
					if(_alpha >= _beta)
					{
						this._return = _beta;
						return true;
					}
				}
                
				else
				{
					// MinValue (algorithme classique), _return contient la valeur de retour
					// On cherche à minimiser beta
					int newBeta = son._return;
					_beta = (_beta < newBeta ? _beta : newBeta);

					// élagage : on ne génère pas les prochains fils de cette génération car on considère que l'on a déjà trouvé la meilleure heuristique
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
	// Renvoie les coordonnées du prochain déplacement
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
    
	// Met à jour la game board fournie en parametre avec le déplacement à effectuer
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