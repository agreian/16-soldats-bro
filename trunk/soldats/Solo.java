package soldats;

import java.util.*;
import java.io.*;

import javax.swing.JFrame;

 /**
 * Lance une partie entre deux IJoueur.
 * Il n'y a pas d'arbitre et cela n'est pas nécessaire car les IJoueur jouent forcément des movements valides.
 *
 * Rien ne vérifie la fin de la partie. Les IJoueur doivent ainsi envoyer la chaine "x x x x" pour signaler la fin d'une partie.
 * (Différent de "0 0 0 0" qui signifie que l'on passe un movement).
 *
 * La classe n'affiche rien, elle passe la main d'un joueur à l'autre.
 */
public class Solo 
{
	private static IJoueur playerW;
	private static IJoueur playerB;	

	// Ne pas modifier ces constantes, elles seront utilisees par l'arbitre
	private final static int WHITE = 1;
	private final static int BLACK = 2;

	private static int TAILLE = 5; 
	
	// applet game viewer
	static boolean DISPLAY_APPLET = true; // Vous pouvez changer la constante

	public void setDisplayApplet(boolean t) 
    {
		DISPLAY_APPLET = t;
	}

	
	/**
     * Retourne un joueur instancié d'une classe perso. Celle-ci doit implanter IJoueur
	 * @param s
	 * @return Ijoueur un joueur demande
	 */
	private static IJoueur getDefaultPlayer(String s) 
    {
		System.out.println(s + " : defaultPlayer");
		return new JoueurAleatoireProf();
		// TODO: Ajouter votre joueur ici
		//return null; // vous devez faire qq chose comme return new MonMeilleurJoueur();		
	}

	/**
	 * Charge une classe Joueur (implantant IJoueur) donnée 
	 * @param classeJoueur
	 * @param s
	 * @return la classe chargee dynamiquement
	 */
	private static IJoueur loadNamedPlayer(String classeJoueur, String s)  
    {
		IJoueur joueur;
		System.out.print(s + " : Chargement de la classe joueur " + classeJoueur + "... ");
		try 
        {
			Class cjoueur = Class.forName(classeJoueur);
			joueur = (IJoueur)cjoueur.newInstance();
		} catch (Exception e) 
        {
			System.out.println("Erreur de chargement");
			System.out.println(e);
			return null;
		}
        
		System.out.println("Ok");
		return joueur;
	}
	
	/**
	 * Boucle principale du jeu, en utilisant une version de l'arbitre identique a celle du tournoi
	 * L'arbitre sera le garant de la validite des movements, et de leur affichage standard
	 * pour la publication via le site web.
	 * 
	 * @param playerW
	 * @param playerB
	 */
	public static void gameLoop(IJoueur playerW, IJoueur playerB) 
    {
		String movement;
		int x1,y1,x2,y2;
		boolean gameOver = false;
        IJoueur currentPlayer = playerW; // Joueurs blancs qui commencent

        while (!gameOver) 
        {
        	System.out.println("On demande ˆ "  + currentPlayer.binoName() + " de jouer...");
        	long waitingTime1 = new Date().getTime();
        	
        	movement = currentPlayer.choixMouvement();

        	long waitingTime2 = new Date().getTime();
			long waitingTime = waitingTime2-waitingTime1 + 1; // On ajoute 1 pour eliminer les temps infinis
			System.out.println("Le joueur " + currentPlayer.binoName() + " a joué le movement " + movement + " en " + waitingTime + "s.");
            
			try 
            {
				Thread.sleep(1); // Attente entre deux movements
			} catch (InterruptedException e) {}
			
		   if(movement.compareTo("x x x x") == 0) 
			   gameOver = true;
               
		   else 
           {
                // Initialisation du prochain joueur courant (l'ennemi)
                currentPlayer = (currentPlayer.getNumJoueur() == WHITE) ? playerB : playerW;
			   
				StringTokenizer st = new StringTokenizer(movement, " \n\0");
				x1 = Integer.parseInt(st.nextToken());
				y1 = Integer.parseInt(st.nextToken());
				x2 = Integer.parseInt(st.nextToken());
				y2 = Integer.parseInt(st.nextToken());

			   // Averti l'ennemi (le nouveau joueur courant) du movement joué précedemment
			   currentPlayer.mouvementEnnemi(x1,y1,x2,y2);
		   }
        }
        
        // TODO: Prendre en compte les égalités
        currentPlayer = (currentPlayer.getNumJoueur() == WHITE) ? playerB : playerW;
        playerW.declareLeVainqueur(currentPlayer.getNumJoueur());
        playerB.declareLeVainqueur(currentPlayer.getNumJoueur());        
	}
	
	/**
	 * On charge eventuellement les classes demandee pour les joueurs, et on lance la boucle principale
	 * @param args
	 */
	public static void main(String args[]) {

		System.out.println("Partie solo de 16 Soldats...");
		
		if (args.length == 0) { // On a deux classes a charger
			playerW = getDefaultPlayer("BLANC");
			playerB = getDefaultPlayer("NOIR");
		} else if (args.length == 2) { // On a deux classes a charger
			playerW = getDefaultPlayer("BLANC");
			playerB = getDefaultPlayer("NOIR");
		} else if (args.length == 3) {
			playerW = loadNamedPlayer(args[0],"BLANC");
			playerB = loadNamedPlayer(args[0],"NOIR");
		} else if (args.length == 4) {
			playerW = loadNamedPlayer(args[0],"BLANC");
			playerB = loadNamedPlayer(args[1],"NOIR");			
		}
				
		playerW.initJoueur(WHITE);
		System.out.println("Joueur BLANC : " + playerW.binoName());

		playerB.initJoueur(BLACK);
		System.out.println("Joueur NOIR : " + playerB.binoName());
		
		System.out.println("Initialisation des deux joueurs ok");

		gameLoop(playerW, playerB);
		
	}
}
