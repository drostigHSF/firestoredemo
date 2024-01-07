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
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Die Klasse `DataSorterTimestamp` ist für die Sortierung der Mitgliederdaten nach dem Zeitstempel verantwortlich.
 * Sie wird in Verbindung mit der `MainActivity` und der Firestore-Datenbank (`dbroot`) verwendet.
 */
public class DataSorterTimestamp {
    // Instanzvariablen für die MainActivity und die Firestore-Datenbank
    private final MainActivity mainActivity;
    private final FirebaseFirestore dbroot;

    /**
     * Konstruktor für die `DataSorterTimestamp`-Klasse.
     * @param mainActivity Die MainActivity, die diese Klasse verwendet.
     * @param dbroot Die Firestore-Datenbankinstanz.
     */
    public DataSorterTimestamp(MainActivity mainActivity, FirebaseFirestore dbroot) {
        this.mainActivity = mainActivity;
        this.dbroot = dbroot;
    }

    /**
     * Die Methode `sortDataTimestamp` in der Klasse `DataSorterTimestamp` wird verwendet, um Mitgliederdaten nach dem Zeitstempel zu sortieren.
     * Dabei werden die neuesten Daten zuerst angezeigt.
     */
    public void sortDataTimestamp() {
        // Startzeit für die Datenbankabfrage festlegen
        long queryStartTime = System.currentTimeMillis();
        // Referenzen zu User-Interface-Elementen holen
        TextView tvTitle = mainActivity.findViewById(R.id.tvTitle);
        tvTitle.setText("Mitglieder (Sortierung nach Timestamp, neueste zuerst)");
        TextView tvRowCount = mainActivity.findViewById(R.id.tvRowCount);

        // Firestore-Datenbankabfrage starten.
        dbroot.collection("students")
                .get()
                .addOnCompleteListener(task -> {
                    // Anzahl der Zeilen (Datenbankergebnisse) erhalten
                    int rowCount = task.getResult().size();
                    // Endzeit für die Datenbankabfrage setzen
                    long queryEndTime = System.currentTimeMillis();
                    // Zeit für die Datenbankabfrage berechnen
                    long queryElapsedTime = queryEndTime - queryStartTime;
                    // Text für die Anzahl der Datensätze und Zeit anzeigen
                    String countText = "Verbindungsaufbau und Umsortierung der Daten dauerte " + queryElapsedTime + " Millisekunden. \nEs befinden sich " + rowCount + " Datensätze in der Datenbank.";
                    tvRowCount.setText(countText);
                    if (task.isSuccessful()) {
                        // Referenz zum TableLayout holen
                        TableLayout tableLayout = mainActivity.findViewById(R.id.tableLayout);

                        // Vorhandenen Inhalt löschen, wenn es nicht um einen Reset geht.
                        tableLayout.removeAllViews();

                        // Daten in eine Liste laden.
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        List<Map<String, Object>> dataToSort = new ArrayList<>();
                        for (DocumentSnapshot document : documents) {
                            dataToSort.add(document.getData());
                        }

                        // Sortieren nach Timestamp, neuester zuerst.
                        Collections.sort(dataToSort, new Comparator<Map<String, Object>>() {
                            @Override
                            public int compare(Map<String, Object> data1, Map<String, Object> data2) {
                                String timestamp1 = (String) data1.get("timestamp");
                                String timestamp2 = (String) data2.get("timestamp");
                                return timestamp2.compareTo(timestamp1); // Umkehrung der Reihenfolge
                            }
                        });

                        // Daten in die User-Interface-Elemente einfügen
                        for (Map<String, Object> data : dataToSort) {

                            // Eine neue TableRow wird durch den Inflater nach der Layout-Ressorce "R.layout:table_row_layout" erstellt.
                            // Hierbei wird das XML-Layout in ein entsprechendes Java-Objekt umgewandelt, um es später in dem User Interface zu verwenden.
                            TableRow dataRow = (TableRow) mainActivity.getLayoutInflater().inflate(R.layout.table_row_layout, null);

                            // Setzen der Daten in die TextViews.
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
                            // Timestamp in das gewünschte Format konvertieren.
                            String formattedTimestamp = TimeStampFormat.convertTimestamp(timestamp);
                            timestampTextView.setText((String) formattedTimestamp);

                            // TableRow zum TableLayout hinzufügen.
                            tableLayout.addView(dataRow);
                        }
                    } else {
                        Toast.makeText(mainActivity.getApplicationContext(), "Error getting data", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
