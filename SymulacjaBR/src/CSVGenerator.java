import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class responsible for collection of data, and writing said data to a .csv file.
 */
public class CSVGenerator {
    /**
     * list that stores data during simulation as Strings
     */
    private static List<String[]> dataLines = new ArrayList<>();
    /**
     * csv file in which the data is saved at the end of the simulation
     */
    private File csvFile = new File("Data_Collected.csv");

    /**
     * Method that converts strings from from dataLines list to .csv format
     */
    public String csvConverter(String[] dataLine) {
        return Stream.of(dataLine).collect(Collectors.joining(","));
    }

    /**
     * Method that writes the data stored in dataLines list to the "Data_Collected.csv"
     */
    public void csvWriter() throws IOException {
        try (PrintWriter myPrintWriter = new PrintWriter(csvFile)) {
            dataLines.stream().map(this::csvConverter).forEach(myPrintWriter::println);
        }
    }

    /**
     * Method that adds the data from the current round of simulation to dataLines list
     * @param npcCount amount of  NPC's left in the simulation
     * @param weaponsCount amount of  weapons left in the simulation
     * @param medkitsCount amount of  medkits left in the simulation
     */
    public void dataAdder(int npcCount, int weaponsCount, int medkitsCount) {
        //if it is the first line of data then the String containing names of the columns is also added to the dataLines line
        if(dataLines.isEmpty()) {
            dataLines.add(new String[]{"Round", "Number of NPC", "Number of weapons", "Number of medkits"});
        }
        dataLines.add(new String[]{ String.valueOf(Logic.roundsCounter), String.valueOf(npcCount), String.valueOf(weaponsCount), String.valueOf(medkitsCount)});
        Logic.roundsCounter++;
    }

    /**
     * Method used to reset the data to be saved after starting the new simulation
     */
    public static void dataReseter() {
        dataLines.clear();
        Logic.roundsCounter = 0;
    }
}
