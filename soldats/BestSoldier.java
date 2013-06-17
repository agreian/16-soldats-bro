package soldats;

public class BestSoldier implements IJoueur
{
	static final int WHITE = 1;
	static final int BLACK = 2;
    static final int EMPTY = 0;
	static final int SIZE = 9; // Taille du plateau
    
    private int playerColor; // Couleur du joueur. 1 : Blanc, 2 = Noir
    
    // Tableau 3D d�crivant les mouvements possibles (format "colonne / ligne") � partir d'une case donn�e.
    private static final byte[][][] movements = {
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
    
    // Tableau 2D d�crivant l'�tat d'une cellule (0 : Empty, 1 : White, 2 : Black)
    private byte[][] state = new int[9][9];
    
    
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
        return "0 0 0 0" + '\0'; // ATTENTION : Format "colonne / ligne", coordonn�es allant de 1 � SIZE (9)
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
        state[startCol-1][startRow-1] = EMPTY; // Il n'y a plus de soldat (case vide) sur l'ancienne case
        state[finishCol-1][finishRow-1] = (color == WHITE) ? BLACK : WHITE;
    }

	/**
	 * @return Le nom de votre binome
	 */
	public String binoName()
    {
        return "Nom du binome";
    }
}