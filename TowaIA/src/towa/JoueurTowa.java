package towa;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Joueur implémentant les actions possibles à partir d'un plateau, pour un
 * niveau donné.
 */
public class JoueurTowa implements IJoueurTowa {

    /**
     * Cette méthode renvoie, pour un plateau donné et un joueur donné, toutes
     * les actions possibles pour ce joueur.
     *
     * @param plateau le plateau considéré
     * @param couleurJoueur couleur du joueur
     * @param niveau le niveau de la partie à jouer
     * @return l'ensemble des actions possibles
     */
    @Override
    public String[] actionsPossibles(Case[][] plateau, char couleurJoueur, int niveau) {
        // afficher l'heure de lancement
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        System.out.println("actionsPossibles : lancement le " + format.format(new Date()));
        // se préparer à stocker les actions possibles
        ActionsPossibles actions = new ActionsPossibles();
        // on compte le nombre de pions sur le plateau avant action
        NbPions nbPions = nbPions(plateau);
        // pour chaque ligne
        for (int lig = 0; lig < Coordonnees.NB_LIGNES; lig++) {
            // pour chaque colonne
            for (int col = 0; col < Coordonnees.NB_COLONNES; col++) {
                Coordonnees coord = new Coordonnees(lig, col);
                Case[] caseActivationEnnemi = caseVoisinActivation(plateau, coord, inverseCouleurJoueur(couleurJoueur));
                Case[] caseActivationAmi = caseVoisinActivation(plateau, coord,couleurJoueur );

                // si la pose d'un pion de cette couleur est possible sur cette case
                if (posePossible(plateau, coord, couleurJoueur)) {
                    // on ajoute l'action dans les actions possibles
                    int nombreVoisinsAdversaires = nombreVoisinsAdversaires(plateau, coord, couleurJoueur);
                    boolean caseVide = plateau[coord.ligne][coord.colonne].couleur == Case.CAR_VIDE;
                    ajoutActionPose(coord, actions, nbPions, nombreVoisinsAdversaires, couleurJoueur, caseVide);
                }

                if (activationPossible(plateau, coord, couleurJoueur)) {
                    int nombreToursAdversesInferieurs = nombreToursAdverseVoisinesInferieurs(plateau, caseActivationEnnemi, coord);
                    ajoutActionActivation(coord, actions, nbPions, couleurJoueur, nombreToursAdversesInferieurs);
                }

                if (fusionPossible(plateau, coord, couleurJoueur)) {
                    int nombreTourAmiVoisin = nombreTourAmiVoisin(caseActivationAmi);
                    ajoutActionFusion(plateau, coord, actions, nbPions, nombreTourAmiVoisin, couleurJoueur);

                }
            }
        }
        System.out.println("actionsPossibles : fin");
        return actions.nettoyer();
    }

    /**
     * Indique s'il est possible de poser un pion sur une case pour ce plateau,
     * ce joueur, dans ce niveau.
     *
     * @param plateau le plateau
     * @param coord coordonnées de la case à considérer
     * @param couleur couleur du joueur
     * @return vrai ssi la pose d'un pion sur cette case est autorisée dans ce
     * niveau
     */
    boolean posePossible(Case[][] plateau, Coordonnees coord, char couleur) {
        Case caseActuelle = plateau[coord.ligne][coord.colonne];
        return !caseActuelle.tourPresente() || caseActuelle.couleur == couleur && caseActuelle.hauteur < 4;
    }

    boolean activationPossible(Case[][] plateau, Coordonnees coord, char couleur) {
        Case caseActuelle = plateau[coord.ligne][coord.colonne];
        return caseActuelle.couleur == couleur;
    }

    boolean fusionPossible(Case[][] plateau, Coordonnees coord, char couleur) {
        return plateau[coord.ligne][coord.colonne].couleur == couleur;
    }

    /**
     * Nombre de pions sur le plateau, de chaque couleur.
     *
     * @param plateau le plateau
     * @return le nombre de pions sur le plateau, de chaque couleur
     */
    static NbPions nbPions(Case[][] plateau) {
        int nbNoir = 0;
        int nbBlanc = 0;
        for (int lig = 0; lig < Coordonnees.NB_LIGNES; lig++) {
            for (int col = 0; col < Coordonnees.NB_COLONNES; col++) {
                if (plateau[lig][col].couleur == 'B') {
                    nbBlanc += plateau[lig][col].hauteur;
                } else if (plateau[lig][col].couleur == 'N') {
                    nbNoir += plateau[lig][col].hauteur;
                }
            }
        }
        return new NbPions(nbNoir, nbBlanc);
    }

