// Import-Anweisungen für benötigte Android- und Firebase-Klassen
package de.codeyourapp.theheapsort;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Die Klasse `DataAddEntryDialog` repräsentiert einen Dialog zur Hinzufügung von Einträgen.
 * Sie wird wahrscheinlich verwendet, um Benutzereingaben für neue Datensätze zu erfassen.
 */
public class DataAddEntryDialog {
    // Instanzvariablen, die die MainActivity und die Zugriffsmöglichkeit auf die Firestore-Datenbank speichern
    private final MainActivity mainActivity;
    private final FirebaseFirestore dbroot;

    // Die folgenden Variablen speichern Referenzen zu User-Interface-Elementen im Dialog, z.B., Textfelder und Fehlermeldungen
    private EditText etName;
    private EditText etVorname;
    private EditText etEmail;
    private EditText etID;
    private TextView tvErrorName;
    private TextView tvErrorVorname;
    private TextView tvErrorEmail;
    private TextView tvErrorID;

    /**
     * Konstruktor der Klasse. Hier werden die Referenzen auf die MainActivity und die Firestore-Datenbank
     * initialisiert.
     *
     * @param mainActivity Die MainActivity, von der aus der Dialog aufgerufen wird.
     * @param dbroot Die Firestore-Datenbankreferenz, mit der auf die Datenbank zugegriffen wird.
     */
    public DataAddEntryDialog(MainActivity mainActivity, FirebaseFirestore dbroot) {
        // Zuweisen der übergebenen Werte an die entsprechenden Instanzvariablen
        this.mainActivity = mainActivity;
        this.dbroot = dbroot;
    }

