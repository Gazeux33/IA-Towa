/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package towa;

/**
 *
 * @author tcastillo
 */
public class DessinerPlateau {

    public static String chaine = "    A   B   C   D   E   F   G   H   I   J   K   L   M   N   O   P ";
    public static char[] tabChaine = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p'};

    public static void dessinerPlateau(Case[][] plateau){
        System.out.println(chaine);
        dessinerLigne();
        int number = 0;
        for(Case[] ligne:plateau){
            
            dessinerLigneCase(ligne,number);
            dessinerLigne();
            number++;
        }
        
    }
    
    public static void dessinerLigne(){
        System.out.println("  +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+");
    }
    
    
    public static void dessinerLigneCase(Case[] ligne,int numero){
        StringBuilder chaine = new StringBuilder();
        chaine.append(tabChaine[numero]);
        chaine.append(" |");
        for(int i=0;i<ligne.length;i++){
            chaine.append(dessinerCase(ligne[i]));
            chaine.append('|');
        }
        System.out.println(chaine);
        
    }
    
    public static String dessinerCase(Case c){
        StringBuilder chaineCase = new StringBuilder();
        chaineCase.append(c.couleur);
        if(c.hauteur > 0){
            chaineCase.append(c.hauteur);
        }
        else{
            chaineCase.append(' ');
        }
        chaineCase.append(' ');
        return chaineCase.toString();
    }
    
}