    /**
     * Ajout d'une action de pose dans l'ensemble des actions possibles.
     *
     * @param coord coordonnées de la case où poser un pion
     * @param actions l'ensemble des actions possibles (en construction)
     * @param nbPions le nombre de pions par couleur sur le plateau avant de
     * jouer l'action
     * @param couleur la couleur du pion à ajouter
     */
    void ajoutActionPose(Coordonnees coord, ActionsPossibles actions, NbPions nbPions, int nombreVoisinsAdversaires, char couleur, boolean CaseVide) {
        int nbNoir = nbPions.nbPionsNoirs;
        int nbBlanc = nbPions.nbPionsBlancs;
        int increment = 1;
        if (CaseVide && nombreVoisinsAdversaires > 0) {
            increment = 2;
        }
        if (couleur == 'N') {
            nbNoir += increment;
        } else if (couleur == 'B') {
            nbBlanc += increment;
        }
        String action = "P" + coord.carLigne() + coord.carColonne() + ","
                + (nbNoir) + ","
                + (nbBlanc);
        actions.ajouterAction(action);
    }

    void ajoutActionActivation(Coordonnees coord, ActionsPossibles actions, NbPions nbPions, char couleur, int nombreToursAdversesInferieurs) {
        int nbNoir = nbPions.nbPionsNoirs;
        int nbBlanc = nbPions.nbPionsBlancs;
        if (couleur == 'N') {
            nbBlanc -= nombreToursAdversesInferieurs;
        } else if (couleur == 'B') {
            nbNoir -= nombreToursAdversesInferieurs;
        }
        String action = "A" + coord.carLigne() + coord.carColonne() + "," + nbNoir + "," + nbBlanc;
        actions.ajouterAction(action);
    }

    void ajoutActionFusion(Case[][] plateau, Coordonnees coord, ActionsPossibles actions, NbPions nbPions, int nombreTourAmiVoisin, char couleur) {
        int nombrePionCase = plateau[coord.ligne][coord.colonne].hauteur;
        int nombrePionTotalSurTour = nombrePionCase + nombreTourAmiVoisin;
        int nombrePionFusionnes = Math.min(nombrePionTotalSurTour, 4); // Limite de 4 pions après la fusion
        int nbPionsNoir = nbPions.nbPionsNoirs;
        int nbPionsBlanc = nbPions.nbPionsBlancs;

        // Calcul du nombre de pions perdus durant la fusion
        int nombrePionsPerdus = nombrePionTotalSurTour - nombrePionFusionnes;

        // Mise à jour du nombre total de pions pour la couleur spécifiée
        if (couleur == Case.CAR_NOIR) {
             nbPionsNoir -= nombrePionsPerdus;
        } else if (couleur == Case.CAR_BLANC) {
            nbPionsBlanc -= nombrePionsPerdus;
        }
        String action = "F" + coord.carLigne() + coord.carColonne() + "," + nbPionsNoir + "," + nbPionsBlanc;
        actions.ajouterAction(action);
        

    }

    static char inverseCouleurJoueur(char couleurJoueur) {
        if (couleurJoueur == Case.CAR_BLANC) {
            return Case.CAR_NOIR;
        } else if (couleurJoueur == Case.CAR_NOIR) {
            return Case.CAR_BLANC;
        }
        return ' ';
    }

    public static boolean estDansPlateau(Coordonnees coord) {
        return coord.ligne < Coordonnees.NB_LIGNES && coord.colonne < Coordonnees.NB_COLONNES
                && coord.ligne >= 0 && coord.colonne >= 0;
    }

    public static int nombreToursAdverseVoisinesInferieurs(Case[][] plateau, Case[] caseVoisinActivation, Coordonnees coord) {
        int nbTourAdverse = 0;
        for (Case c : caseVoisinActivation) {
            if (plateau[coord.ligne][coord.colonne].hauteur > c.hauteur) {
                nbTourAdverse += c.hauteur;
            }
        }

        return nbTourAdverse;
    }

