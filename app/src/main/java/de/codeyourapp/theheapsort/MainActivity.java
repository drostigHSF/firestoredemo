// Import-Anweisungen für benötigte Android- und Firebase-Klassen
package de.codeyourapp.theheapsort;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Diese Klasse repräsentiert die Hauptaktivität der Anwendung und erweitert die AppCompatActivity.
 */
public class MainActivity extends AppCompatActivity {
    // Zeitpunkt für den Start der App setzen (in Millisekunden)
    long appStartTime = System.currentTimeMillis();
    // Gesture-Detector und User-Interface-Elemente initialisieren
    private GestureDetector gestureDetector; //GestureDetector wird verwendet, um Wischgesten zu erkennen.
    private TableLayout tableLayout; //Table-Layout-Instanz, die die Tabelle aus dem Layout repräsentiert
    private DataAddEntryDialog dataAddEntryDialog;
    // Sortierer für zufällige, zeitliche und namensbasierte Sortierung initialisieren
    DataSorterRandom dataSorterRandom = new DataSorterRandom(this, FirebaseFirestore.getInstance());
    DataSorterTimestamp dataSorterTimestamp = new DataSorterTimestamp(this, FirebaseFirestore.getInstance());
    DataSorterName dataSorterName = new DataSorterName(this, FirebaseFirestore.getInstance());

    // Firestore-Datenbankinstanz initialisieren
    private FirebaseFirestore dbroot;

    // Zeitmessung für die App-Initialisierung
    private long appInitializationTime;

    // Methode, die beim Erstellen der Aktivität aufgerufen wird.
    @SuppressLint({"ClickableViewAccessibility", "WrongViewCast"}) // Unterdrückt eine Warnung für den Touch-Listener, die durch den GestureDetector verursacht wird.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TableLayout aus der Layout-Ressource holen
        tableLayout = findViewById(R.id.tableLayout);
        // Erstellen eines neuen SwipeGestureListener und Zuweisen an den GestureDetector
        gestureDetector = new GestureDetector(this, new SwipeGestureListener(this));

        // Hinzufügen eines OnTouchListener zum TableLayout
        tableLayout.setOnTouchListener((v, event) -> {
            // Übergeben des Events an den GestureDetector
            gestureDetector.onTouchEvent(event);
            return true;
        });

        // Dialog für das Hinzufügen von Einträgen initialisieren und mit der Datenbankinstanz verknüpfen.
        dbroot = FirebaseFirestore.getInstance();
        dataAddEntryDialog = new DataAddEntryDialog(this, dbroot);
        dataSorterName = new DataSorterName(this, dbroot);

        // User-Interface-Elemente mit ihren zugehörigen Buttons verknüpfen und Click-Listener hinzufügen.
        Button btnCloseApp = findViewById(R.id.btnCloseApp); // Button für das Schließen der App
        btnCloseApp.setOnClickListener(v -> showExitConfirmationDialog());

        Button btnHeapsort = findViewById(R.id.btnHeapsort); // Button für die Anwendung des Heapsort-Algorithmus auf die Daten
        btnHeapsort.setOnClickListener(v -> sortDataHeapsort());

        Button btnSortName = findViewById(R.id.btnSortName); // Button für die Sortierung der Daten nach Namen
        btnSortName.setOnClickListener(v -> dataSorterName.showSortDialog());

        Button btnSortRandom = findViewById(R.id.btnSortRandom); // Button für die zufällige Sortierung der Daten
        btnSortRandom.setOnClickListener(v -> dataSorterRandom.sortDataRandom());

        Button btnSortByTimestamp = findViewById(R.id.btnSortByTimestamp); // Button für die Sortierung der Daten nach dem Timestamp
        btnSortByTimestamp.setOnClickListener(v -> dataSorterTimestamp.sortDataTimestamp());

        Button btnResetSort = findViewById(R.id.btnResetSort); // Button zum Zurücksetzen der Sortierung und Anzeigen aller Daten in der ursprünglichen Reihenfolge
        btnResetSort.setOnClickListener(v -> resetData());

        Button btnAddEntry = findViewById(R.id.btnAddEntry); // Button zum Anzeigen des Dialogfelds zum Hinzufügen neuer Einträge
        btnAddEntry.setOnClickListener(v -> dataAddEntryDialog.showAddEntryDialog());

        // Laden der vorhandenen Daten beim Start der Aktivität
        loadData();

