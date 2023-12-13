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


    public static void dessinerPlateau(Case[][] plateau){
        dessinerLigne();
        for(Case[] ligne:plateau){
            
            dessinerLigneCase(ligne);
            dessinerLigne();
        }
        
    }
    
    public static void dessinerLigne(){
        System.out.println("+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+");
    }
    
    
    public static void dessinerLigneCase(Case[] ligne){
        StringBuilder chaine = new StringBuilder();
        chaine.append('|');
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
