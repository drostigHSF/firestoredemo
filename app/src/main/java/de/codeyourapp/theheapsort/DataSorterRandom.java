// Import-Anweisungen für benötigte Android- und Firebase-Klassen
package de.codeyourapp.theheapsort;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Diese Methode führt eine Datenbankabfrage durch und zeigt sie in einer zufällige Reihenfolge an.
 */
public class DataSorterRandom {
    private final MainActivity mainActivity;
    private final FirebaseFirestore dbroot;
    /**
     * Konstruktor für die DataSorterRandom-Klasse.
     * @param mainActivity Die Hauptaktivität, auf der die Daten angezeigt werden.
     * @param dbroot Die Firebase Firestore-Datenbankreferenz.
     */
    public DataSorterRandom(MainActivity mainActivity, FirebaseFirestore dbroot) {
        this.mainActivity = mainActivity;
        this.dbroot = dbroot;
    }

    /**
     * Methode zum Sortieren von Daten in zufälliger Reihenfolge.
     */
    public void sortDataRandom() {
        // Startzeitpunkt für die Datenbankabfrage festlegen
        long queryStartTime = System.currentTimeMillis();
        // TextView-Elemente für Titel und Zeilenanzahl initialisieren
        TextView tvTitle = mainActivity.findViewById(R.id.tvTitle);
        tvTitle.setText("Mitglieder (Zufällige Sortierung)");
        TextView tvRowCount = mainActivity.findViewById(R.id.tvRowCount);

        // Firestore-Datenbankabfrage durchführen
        dbroot.collection("students")
                .get()
                .addOnCompleteListener(task -> {
                    // Anzahl der Zeilen in der Datenbank abrufen
                    int rowCount = task.getResult().size();
                    // Endzeitpunkt für die Datenbankabfrage setzen
                    long queryEndTime = System.currentTimeMillis();
                    // Gesamtdauer der Datenbankabfrage berechnen
                    long queryElapsedTime = queryEndTime - queryStartTime;
                    // Text für Zeilenanzahl und Abfragezeit erstellen
                    String countText = "Verbindungsaufbau und Umsortierung der Daten dauerte " + queryElapsedTime + " Millisekunden. \nEs befinden sich " + rowCount + " Datensätze in der Datenbank.";
                    // Zeilenanzahl in der TextView aktualisieren
                    tvRowCount.setText(countText);
                    // Überprüfen, ob die Abfrage erfolgreich war
                    if (task.isSuccessful()) {
                        // TableLayout-Element für die Anzeige der Daten initialisieren
                        TableLayout tableLayout = mainActivity.findViewById(R.id.tableLayout);

                        // Vorhandenen Inhalt löschen, wenn es nicht um einen Reset geht
                        tableLayout.removeAllViews();

                        // Daten aus der Firestore-Abfrage extrahieren
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        List<Map<String, Object>> dataToSort = new ArrayList<>();
                        for (DocumentSnapshot document : documents) {
                            dataToSort.add(document.getData());
                        }

                        // Daten in zufälliger Reihenfolge sortieren
                        Collections.shuffle(dataToSort);

                        // Daten in die User Interfaces einfügen
                        for (Map<String, Object> data : dataToSort) {

                            // TableRow-Element wird durch den Inflater nach der Layout-Ressorce "R.layout:table_row_layout" erstellt
                            TableRow dataRow = (TableRow) mainActivity.getLayoutInflater().inflate(R.layout.table_row_layout, null);

                            // Daten in die TextViews setzen
                            TextView idTextView = dataRow.findViewById(R.id.idTextView); // TextView für die ID
                            idTextView.setText((String) data.get("id"));

                            TextView nameTextView = dataRow.findViewById(R.id.nameTextView); // TextView für die Namen
                            nameTextView.setText((String) data.get("name"));

                            TextView vornameTextView = dataRow.findViewById(R.id.vornameTextView); // TextView für die Vornamen
                            vornameTextView.setText((String) data.get("vorname"));

                            TextView emailTextView = dataRow.findViewById(R.id.emailTextView); // TextView für die E-Mail
                            emailTextView.setText((String) data.get("email"));

                            TextView timestampTextView = dataRow.findViewById(R.id.timestampTextView); // TextView für den Timestamp
                            String timestamp = (String) data.get("timestamp");
                            // Timestamp in das gewünschte Format konvertieren
                            String formattedTimestamp = TimeStampFormat.convertTimestamp(timestamp);
                            timestampTextView.setText((String) formattedTimestamp);

                            // TableRow zum TableLayout hinzufügen
                            tableLayout.addView(dataRow);
                        }
                    }
                    else {
                        // Fehlermeldung anzeigen, falls die Daten nicht abgerufen werden können
                        Toast.makeText(mainActivity.getApplicationContext(), "Error getting data", Toast.LENGTH_LONG).show();
                    }
                });
    }

}
