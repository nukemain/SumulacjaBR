import java.util.Objects;
import java.util.Scanner;
import java.util.Random;
public class Logic {
    public static void main(String[] args)
    {
            Symulacja(50,50,400); //tymczasowo zhardkodowane
            //TODO: wyłapywanie czy NPCcount nie jest większy od maksymalnej możliwej ilości npc (liczb pól w planszy) - zrobić to przed odpaleniem symulacji
    }
    static void Symulacja(int sizeX,int sizeY,int NPCcount){
        //TODO: Wczytywanie danych z pliku? - nie trzeba by było przekazywać miliona zmiennych do funkcji

        /*
            sizeX = rozmiar x planszy
            sizeY = rozmiar Y planszy
            NPCcount = ile agentów npc ma mieć symulacja
            WPNcount = ile sztuk broni ma zrespić symulacja
            //todo: przerebić ten komentarz na angielski jak już nie będzie potrebny
        */

        String[][]  board = new String[sizeX][sizeY];
        Random rand = new Random();

        //loop to fill the board with empty spaces ("[ ]")
        for(int y=0;y<sizeY;y++){
            for(int x=0;x<sizeX;x++){
                board[y][x] = "[ ]";
            }
        }

        //TODO: limit npc (prawdopodobnie (sizeX-1 * sizeY-1)-1) - kod za to odpowiedzialny dać przed wywołaniem symulacji !!!
        //Logic required for spawning NPC's
        //NOTE: this logic purposefully prevents spawns on the edges of the board
        int NPC=0; //counter for how many NPC have been spawned
        while(NPC<NPCcount){
            int posX= rand.nextInt(0, sizeX);
            int posY= rand.nextInt(0, sizeY);
            try {      //check if the random coordinates are not occupied and don't neighbour with any other NPC
                if (!Objects.equals(board[posY][posX], "[x]") &&
                        !Objects.equals(board[posY + 1][posX], "[x]") &&
                        !Objects.equals(board[posY + 1][posX + 1], "[x]") &&
                        !Objects.equals(board[posY][posX + 1], "[x]") &&
                        !Objects.equals(board[posY - 1][posX + 1], "[x]") &&
                        !Objects.equals(board[posY - 1][posX], "[x]") &&
                        !Objects.equals(board[posY - 1][posX - 1], "[x]") &&
                        !Objects.equals(board[posY][posX - 1], "[x]") &&
                        !Objects.equals(board[posY + 1][posX - 1], "[x]")) {
                    board[posY][posX] = "[x]";
                    //todo: tu wywołanie konstruktora klasy NPC
                    NPC++;
                }else{
                    //however we sometimes ignore the check above (with a 15% rate) to make the NPC placement more random todo: 15 wybrane tak o, może jakaś lepsza wartość?
                    //This also prevents a situation where the amount of NPCs that we need to spawn would make it impossible
                    //to spawn them at least 1 tile away from each other.
                    if( (rand.nextInt(1,101)<=15) && (!Objects.equals(board[posY][posX], "[x]")) ){
                        board[posY][posX] = "[x]";
                        //todo: tu wywołanie konstruktora klasy NPC
                        NPC++;
                    }
                }
            } catch(Exception e){/*po prostu zrespić ziutka`gdzieś indziej*/}
        }

        //Loop below counts how many non NPC neighbouring tiles are on the board.
        //The found value is then used to decide the amount of weapons and medkits to spawn
        int NoNeighbours=0;
        for (int Y = 0; Y < sizeY; Y++) {
            for (int X = 0; X < sizeX; X++){
                try {
                    if (Objects.equals(board[Y][X], "[ ]") &&
                            Objects.equals(board[Y + 1][X], "[ ]") &&
                            Objects.equals(board[Y + 1][X + 1], "[ ]") &&
                            Objects.equals(board[Y][X + 1], "[ ]") &&
                            Objects.equals(board[Y - 1][X + 1], "[ ]") &&
                            Objects.equals(board[Y - 1][X], "[ ]") &&
                            Objects.equals(board[Y - 1][X - 1], "[ ]") &&
                            Objects.equals(board[Y][X - 1], "[ ]") &&
                            Objects.equals(board[Y + 1][X - 1], "[ ]")) {
                        NoNeighbours++;
                    }
                }catch (Exception e){}
            }
        }

        int WPNcount=0;

        //Logic deciding how many weapons should we spawn
        //numerical values chosen through trial and error
        if((NoNeighbours/5)<((int)(NPC*1.5))){ // check if we can spawn 1.5 weapon per NPC -> if not, use a smaller number
            WPNcount =(NoNeighbours/5);
        }else{
            WPNcount = ((int) (NPC * 1.5));
        }

        //Logic required for spawning Weapons
        //Works almost exactly the same way as the NPC spawning logic above
        int WPN=0;
        while(WPN<WPNcount){
            int posX= rand.nextInt(0, sizeX);
            int posY= rand.nextInt(0, sizeY);
            try {
                if (Objects.equals(board[posY][posX], "[ ]") &&
                        Objects.equals(board[posY + 1][posX], "[ ]") &&
                        Objects.equals(board[posY + 1][posX + 1], "[ ]") &&
                        Objects.equals(board[posY][posX + 1], "[ ]") &&
                        Objects.equals(board[posY - 1][posX + 1], "[ ]") &&
                        Objects.equals(board[posY - 1][posX], "[ ]") &&
                        Objects.equals(board[posY - 1][posX - 1], "[ ]") &&
                        Objects.equals(board[posY][posX - 1], "[ ]") &&
                        Objects.equals(board[posY + 1][posX - 1], "[ ]")) {
                    board[posY][posX] = "[W]";
                    //todo: tu wywołanie konstruktora klasy NPC
                    WPN++;
                }
            } catch(Exception ignored){}
        }


        //tymcasowa pętla do drukowania tablicy
        for(int y=0;y<sizeY;y++){
            for(int x=0;x<sizeX;x++){
                System.out.print(board[y][x]);
            }
            System.out.println();
        }
    }
}
