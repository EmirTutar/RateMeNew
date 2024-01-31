package RateMe.ScanActivity.Scan;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * ScanDataManager ist verantwortlich für die Verwaltung der Datenlogik
 * innerhalb des Scan_Fragments. Die Hauptaufgaben dieser Klasse sind
 * das Extrahieren und die Aufbereitung von Produktdetails aus den
 * API-Antworten.
 *
 * Diese Klasse bietet Methoden, um Produkttitel und Bild-URLs aus
 * einem gegebenen Datenstring zu extrahieren.
 */

public class ScanDataManager {
    public List<String> extractImageUrls(String details) {
        List<String> imageUrls = new ArrayList<>();
        Scanner scanner = new Scanner(details);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("Image")) {
                String imageUrl = line.substring(line.indexOf("http"));
                imageUrls.add(imageUrl);
            }
        }
        scanner.close();
        return imageUrls;
    }

    public String extractTitle(String details) {
        if (details.contains("Title: ")) {
            int startIndex = details.indexOf("Title: ") + "Title: ".length();
            int endIndex = details.indexOf("\n", startIndex);
            return details.substring(startIndex, endIndex);
        }
        return "";
    }
}
