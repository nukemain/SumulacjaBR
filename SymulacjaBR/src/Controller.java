import java.io.FileNotFoundException;
import java.util.Scanner;

public class Controller {
    public static void main(String[] args) throws FileNotFoundException {
        //TODO: Na koniec jak już będzie śmigać dodać input od użytkownika w GUI.
        int NPCcount = 10;
        int sizeX=30;
        int sizeY=sizeX; //plansza ma być kwadratem, bo tak łatwiej w życiu i tyle pozdro
        /* wykomentowałem bo do robiąc gui nie potrzebuję
        Scanner menu = new Scanner(System.in);
        System.out.println("Menu: ");
        System.out.println("1. Use current settings");
        System.out.println("2. Load settings from the file");
        int loadingMenu = menu.nextInt();
        if(loadingMenu == 1) {
            NPCcount = 10;
            sizeX = 20;
            sizeY = sizeX;
        }
        else if(loadingMenu == 2) {
            FileReader.fileReader();
            NPCcount = FileReader.npcCount;
            sizeX = FileReader.size;
            sizeY = sizeX;
        }
        else {
            System.out.println("Choose 1 or 2");
        }*/

        if(NPCcount>(sizeX-1)*(sizeY-1)*0.25){ //mniej niż 25% planszy to npc -> czemu 25%? liczba wybrana tak o z dupy + cał plansza npctów srednio działa
            System.out.println("Number of NPCs can not be higher than amount of fields.");
        }
        else{
            Logic.Symulacja(sizeX,sizeY,NPCcount);
        }
    }
}