        long appEndTime = System.currentTimeMillis(); // Endzeit für App-Initialisierung setzen
        appInitializationTime = appEndTime - appStartTime; // Zeit für App-Initialisierung berechnen
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Übergeben des Events an den GestureDetector
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * Diese Methode zeigt einen Informationsblock über den Heapsort-Algorithmus in einem AlertDialog an.
     */
    public void showInformationBlock() {
        // Ein AlertDialog.Builder wird verwendet, um den Dialog zu erstellen.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Titel für den Dialog setzen.
        builder.setTitle("Über den Heapsort");
        // Nachricht für den Dialog setzen. Hier wird eine ausführliche Erklärung des Heapsort-Algorithmus bereitgestellt.
        builder.setMessage("Heapsort ist ein effizienter Sortieralgorithmus, der auf der Datenstruktur \"Heap\" basiert. Ein Heap ist ein spezieller Baum, bei dem für jeden Knoten im Baum die Schlüsselbedingung erfüllt ist: Der Schlüssel des Elternknotens ist entweder immer größer (max-Heap) oder immer kleiner (min-Heap) als die Schlüssel seiner Kinder.\n" +
                "\n" +
                "Der Heapsort-Algorithmus nutzt einen Binärheap, in dem das größte Element (bei einem max-Heap) an der Wurzel liegt. Der Algorithmus arbeitet in zwei Phasen: der Aufbauphase (Heapify) und der Sortierphase.");
        // Ein "OK"-Button wird hinzugefügt, um den Dialog zu schließen, wenn darauf geklickt wird.
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        // Den erstellten Dialog anzeigen
        builder.show();
    }

    /**
     * Diese Methode zeigt einen Bestätigungsdialog für das Beenden der App an.
      */
    private void showExitConfirmationDialog() {
        // Ein AlertDialog.Builder wird verwendet, um den Dialog zu erstellen.
        new AlertDialog.Builder(this)
                // Die Nachricht im Dialogfenster wird festgelegt, um den Benutzer zu fragen, ob die App wirklich geschlossen werden soll.
                .setMessage("Möchten Sie die App wirklich schließen?")
                // Der Positive Button wird hinzugefügt, um das Beenden der App zu bestätigen.
                .setPositiveButton("Ok", (dialog, id) -> finish())
                // Der Negative Button wird hinzugefügt, um das Beenden der App abzubrechen.
                .setNegativeButton("Abbrechen", (dialog, id) -> dialog.dismiss())
                // Der erstellte AlertDialog wird erstellt, aber noch nicht angezeigt.
                .create()
                // Der erstellte AlertDialog wird angezeigt, sodass der Benutzer darauf reagieren kann.
                .show();
    }