    public static int nombreTourAmiVoisin(Case[] caseVoisinAmi) {
        int totalPionAmi = 0;
        for (Case c : caseVoisinAmi) {
            totalPionAmi += c.hauteur;
        }
        return totalPionAmi;
    }

    public static int nombreTourApresFusion(Case[][] plateau, Coordonnees coord, char couleur, int nombreTourAmiVoisin, NbPions nbPions) {
        int nombrePionCase = plateau[coord.ligne][coord.colonne].hauteur;
        int nombrePionTotal = nombrePionCase + nombreTourAmiVoisin;
        int nombrePionFusionnes = Math.min(nombrePionTotal, 4); // Limite de 4 pions après la fusion

        // Calcul du nombre de pions perdus
        int nombrePionsPerdus = nombrePionTotal - nombrePionFusionnes;

        // Mise à jour du nombre de pions total
        if (couleur == Case.CAR_NOIR) {
            nbPions.nbPionsNoirs -= nombrePionsPerdus;
        } else if (couleur == Case.CAR_BLANC) {
            nbPions.nbPionsBlancs -= nombrePionsPerdus;
        }
        // Retourner le nombre de pions sur la case après la fusion
        return nombrePionFusionnes;
    }

    public static Case[] caseVoisinActivation(Case[][] plateau, Coordonnees coord, char couleur) {
        Case[] cases = new Case[8]; // Taille maximale estimée
        int nombreVoisine = 0;

        // Vérifier les diagonales
        for (int i = -1; i <= 1; i += 2) {
            for (int y = -1; y <= 1; y += 2) {
                Coordonnees newCoord = new Coordonnees(coord.ligne + i, coord.colonne + y);
                if (estDansPlateau(newCoord) && plateau[newCoord.ligne][newCoord.colonne].tourPresente() && plateau[newCoord.ligne][newCoord.colonne].couleur == couleur) {
                    cases[nombreVoisine] = plateau[newCoord.ligne][newCoord.colonne];
                    nombreVoisine++;
                }
            }
        }

        //Directions: Haut, Bas, Gauche, Droite
        int[][] directions = {{0, -1}, {1, 0}, {-1, 0}, {0, 1}};
        for (int[] dir : directions) {
            Case caseDirection = verifDirectionCouleur(plateau, coord, dir, couleur, 1);
            if (caseDirection != null) {
                cases[nombreVoisine] = caseDirection;
                nombreVoisine++;
            }
        }
        return Arrays.copyOf(cases, nombreVoisine);
    }

    /**
     * Renvoie une cas de la meme couleur depuis une coordonnée de depart et
     * dans une direction précise
     *
     * @param plateau
     * @param coord
     * @param direction
     * @param couleur
     * @return
     */
    public static Case verifDirectionCouleur(Case[][] plateau, Coordonnees coord, int[] direction, char couleur, int depart) {
        Case tab = null;
        boolean tour = false;
        int i = depart;
        while (!tour && i < Coordonnees.NB_LIGNES) {
            Coordonnees coordTemp = new Coordonnees(coord.ligne + direction[1] * i, coord.colonne + direction[0] * i);
            if (estDansPlateau(coordTemp)) {
                if (plateau[coordTemp.ligne][coordTemp.colonne].tourPresente()) {
                    tour = true;
                    if (plateau[coordTemp.ligne][coordTemp.colonne].couleur == couleur) {
                        tab = plateau[coordTemp.ligne][coordTemp.colonne];
                    }
                }
            }
            i++;

        }
        return tab;
    }
    
    public static int nombreVoisinsAdversaires(Case[][] plateau, Coordonnees coord, char couleur) {
        int nombreVoisine = 0;
        for (int i = -1; i <= 1; i++) {
            for (int y = -1; y <= 1; y++) {
                Coordonnees newCoord = new Coordonnees(coord.ligne + i, coord.colonne + y);
                if (estDansPlateau(newCoord)) {
                    if (plateau[newCoord.ligne][newCoord.colonne].tourPresente() && plateau[newCoord.ligne][newCoord.colonne].couleur != couleur) {
                        nombreVoisine++;
                    }
                }
            }
        }
        return nombreVoisine;
    }

}