    /**
     * Zeigt einen Dialog zum Hinzufügen eines neuen Eintrags an.
     */
    public void showAddEntryDialog() {
        // Layout-Inflater wird instanziiert, um das Dialoglayout zu erstellen
        LayoutInflater inflater = LayoutInflater.from(mainActivity);
        View dialogView = inflater.inflate(R.layout.dialog_add_entry, null);

        // Initialisierung der EditText-Variablen für die Benutzereingabe
        etName = dialogView.findViewById(R.id.etName);
        etVorname = dialogView.findViewById(R.id.etVorname);
        etEmail = dialogView.findViewById(R.id.etEmail);
        etID = dialogView.findViewById(R.id.etID);

        // Initialisierung der TextView-Variablen für Fehlermeldungen
        tvErrorName = dialogView.findViewById(R.id.tvErrorName);
        tvErrorVorname = dialogView.findViewById(R.id.tvErrorVorname);
        tvErrorEmail = dialogView.findViewById(R.id.tvErrorEmail);
        tvErrorID = dialogView.findViewById(R.id.tvErrorID);

        // Zufällige ID generieren und setzen
        final EditText etID = dialogView.findViewById(R.id.etID);
        etID.setText(String.valueOf(generateRandomID()));

        // AlertDialog.Builder erstellen und Dialog konfigurieren
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setView(dialogView)
                .setTitle("Eintrag hinzufügen")
                .setPositiveButton("Hinzufügen", null)
                .setNegativeButton("Abbrechen", (dialog, which) -> dialog.dismiss());

        // AlertDialog erstellen und anzeigen
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Den Standard-PositiveButton-ClickListener überschreiben
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            // Benutzereingaben aus den EditText-Feldern abrufen
            String name = etName.getText().toString().trim();
            String vorname = etVorname.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String idText = etID.getText().toString().trim();

            // Setzen von Fehlermeldungen standardmäßig auf unsichtbar
            tvErrorName.setVisibility(View.GONE);
            tvErrorVorname.setVisibility(View.GONE);
            tvErrorEmail.setVisibility(View.GONE);
            tvErrorID.setVisibility(View.GONE);

            // Überprüfen der Validität der Benutzereingaben
            if (isValidData(name, vorname, email, idText)) {
                alertDialog.dismiss();
                insertData(name, vorname, email, idText);
            } else {
                // Fehlermeldungen anzeigen und das Dialogfeld offenhalten
                if (name.isEmpty()) { // Überprüfe, ob der Name leer ist
                    tvErrorName.setVisibility(View.VISIBLE); // Wenn ja, setze die Sichtbarkeit der Fehlermeldung für den Namen auf "sichtbar"
                }
                if (vorname.isEmpty()) { // Überprüfe, ob der Vorname leer ist
                    tvErrorVorname.setVisibility(View.VISIBLE); // Wenn ja, setze die Sichtbarkeit der Fehlermeldung für den Vorname auf "sichtbar"
                }
                if (email.isEmpty() || !isValidEmail(email)) { // Überprüfe, ob die E-Mail leer ist oder keine gültige E-Mail-Adresse ist
                    tvErrorEmail.setVisibility(View.VISIBLE); // Wenn ja, setze die Sichtbarkeit der Fehlermeldung für die E-Mail auf "sichtbar"
                }
                if (idText.isEmpty()) { // Überprüfe, ob die ID leer ist
                    tvErrorID.setVisibility(View.VISIBLE); // Wenn ja, setze die Sichtbarkeit der Fehlermeldung für die ID auf "sichtbar"
                }
            }
        });
    }

    /**
     * Generiert eine zufällige fünfstellige ID.
     *
     * @return Eine zufällige fünfstellige ID.
     */
    private int generateRandomID() {
        // Erstellen einer Instanz der Random-Klasse
        Random random = new Random();
        // Generieren einer zufällige Zahl im Bereich von 10000 bis (10000 + 99999)
        // Das Ergebnis ist eine fünfstellige Zahl
        return 10000 + random.nextInt(99999); // Generieren einer fünfstelligen Zahl
    }

    /**
     * Überprüft die Gültigkeit der eingegebenen Daten.
     *
     * @param name    Der eingegebene Name.
     * @param vorname Der eingegebene Vorname.
     * @param email   Die eingegebene E-Mail-Adresse.
     * @param id      Die eingegebene ID.
     * @return True, wenn alle Daten gültig sind, andernfalls False.
     */
    private boolean isValidData(String name, String vorname, String email, String id) {
        // Überprüfen, ob alle Felder ausgefüllt sind
        if (name.isEmpty() || vorname.isEmpty() || email.isEmpty() || id.isEmpty()) {
            return false;
        }
        // Überprüfen, ob die E-Mail einem bestimmten Muster entspricht
        return isValidEmail(email);
    }

    /**
     * Überprüft die Gültigkeit einer E-Mail-Adresse anhand eines verbesserten regulären Ausdrucks.
     *
     * @param email Die zu überprüfende E-Mail-Adresse.
     * @return True, wenn die E-Mail-Adresse gültig ist, andernfalls False.
     */
    private boolean isValidEmail(String email) {
        // Verbesserter regulärer Ausdruck für die E-Mail-Validierung
        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email.matches(emailPattern);
    }

    /**
     * Fügt Daten zur Firestore-Datenbank hinzu.
     *
     * @param name    Der Name für den neuen Eintrag.
     * @param vorname Der Vorname für den neuen Eintrag.
     * @param email   Die E-Mail für den neuen Eintrag.
     * @param id      Die ID für den neuen Eintrag.
     */
    private void insertData(String name, String vorname, String email, String id) {
        // Erstellen einer Map, um die Daten für den neuen Eintrag zu speichern
        Map<String, Object> items = new HashMap<>();
        items.put("name", name);
        items.put("vorname", vorname);
        items.put("email", email);
        items.put("id", id);

        // Erzeugen eines Timestamps im Format "yyyy-MM-dd HH:mm:ss" und Hinzufügen zur Map
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTimeStamp = dateFormat.format(Calendar.getInstance().getTime());
        items.put("timestamp", currentTimeStamp);

        // Hinzufügen der Map zur Firestore-Datenbank
        dbroot.collection("students").add(items)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Zeigen einer Erfolgsmeldung und Neuladen der Daten
                        Toast.makeText(this.mainActivity, "Eintrag erfolgreich hinzugefügt", Toast.LENGTH_LONG).show();
                        mainActivity.resetData(); // Nach dem Hinzufügen die Daten neu laden
                    } else {
                        // Zeigen einer Fehlermeldung, falls das Hinzufügen fehlschlägt
                        Toast.makeText(this.mainActivity, "Fehler beim Hinzufügen des Eintrags", Toast.LENGTH_LONG).show();
                    }
                });
    }

}