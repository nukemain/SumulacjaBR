import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVGenerator {
    //public static int roundsCounter = 0;
    private static List<String[]> dataLines = new ArrayList<>();
    File csvFile = new File("Data_Collected.csv");

    public String escapeSpecialCharacters(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Input data cannot be null");
        }
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public void csvWriter() throws IOException {
        try (PrintWriter pw = new PrintWriter(csvFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
    }

    public void dataAdder(int npcCount, int weaponsCount, int medkitsCount) {
        if(dataLines.isEmpty()) {
            dataLines.add(new String[]{"Round", "Number of NPC", "Number of weapons", "Number of medkits"});
        }
        dataLines.add(new String[]{ String.valueOf(Logic.roundsCounter), String.valueOf(npcCount), String.valueOf(weaponsCount), String.valueOf(medkitsCount)});
        int roundsCounter;
        Logic.roundsCounter++;
    }

    public static void dataReseter() {
        dataLines.clear();
        Logic.roundsCounter = 0;
    }
}
