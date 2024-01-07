// Import-Anweisungen für benötigte Android- und Firebase-Klassen
package de.codeyourapp.theheapsort;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Diese Klasse stellt eine Hilfsmethode bereit, um einen Timestamp in ein anderes Format zu konvertieren.
 */
public class TimeStampFormat {
    /**
     * Diese Methode konvertiert einen Timestamp von einem ursprünglichen Format in ein gewünschtes Format.
     *
     * @param timestamp Der zu konvertierende Timestamp im ursprünglichen Format (z.B., "yyyy-MM-dd HH:mm:ss").
     * @return Der konvertierte Timestamp im gewünschten Format (z.B., "dd.MM.yyyy 'um' HH:mm 'Uhr'").
     */
        public static String formatTimestamp(String timestamp) {
            try {
                // Anpassung des Originalformats des Timestamps, falls erforderlich
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = originalFormat.parse(timestamp);

                // Gewünschtes Format wird für die Ausgabe definiert
                SimpleDateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy 'um' HH:mm 'Uhr'");

                // Datum in das gewünschte Format umwandeln
                return targetFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return timestamp; // Im Fehlerfall wird der ursprüngliche Timestamp zurückgegeben
            }
        }

    /**
     * Diese Methode konvertiert einen Timestamp von einem ursprünglichen Format in ein gewünschtes Format.
     *
     * @param timestamp Der zu konvertierende Timestamp im ursprünglichen Format (z.B., "yyyy-MM-dd HH:mm:ss").
     * @return Der konvertierte Timestamp im gewünschten Format (z.B., "dd.MM.yyyy 'um' HH:mm 'Uhr'").
     */
    public static String convertTimestamp(String timestamp) {
        try {
            // Anpassung des Originalformats des Timestamps, falls erforderlich
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = originalFormat.parse(timestamp);

            // Gewünschtes Format für die Ausgabe definieren
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy 'um' HH:mm 'Uhr'");

            // Datum wird in das gewünschte Format umgewandelt
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return timestamp; // Im Fehlerfall wird der ursprüngliche Timestamp zurückgegeben
        }
    }
    }