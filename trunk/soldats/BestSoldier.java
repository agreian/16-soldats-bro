package soldats;

public class BestSoldier implements IJoueur
{
	public static final int WHITE = 1;
	public static final int BLACK = 2;
    public static final int EMPTY = 0;
	public static final int SIZE = 9; // Taille du plateau
    public static final int MAX_GENERATIONS = 3;
    
    private int playerColor; // Couleur du joueur. 1 : Blanc, 2 = Noir
    private int[][] state; // Tableau 2D d�crivant l'�tat d'une cellule. Initialis� dans le ctor()
    
    // Tableau 3D d�crivant les mouvements possibles (format "colonne / ligne") � partir d'une case donn�e.
    public static final int[][][] movements = {
                            { {0},{0},{8,3},{0},{1,3,4},{0},{1,5},{0},{0} },

                            { {0},{0},{0},{3,8,6},{1,2,3,4},{1,5,7},{0},{0},{0} },

                            { {8,4},{0},{3,8,4},{1,3,4},{1,2,3,4,5,6,7,8},{1,4,3},{1,5,4},{0},{5,4} },

                            { {0},{4,8,6},{2,4,3},{1,2,3,4,5,6,7,8},{1,2,3,4},{1,2,3,4,5,6,7,8},{1,2,4},{7,5,4},{0} },

                            { {2,4,3},{1,2,3,4},{1,2,3,4,5,6,7,8},{1,2,3,4},{1,2,3,4,5,6,7,8},{1,2,3,4},{1,2,3,4,5,6,7,8},{1,2,3,4},{2,4,1} },

                            { {0},{2,7,5},{2,4,3},{1,2,3,4,5,6,7,8},{1,2,3,4},{1,2,3,4,5,6,7,8},{1,2,4},{2,6,8},{0} },

                            { {2,7},{0},{3,7,2},{1,3,2},{1,2,3,4,5,6,7,8},{1,2,3},{1,6,2},{0},{6,2} },

                            { {0},{0},{0},{3,7,5},{1,2,3,4},{1,6,8},{0},{0},{0} },

                            { {0},{0},{7,3},{0},{1,3,2},{0},{1,6},{0},{0} }
                        };

    // Attention : L'index d�marre � 0. colMov[0] correspond au d�placement en colonne du mouvement "1"
	public static int[] colMov = { 0, -1, 0, 1, +1, -1, -1, +1}; // D�placement sur le plateau de jeu en colonne en fonction des mouvements 1, 2, 3, ..., 8
	public static int[] rowMov = { -1, 0, 1, 0, -1, -1, +1, +1}; // D�placement sur le plateau de jeu en ligne en fonction des mouvements 1, 2, 3, ..., 8

    // ctor()
    public BestSoldier()
    {        
        state = new int[9][9]; // Initialisation d'un tableau de 9*9 rempli de 0 (= EMPTY)
        
        // Pions blancs
        for(int row = 2; row < 4; ++row)
        {
            for(int col = 2; col < 7; ++col)
            {
                state[col][row] = WHITE;
            }
        }
        
        state[2][0] = WHITE;
        state[4][0] = WHITE;
        state[6][0] = WHITE;
        
        state[3][1] = WHITE;
        state[4][1] = WHITE;
        state[5][1] = WHITE;
        
        // Pions noirs
        for(int row = 5; row < 7; ++row)
        {
            for(int col = 2; col < 7; ++col)
            {
                state[col][row] = BLACK;
            }
        }
        
        state[2][8] = BLACK;
        state[4][8] = BLACK;
        state[6][8] = BLACK;
        
        state[3][7] = BLACK;
        state[4][7] = BLACK;
        state[5][7] = BLACK;
    }
    
    /**
	 * M�thode appel�e par l'arbitre pour attribuer une couleur � l'instance Joueur
	 * @param mycolour La couleur dans laquelle vous allez jouer (1=BLANC, 2=NOIR)
	 */
	public void initJoueur(int mycolour)
    {
        // On admet que la m�thode est fiable et ne v�rifie pas que mycolour soit tjrs �gal � 1 ou 2
        playerColor = mycolour;
    }

	public int getNumJoueur()
    {
        return playerColor;
    }

	
	/**
	 * Utilisation de l'IA pour trouver le meilleur coup � jouer
	 * 
	 * @return une chaine d�crivant le mouvement. La chaine doit respecter la syntaxe suivante :
	 * String msg = "" + colonnePiece + " " + lignePiece + " " + colonneDestination + " " + LigneDestination + '\0';
     * ATTENTION : Format "colonne / ligne"
	 */
	public String choixMouvement()
    {
        Node root = new Node(state, playerColor, playerColor, MAX_GENERATIONS);
        root.Evaluation();
        state = root.BestSonGameBoard();        
        return root.BestMovement();
    }

	
	/**
	 * Appel�e par l'arbitre pour d�clarer le vainqueur
	 * 
	 * @param colour La couleur du gagnant (BLANC=1, NOIR=2).
	 */
	public void declareLeVainqueur(int colour)
    {
        if(playerColor == colour)
            System.out.println("J'ai gagn�");
        
        else
            System.out.println("J'ai perdu");            
    }
	
	
	/**
	 * On suppose que l'arbitre a v�rifi� la l�galit� du mouvement adverse. Il en informe notre joueur.
	 * Mettre � jour les structures (�tat de chaque case du terrain).
     * Attention : On ne r�fl�chit pas au prochain coup.
     *
	 * @param startCol : Colonne de d�part (entre 1 (gauche) et TAILLE (droite))
	 * @param startRow : Ligne de d�part (entre 1 (haut) et TAILLE (bas))
	 * @param finishCol : Colonne d'arriv�e (entre 1 (gauche) et TAILLE (droite))
	 * @param finishRow : Ligne d'arriv�e (entre 1 (haut) et TAILLE (bas))
	 */
	public void mouvementEnnemi(int startCol, int startRow, int finishCol, int finishRow)
    {
        --startCol; // pour travailler avec des index de 0 � (TAILLE - 1)
        --startRow;
        --finishCol;
        --finishRow;
        
        state[startCol][startRow] = EMPTY; // Il n'y a plus de soldat (case vide) sur l'ancienne case
        state[finishCol][finishRow] = (playerColor == WHITE) ? BLACK : WHITE;
        
        // Saut sur les bords : saut de 4 case en tout (1 non-walkable, 1 walkable, 1 non-walkable, 1 de destination) 
        if(Math.abs(finishCol - startCol) == 4 || Math.abs(finishRow - startRow) == 4)
        {
            // on sait qu'on a FORCEMENT mang� un pion, en ayant saut� de 4 cases
            state[(startCol+finishCol)/2][(startRow+finishRow)/2] = EMPTY;
        }
        
        // Saut de 2. On a soit boug� sur les bords, soit saut� un soldat
        else if(Math.abs(finishCol - startCol) == 2 || Math.abs(finishRow - startRow) == 2)
        {
            if(movements[(startCol+finishCol)/2][(startRow+finishRow)/2].length > 1) // case walkable : pi�ce mang�e
            {
                state[(startCol+finishCol)/2][(startRow+finishRow)/2] = EMPTY;
                // System.out.println("tmort : "+((startCol+finishCol)/2)+", "+((startRow+finishRow)/2));
            }
            
            //else { case non-walkable : simple d�placement }
        }
    }

	/**
	 * @return Le nom de votre binome
	 */
	public String binoName()
    {
        return "Nom du binome";
    }
}