    /**
     * Diese Methode führt eine Heapsort-Sortierung der Datenbankdaten durch und zeigt die Ergebnisse in einer Tabelle an.
     */
    private void sortDataHeapsort() {
        // Zeitpunkt für den Start der Datenbankabfrage setzen
        long queryStartTime = System.currentTimeMillis();
        // User-Interface-Elemente für den Titel und die Zeilenanzahl initialisieren
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Mitglieder (Sortierung der ID nach Heapsort)");
        TextView tvRowCount = findViewById(R.id.tvRowCount);

        // Auf die Firestore-Datenbank zugreifen und alle Dokumente abrufen.
        dbroot.collection("students")
                .get()
                .addOnCompleteListener(task -> {
                    // Die Variable 'rowCount' wird erstellt und mit der Anzahl der Dokumente in der Firestore-Datenbankabfrage initialisiert.
                    int rowCount = task.getResult().size();
                    // Endzeit für Datenbankabfrage setzen
                    long queryEndTime = System.currentTimeMillis();
                    // Zeit für Datenbankabfrage berechnen
                    long queryElapsedTime = queryEndTime - queryStartTime;
                    // Anzeigen der Zeit für den Verbindungsaufbau und die Umsortierung sowie der Anzahl der Datensätze
                    String CountText = "Verbindungsaufbau und Umsortierung der Daten dauerte " + queryElapsedTime + " Millisekunden. \nEs befinden sich " + rowCount + " Datensätze in der Datenbank.";
                    tvRowCount.setText(CountText);
                    // Überprüfen, ob die Datenbankabfrage erfolgreich war
                    if (task.isSuccessful()) {
                        TableLayout tableLayout = findViewById(R.id.tableLayout);

                        // Vorhandenen Inhalt löschen, wenn es nicht um einen Reset geht.
                        tableLayout.removeAllViews();

                        // Dokumente aus dem Abfrageergebnis extrahieren
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        List<Map<String, Object>> dataToSort = new ArrayList<>();
                        // Daten aus den Dokumenten extrahieren und in die Liste dataToSort einfügen
                        for (DocumentSnapshot document : documents) {
                            dataToSort.add(document.getData());
                        }

                        // Zurücksetzen des Schrittzählers für die Heapsort-Implementierung
                        DataSorterHeapsort.resetStepCount();
                        // Aufrufen der Heapsort-Sortierung
                        DataSorterHeapsort.sort(dataToSort);
                        // Anzahl der Schritte für die Heapsort-Sortierung abrufen
                        int heapsortSteps = DataSorterHeapsort.getStepCount();

                        // Anzeigen einer Nachricht mit der Anzahl der Schritte für die Sortierung.
                        Toast.makeText(this, "Sortierung abgeschlossen in " + heapsortSteps + " Schritten.", Toast.LENGTH_SHORT).show();

                        // Für jedes Datenobjekt in der Liste 'dataToSort' wird eine neue TableRow erstellt und mit Daten gefüllt.
                        for (Map<String, Object> data : dataToSort) {

                            // Eine neue Tabellenzeile wird durch den Inflater nach der Layout-Ressorce "R.layout:table_row_layout" erstellt.
                            TableRow dataRow = (TableRow) getLayoutInflater().inflate(R.layout.table_row_layout, null);

                            // Die Daten werden in die entsprechenden TextViews der TableRow gesetzt.
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
                            String formattedTimestamp = TimeStampFormat.convertTimestamp(timestamp); // Der Timestamp wird in das gewünschte Format konvertiert.
                            timestampTextView.setText((String) formattedTimestamp);

                            // Die erstellte TableRow wird zum TableLayout hinzugefügt.
                            tableLayout.addView(dataRow);
                        }
                    } else {
                        // Anzeigen einer Fehlermeldung, falls die Datenbankabfrage nicht erfolgreich war
                        Toast.makeText(getApplicationContext(), "Error getting data", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Methode zum Laden von Daten aus der Datenbank oder zum Zurücksetzen von Daten mit optionaler Umsortierung.
      */
    private void loadDataOrResetData(boolean isReset) {
        // Startzeit für die Datenbankabfrage setzen
        long queryStartTime = System.currentTimeMillis();
        // Referenzen zu den Textviews in der Benutzeroberfläche initialisieren
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Mitglieder"); // Titeltext kann hier entsprechend angepasst werden
        TextView tvRowCount = findViewById(R.id.tvRowCount);

        // Datenbankabfrage durchführen
        dbroot.collection("students")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Referenz zum TableLayout in der Benutzeroberfläche holen
                        TableLayout tableLayout = findViewById(R.id.tableLayout);
                        // Vorhandenen Inhalt löschen, wenn es nicht um einen Reset geht.
                        tableLayout.removeAllViews();

                        // Datenzeilen hinzufügen
                        for (DocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();

                            // Eine neue Tabellenzeile wird durch den Inflater erstellt.
                            TableRow dataRow = (TableRow) getLayoutInflater().inflate(R.layout.table_row_layout, null);

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
                            // Timestamp in das gewünschte Format konvertieren.
                            String formattedTimestamp = TimeStampFormat.convertTimestamp(timestamp);
                            timestampTextView.setText((String) formattedTimestamp);

                            // Die erstellte TableRow wird zum TableLayout hinzugefügt.
                            tableLayout.addView(dataRow);
                        }

                        // Anzahl der abgerufenen Datensätze
                        int rowCount = task.getResult().size();
                        // Endzeit für Datenbankabfrage setzen
                        long queryEndTime = System.currentTimeMillis();
                        // Zeit für Datenbankabfrage berechnen
                        long queryElapsedTime = queryEndTime - queryStartTime;
                        // Text für die Anzahl der Datensätze und die Zeit zur Benutzeroberfläche setzen
                        String countText = isReset ?
                                "Verbindungsaufbau und Umsortierung der Daten dauerte " + queryElapsedTime + " Millisekunden. \nEs befinden sich " + rowCount + " Datensätze in der Datenbank." :
                                "Initialisierung und Verbindungsaufbau zur Datenbank dauerte: " + queryElapsedTime + " Millisekunden. Davon hat die App zum Starten " + appInitializationTime + " Millisekunden benötigt. \nEs befinden sich " + rowCount + " Datensätze in der Datenbank.";
                        // Text in das TextView setzen
                        tvRowCount.setText(countText);

                    } else {
                        // Fehlermeldung anzeigen, wenn das Abrufen der Daten aus der Datenbank nicht erfolgreich ist.
                        Toast.makeText(getApplicationContext(), "Error getting data", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Diese Methode ruft die Methode `loadDataOrResetData` auf, um Daten aus der Datenbank zu laden,
     * ohne die vorhandenen Daten zu resetten.
     */
    public void loadData() {
        loadDataOrResetData(false);
    }

    /**
     * Diese Methode ruft die Methode `loadDataOrResetData` auf, um Daten aus der Datenbank zu laden
     * und dabei die vorhandenen Daten zurückzusetzen.
     */
    public void resetData() {
        loadDataOrResetData(true);
    }

}