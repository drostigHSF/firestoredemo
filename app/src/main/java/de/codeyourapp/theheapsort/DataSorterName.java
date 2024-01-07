// Import-Anweisungen für benötigte Android- und Firebase-Klassen
package de.codeyourapp.theheapsort;

import android.app.AlertDialog;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.view.View;
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
 * Diese Klasse repräsentiert einen Daten-Sortierer nach dem Namen.
 * Sie enthält Methoden und Funktionalitäten, um Daten nach dem Namen zu sortieren.
 */
public class DataSorterName {
    // Hauptaktivität und Datenbankreferenz als Instanzvariablen
    private final MainActivity mainActivity;
    private final FirebaseFirestore dbroot;
    /**
     * Konstruktor für den DataSorterName.
     *
     * @param mainActivity Die Hauptaktivität, von der aus der Sortierer aufgerufen wird.
     * @param dbroot Die Firebase Firestore-Datenbankreferenz.
     */
    public DataSorterName(MainActivity mainActivity, FirebaseFirestore dbroot) {
        this.mainActivity = mainActivity;
        this.dbroot = dbroot;
    }

    /**
     * Zeigt ein Dialogfeld für die Auswahl der Sortierreihenfolge nach Nachnamen an.
     * Erlaubt dem Benutzer, zwischen aufsteigender und absteigender Sortierreihenfolge zu wählen.
     */
    public void showSortDialog() {
        // AlertDialog.Builder zur Erstellung des Dialogfelds
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Nach Nachnamen sortieren...");

        // Erstellen des Layouts für das Dialogfeld
        View view = mainActivity.getLayoutInflater().inflate(R.layout.dialog_data_sort, null);
        builder.setView(view);

        // Referenzen zu den User-Interface-Elementen im Dialogfeld
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        RadioButton ascendingRadioButton = view.findViewById(R.id.radioAscending);
        RadioButton descendingRadioButton = view.findViewById(R.id.radioDescending);

        // Setzen der positiven Schaltfläche (Bestätigen)
        builder.setPositiveButton("Bestätigen", (dialog, which) -> {
            // Überprüfen, welche Sortierreihenfolge ausgewählt wurde
            boolean ascendingOrder = ascendingRadioButton.isChecked();
            boolean descendingOrder = descendingRadioButton.isChecked();

            // Basierend auf der Auswahl den entsprechenden Sortierprozess starten
            if (ascendingOrder) {
                DataSorterName.setAscendingOrder(true);
                DataSorterName.sortDataName(mainActivity, dbroot, true);
            } else if (descendingOrder) {
                DataSorterName.setAscendingOrder(false);
                DataSorterName.sortDataName(mainActivity, dbroot, false);
            }
        });

        // Setzen der negativen Schaltfläche (Abbrechen)
        builder.setNegativeButton("Abbrechen", (dialog, which) -> dialog.dismiss());

        // Anzeigen des erstellten Dialogfelds
        builder.show();
    }

    /**
     * Statische Variable, die die aktuelle Sortierreihenfolge für den Nachnamen speichert.
     * Wenn `true`, erfolgt die Sortierung in aufsteigender Reihenfolge, andernfalls in absteigender Reihenfolge.
     * Standardmäßig ist die Sortierreihenfolge auf aufsteigend gesetzt.
     */
    private static boolean ascendingOrder = true;

    /**
     * Statische Methode, die die Sortierreihenfolge für den Nachnamen festlegt.
     * @param ascendingOrder Wenn `true`, erfolgt die Sortierung in aufsteigender Reihenfolge, andernfalls in absteigender Reihenfolge.
     */
    public static void setAscendingOrder(boolean ascendingOrder) {
        DataSorterName.ascendingOrder = ascendingOrder;
    }

    /**
     * Statische Methode, die die Daten nach dem Nachnamen sortiert und in der UI anzeigt.
     * @param mainActivity Die Hauptaktivität, auf der die Daten angezeigt werden.
     * @param dbroot Die Firebase Firestore-Datenbankreferenz.
     * @param ascendingOrder Wenn `true`, erfolgt die Sortierung in aufsteigender Reihenfolge, andernfalls in absteigender Reihenfolge.
     */
    public static void sortDataName(MainActivity mainActivity, FirebaseFirestore dbroot, boolean ascendingOrder) {
        long queryStartTime = System.currentTimeMillis();
        // User-Interface-Elemente initialisieren
        TextView tvTitle = mainActivity.findViewById(R.id.tvTitle);
        tvTitle.setText("Mitglieder (Alphabetisch sortiert, A-Z)");
        TextView tvRowCount = mainActivity.findViewById(R.id.tvRowCount);

        dbroot.collection("students")
                .get()
                .addOnCompleteListener(task -> {
                    // Zeit- und User-Interface-Informationen aktualisieren
                    int rowCount = task.getResult().size();
                    long queryEndTime = System.currentTimeMillis();
                    long queryElapsedTime = queryEndTime - queryStartTime;
                    String countText = "Verbindungsaufbau und Umsortierung der Daten dauerte " + queryElapsedTime + " Millisekunden. \nEs befinden sich " + rowCount + " Datensätze in der Datenbank.";
                    tvRowCount.setText(countText);
                    if (task.isSuccessful()) {
                        TableLayout tableLayout = mainActivity.findViewById(R.id.tableLayout);

                        // Vorhandenen Inhalt löschen, wenn es nicht um einen Reset geht
                        tableLayout.removeAllViews();

                        // Daten in Liste für die Sortierung vorbereiten
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        List<Map<String, Object>> dataToSort = new ArrayList<>();
                        for (DocumentSnapshot document : documents) {
                            dataToSort.add(document.getData());
                        }

                        // Sortieren nach Name
                        Collections.sort(dataToSort, new Comparator<Map<String, Object>>() {
                            @Override
                            public int compare(Map<String, Object> data1, Map<String, Object> data2) {
                                String name1 = (String) data1.get("name");
                                String name2 = (String) data2.get("name");

                                if (ascendingOrder) {
                                    return name1.compareTo(name2);
                                } else {
                                    tvTitle.setText("Mitglieder (Alphabetisch sortiert, Z-A)");
                                    return name2.compareTo(name1);
                                }
                            }
                        });

                        // Daten in die User Interfaces einfügen
                        for (Map<String, Object> data : dataToSort) {

                            // Eine neue Tabellenzeile wird durch den Inflater erstellt
                            TableRow dataRow = (TableRow) mainActivity.getLayoutInflater().inflate(R.layout.table_row_layout, null);

                            // Setzen der Daten in die TextViews
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

                            // Die erstellte TableRow wird zum TableLayout hinzugefügt
                            tableLayout.addView(dataRow);
                        }
                    } else {
                        Toast.makeText(mainActivity.getApplicationContext(), "Error getting data", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
