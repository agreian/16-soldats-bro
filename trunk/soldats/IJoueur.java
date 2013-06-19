package soldats;

public interface IJoueur {
	
	static final int TAILLE = 9;
	static final int BLANC = 1;  
	static final int NOIR = 2;  
	
	public void initJoueur(int mycolour);

	public int getNumJoueur();

	public String choixMouvement();	
	
	public void declareLeVainqueur(int colour);
	
	public void mouvementEnnemi(int startCol, int startRow, int finishCol, int finishRow);

	public String binoName();

	
}


