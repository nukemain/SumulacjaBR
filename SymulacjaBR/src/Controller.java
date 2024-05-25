public class Controller {
    public static void main(String[] args)
    {
        //TODO: Na koniec jak już będzie śmigać dodać input od użytkownika w GUI.
        int NPCcount = 5;
        int sizeX=20;
        int sizeY=20;
        if(NPCcount>(sizeX-1)*(sizeY-1)*0.25){ //mniej niż 25% planszy to npc -> czemu 25%? liczba wybrana tak o z dupy + cał plansza npctów srednio działa
            System.out.println("Number of NPCs can not be higher than amount of fields.");
        }
        else{
            Logic.Symulacja(sizeX,sizeY,NPCcount);
        }
    }
}
