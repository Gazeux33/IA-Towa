package towa;

import towa.Case;
import towa.IATowa;

import java.util.Random;

public class MinMax {

    /**
     * Nombre d'action selectionnées parmi les mieux évaluées et les pires.
     */
    static final int NB_ACTION_SELEC = 8;

    /**
     * Profondeur de l'abre des possibilités calculées (coups d'avances).
     */
    static final int PROFONDEUR = 3;

    /**
     * Permet de trouver la meilleure action.
     *
     * @param plateau du jeu.
     * @param actionsPossibles ensembles des actions possibles à partir de ce plateau.
     * @return la meilleure action
     */
    static String trouveMeilleurAction(Case[][] plateau, String[] actionsPossibles, int nbToursJeu,char couleur){
        if(nbToursJeu != 1)actionsPossibles = selectionnerActions(actionsPossibles, couleur);
        actionsPossibles = IATowa.enleverVitaliteTableau(actionsPossibles);
        if(nbToursJeu==1){

            for(String action:actionsPossibles){
                System.out.print(action + " ");
            }
            Random random = new Random();
            int randomNumber = random.nextInt(actionsPossibles.length);
            System.out.println("nombre aleartoire:" + randomNumber);

            return actionsPossibles[randomNumber];
        }

        String meilleur_action = null;
        int meilleur_score = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE; // -infinie
        int beta = Integer.MAX_VALUE; // +infini

        for(String action:actionsPossibles){

            Case[][] plateauAModifier = IATowa.copierPlateau(plateau);
            IATowa.mettreAJour(plateauAModifier,action,couleur);
            int score = minMax(plateauAModifier,true, 0,couleur,couleur, alpha,beta);

            if(score>meilleur_score){
                meilleur_action = action;
                meilleur_score = score;
            }

        }
        return meilleur_action;
    }

    /**
     * Fonction recursive visant à determiner le meilleur score
     * @param plateau plateau du jeu
     * @param profondeurCourante profondeur actuelle
     * @return score de l'action
     */
    static int minMax(Case[][] plateau, boolean maximising, int profondeurCourante,char couleurCourante,char couleurInitiale,int alpha ,int beta) {
        JoueurTowa joueur = new JoueurTowa();
        String[] actionsPossibles;
        if (profondeurCourante >= PROFONDEUR) {
            return evaluatePlateau(plateau,couleurInitiale);
        }
        if (maximising) {
            int score = Integer.MIN_VALUE;
            actionsPossibles = joueur.actionsPossibles(plateau, couleurCourante, 8);
            actionsPossibles = selectionnerActions(actionsPossibles,couleurCourante);
            actionsPossibles = IATowa.enleverVitaliteTableau(actionsPossibles);
            for (String action : actionsPossibles) {
                Case[][] plateauCopie = IATowa.copierPlateau(plateau);
                IATowa.mettreAJour(plateauCopie, action, couleurCourante);
                score = Math.max(score,minMax(plateauCopie, false, profondeurCourante + 1,couleurCourante,couleurInitiale,alpha,beta));
                alpha = Math.max(alpha, score);
                if (score >= beta) {
                    break; // Élagage alpha
                }

            }
            return score;
        } else {
            int score = Integer.MAX_VALUE;
            actionsPossibles = joueur.actionsPossibles(plateau, Utils.inverseCouleurJoueur(couleurCourante), 8);
            actionsPossibles = selectionnerActions(actionsPossibles,Utils.inverseCouleurJoueur(couleurCourante));
            actionsPossibles = IATowa.enleverVitaliteTableau(actionsPossibles);
            for (String action : actionsPossibles) {
                Case[][] plateauCopie = IATowa.copierPlateau(plateau);
                IATowa.mettreAJour(plateauCopie, action, Utils.inverseCouleurJoueur(couleurCourante));
                score = Math.min(score,minMax(plateauCopie, true, profondeurCourante + 1,couleurCourante,couleurInitiale,alpha,beta));
                beta = Math.min(beta, score);
                if (score <= alpha) {
                    break; // Élagage beta
                }
            }
            return score;
        }
    }

    public static String[] selectionnerActions(String[] actionsPossibles, char couleur) {
        IATowa.Action[] tabActions = new IATowa.Action[actionsPossibles.length];
        for(int i=0;i<actionsPossibles.length;i++){
            tabActions[i] = new IATowa.Action(actionsPossibles[i], scoreAction(actionsPossibles[i], couleur));
        }
        triSelection(tabActions);
        int nombreAction = Math.min(tabActions.length,NB_ACTION_SELEC);

        String[] actionsSelectionnees = new String[nombreAction*2];
        for(int i=0;i<nombreAction;i++){
            actionsSelectionnees[i] = tabActions[i].action;
            actionsSelectionnees[i+nombreAction] = tabActions[tabActions.length-1-i].action;
        }
        return actionsSelectionnees;
    }

    public static int scoreAction(String action,char couleur){
        int[] numbers = IATowa.getVitalite(action);
        switch (couleur){
            case Case.CAR_NOIR:
                return numbers[0]-numbers[1];

            case Case.CAR_BLANC:
                return numbers[1]-numbers[0];

            default:
                return 0;
        }

    }

    public static void triSelection(IATowa.Action[] actions){
        for(int i=0;i<actions.length;i++){
            int indexMin = indexMinAction(actions,i);
            IATowa.Action temp = actions[i];
            actions[i] = actions[indexMin];
            actions[indexMin] = temp;
        }

    }

    public static int indexMinAction(IATowa.Action[] actions, int depart){
        int index = 0;
        int min = Integer.MAX_VALUE;
        for(int i=depart;i<actions.length;i++){
            if(actions[i].score<min){
                min = actions[i].score;
                index = i;
            }
        }
        return index;
    }

    static int evaluatePlateau(Case[][] plateau,char couleur){

        NbPions nbPions = JoueurTowa.nbPions(plateau);
        if(couleur==Case.CAR_NOIR) {
            return nbPions.nbPionsNoirs - nbPions.nbPionsBlancs;
        }
        return nbPions.nbPionsBlancs-nbPions.nbPionsNoirs;
    }
}
