import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVGenerator {
    private static List<String[]> dataLines = new ArrayList<>(); //list that stores data during simulation as Strings
    private File csvFile = new File("Data_Collected.csv"); //csv file in which the data is saved at the end of the simulation

    //method that converts the String from dataLines list to .csv format
    public String csvConverter(String[] dataLine) {
        return Stream.of(dataLine).collect(Collectors.joining(","));
    }

    //method that writes the data stored in dataLines list to the "Data_Collected.csv"
    public void csvWriter() throws IOException {
        try (PrintWriter myPrintWriter = new PrintWriter(csvFile)) {
            dataLines.stream().map(this::csvConverter).forEach(myPrintWriter::println);
        }
    }

    //method that adds the data from the current round of simiulation to dataLines list
    public void dataAdder(int npcCount, int weaponsCount, int medkitsCount) {
        //if it is the first line of data then the String containing names of the columns is also added to the dataLines line
        if(dataLines.isEmpty()) {
            dataLines.add(new String[]{"Round", "Number of NPC", "Number of weapons", "Number of medkits"});
        }
        dataLines.add(new String[]{ String.valueOf(Logic.roundsCounter), String.valueOf(npcCount), String.valueOf(weaponsCount), String.valueOf(medkitsCount)});
        Logic.roundsCounter++;
    }

    //method used to reset the data to be saved after starting the new simulation
    public static void dataReseter() {
        dataLines.clear();
        Logic.roundsCounter = 0;
    }
}
