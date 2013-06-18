package soldats;

/**
 * Voici l'interface abstraite qu'il suffit d'implanter pour jouer.
 * Ensuite, vous devez utiliser ClientJeu en lui donnant le nom de votre classe
 * pour qu'il la charge et se connecte au serveur.
 * 
 * Vous pouvez utiliser le RandomPlayer pour tester.
 * 
 * @author L. Simon (Univ. Paris-Sud)- 2006-2013
 *
 */

public interface IJoueur 
{	
	static final int TAILLE = 9; // Taille du plateau. Pas la peine de la changer !
	static final int BLANC = 1;  // Mais pas lors de la conversation avec l'arbitre (methodes initJoueur et getNumJoueur)
	static final int NOIR = 2;   // Vous pouvez changer cela en interne si vous le souhaitez
	
	/**
	 * L'arbitre vient de lancer votre joueur. Il lui informe par cette methode
	 * que vous devez jouer dans cette couleur. Vous pouvez utiliser cette methode
	 * abstraite, ou la methode constructeur de votre classe, pour initialiser
	 * vos structures.
	 * @param mycolour La couleur dans laquelle vous allez jouer (1=BLANC, 2=NOIR)
	 */
	public void initJoueur(int mycolour);

	public int getNumJoueur(); // Doit retourner l'argument passÂe par la fonction ci-dessus (constantes BLANC ou NOIR)

	
	/**
	 * C'est ici que vous devez faire appel a votre IA pour trouver le meilleur coup a jouer
	 * sur le plateau courant.
	 * 
	 * @return une chaine decrivant le mouvement. Cette chaine doit etre decrite exactement comme sur l'exemple :
	 * String msg = "" + colonnePiece + " " + lignePiece + " " + colonneDestination + " " + LigneDestination + '\0';
	 * Attention, j'ai modifie l'ordre pour etre consistant avec l'enonce du devoir : colonne ligne colonne ligne
	 * System.out.println("Voici mon mouvement : " + msg);
	 */
	public String choixMouvement();	

	
	/**
	 * Methode appelee par l'arbitre pour designer le vainqueur. Vous pouvez en profiter pour
	 * imprimer une bannière de joie... Si vous gagnez... 
	 * 
	 * @param colour La couleur du gagnant (BLANC=1, NOIR=2).
	 */
	public void declareLeVainqueur(int colour);
	
	
	/**
	 * On suppose que l'arbitre a verifie que le mouvement ennemi etait bien legal. Il vous informe
	 * du mouvement ennemi. A vous de repercuter ce mouvement dans vos structures. Comme
	 * par exemple eliminer les pions que ennemi vient de vous prendre par ce mouvement.
	 * Il n'est pas necessaire de verifie deja a votre prochain coup a jouer : pour cela
	 * l'arbitre appelera ensuite choixMouvement().
	 * 
	 * @param startCol Colonne de depart du mouvement (entre 1 et TAILLE), 
	 *                 commençant a gauche=1 a droite=(TAILLE)
	 * @param startRow Ligne de depart du mouvement (entre 1 et TAILLE), 
	 *                 commençant en haut=1 a bas=(TAILLE)
	 * @param finishCol Colonne d'arrivee du mouvement (entre 1 et TAILLE),
	 * 	                commençant a gauche=1 a droite=(TAILLE)
	 * @param finishRow Ligne d'arrivee du mouvement (entre 1 et TAILLE),
	 *                  commençant en haut=1 a bas=(TAILLE)
	 */
	public void mouvementEnnemi(int startCol, int startRow, int finishCol, int finishRow);

	/**
	 * @return Le nom de votre binome
	 */
	public String binoName();	
}


