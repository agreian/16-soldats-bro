package soldats;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Cette classe permet de charger dynamiquement une classe de joueur,
 * qui doit obligatoirement implanter l'interface IJoueur. Vous lui
 * donnez aussi en argument le nom de la machine distante (ou "localhost")
 * sur laquelle le serveur de jeu est lancé, ainsi que le port sur lequel
 * la machine écoute.
 *
 * Exemple: >java -cp . soldats/ClientJeu soldats.joueurProf localhost 1234
 * 
 * Le client s'occupe alors de tout en lançant les méthodes implantées de l'interface
 * IJoueur. Toute la gestion réseau est donc cachée.
 * 
 * @author L. Simon (Univ. Paris-Sud)- 2006-2013
 * @see IJoueur
 */
public class ClientJeu 
{	
	static final int BLACK = 2; // Vous pouvez changer cela en interne si vous le souhaitez
	static final int WHITE = 1; // Mais pas lors de la conversation avec l'arbitre
	static final int EMPTY = 0;
	
	/**
	 * @param args Dans l'ordre : NomClasseJoueur MachineServeur PortEcoute
	 */
	public static void main(String[] args) 
    {				
		if (args.length < 3) 
        {
			System.err.println("ClientJeu Usage: NomClasseJoueur MachineServeur PortEcoute");
			System.exit(1);
		}
		
		String classeJoueur = args[0];
		String serverMachine = args[1];
		int portNum = Integer.parseInt(args[2]);
		
		System.out.println("Le client se connectera sur " + serverMachine + ":" + portNum);
		
		Socket clientSocket = null;
		IJoueur joueur; 
		String msg, firstToken;
		StringTokenizer msgTokenizer; // analyser les chaines de caractères lues
		int playingColor; // couleur qui doit jouer le prochain coup
		int maCouleur; // ma couleur (quand je joue)
		
		boolean gameOver = false;
		int startLine, startColumn, endLine, endColumn; // Un mouvement
        
		try 
        {
			clientSocket = new Socket(serverMachine, portNum);
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			//*****************************************************
			System.out.print("Chargement de la classe joueur " + classeJoueur + "... ");
			Class cjoueur = Class.forName(classeJoueur);
			joueur = (IJoueur)cjoueur.newInstance();
			System.out.println("Ok");
			// ****************************************************
			
			// Envoie de l'identifiant de votre binome
			out.println(joueur.binoName());
			System.out.println("Mon nom de binome est " + joueur.binoName());
			
			// Récupère le message sous forme de chaine de caractères
			msg = in.readLine();
			System.out.println(msg);
			
			// Lit le contenu du message, toutes les infos du message
			msgTokenizer = new StringTokenizer(msg, " \n\0");
            
			if ((msgTokenizer.nextToken()).equals("BLANC")) 
            {
				System.out.println("Je suis BLANC, je commence.");
				maCouleur = WHITE;
			} 
            
            else 
            {
				System.out.println("Je suis NOIR, c'est ennemi qui commence.");
				maCouleur = BLACK;
			}

			// permet d'initialiser votre joueur avec sa couleur
			joueur.initJoueur(maCouleur);
			
			do // boucle principale
            {
				msg = in.readLine(); // Lit le message du serveur
				System.out.println(msg);
                
				msgTokenizer = new StringTokenizer(msg, " \n\0");
				firstToken = msgTokenizer.nextToken();
                
				if (firstToken.equals("FIN!")) 
                {
					gameOver = true;
					String theWinnerIs = msgTokenizer.nextToken();
                    
                    playingColor = (theWinnerIs.equals("BLANC")) ? WHITE : (theWinnerIs.equals("NOIR")) ? BLACK : EMPTY;
					
					if(playingColor == maCouleur)
						System.out.println("J'ai gagné");
					
					joueur.declareLeVainqueur(playingColor);
				}
                
				else if (firstToken.equals("JOUEUR")) 
                {
                    playingColor = ((msgTokenizer.nextToken()).equals("BLANC")) ? WHITE : BLACK;
                    
					if (playingColor == maCouleur) 
                    {
						// On appelle la classe du joueur pour choisir un mouvement
						msg = joueur.choixMouvement();
						out.println(msg);
					}
                    
				} 
                
                else if (firstToken.equals("MOUVEMENT")) 
                {
                    // Lit le mouvement du joueur et informe l'ennemi
					startColumn = Integer.parseInt(msgTokenizer.nextToken());
					startLine = Integer.parseInt(msgTokenizer.nextToken());
					endColumn = Integer.parseInt(msgTokenizer.nextToken());
					endLine = Integer.parseInt(msgTokenizer.nextToken());
					joueur.mouvementEnnemi( startColumn, startLine, endColumn, endLine);
				}
			} while(!gameOver);
		}
        
		catch (Exception e) 
        {
			System.out.println(e);
		}
	}
}
