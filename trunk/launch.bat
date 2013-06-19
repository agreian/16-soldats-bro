cd soldats
start cmd /c java -cp obfsoldats.jar soldats.ServeurJeu 1234
start cmd /c java -cp obfsoldats.jar soldats.ClientJeu soldats.JoueurAleatoireProf localhost 1234
start cmd /c java -cp . soldats.ClientJeu soldats.BestSoldier localhost 1234