// Import-Anweisungen für benötigte Android- und Firebase-Klassen
package de.codeyourapp.theheapsort;

import java.util.List;
import java.util.Map;

/**
 * Diese Klasse implementiert den Heapsort-Algorithmus zum Sortieren einer Liste von Maps.
 * Der Algorithmus wird auf die gegebene Liste angewendet und modifiziert sie direkt.
 *
 * Beachte: Diese Implementierung geht davon aus, dass die Daten in der Liste einen numerischen Schlüssel namens "id" haben.
 * Der Vergleich und das Sortieren erfolgen anhand dieses numerischen Schlüssels.
 */
public class DataSorterHeapsort {

    private static int stepCount = 0;

    /**
     * Diese Methode initiiert den Heapsort-Algorithmus auf der gegebenen Liste von Daten.
     * Der Algorithmus wird verwendet, um die Daten nach der numerischen "id" zu sortieren.
     *
     * @param dataToSort Die zu sortierende Liste von Daten (Maps).
     */
    public static void sort(List<Map<String, Object>> dataToSort) {
        resetStepCount(); // Zurücksetzen des Schritt-Zählers
        int n = dataToSort.size();

        // Erzeugen eines Heaps aus der gegebenen Liste
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(dataToSort, n, i);
        }

        // Extrahieren der Elemente aus dem Heap und Sortieren der Liste
        for (int i = n - 1; i > 0; i--) {
            swap(dataToSort, 0, i);
            heapify(dataToSort, i, 0);
        }
    }

    /**
     * Diese Methode passt den Heap für die gegebene Liste von Daten an.
     *
     * @param dataToSort Die zu sortierende Liste von Daten (Maps).
     * @param n           Die Größe des Heaps.
     * @param i           Der Index des zu überprüfenden Elements im Heap.
     */
    private static void heapify(List<Map<String, Object>> dataToSort, int n, int i) {
        // Initialisieren des größten Index mit dem aktuellen Index
        int largest = i;
        // Berechnen der Indizes der linken und rechten Kinder
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        // Überprüfen, ob das linke Kind existiert und größer als das aktuelle Element ist
        if (left < n && compare(dataToSort, left, largest) > 0) {
            largest = left;
        }

        // Überprüfen, ob das rechte Kind existiert und größer als das aktuelle Element oder das linke Kind ist
        if (right < n && compare(dataToSort, right, largest) > 0) {
            largest = right;
        }

        // Wenn der größte Index nicht mehr der aktuelle Index ist, werden die Elemente getauscht und der Heap angepasst
        if (largest != i) {
            swap(dataToSort, i, largest);
            heapify(dataToSort, n, largest);
        }

        // Inkrementieren des Schritt-Zählers nach jedem Schritt im Heapify-Prozess
        incrementStepCount();
    }

    /**
     * Vergleicht zwei Elemente in der Liste an den gegebenen Indizes basierend auf dem "id"-Attribut.
     *
     * @param dataToSort Die zu sortierende Liste von Daten (Maps).
     * @param i           Der Index des ersten Elements in der Liste.
     * @param j           Der Index des zweiten Elements in der Liste.
     * @return            Ein negatives Integer, wenn das erste Element kleiner ist,
     *                    ein positives Integer, wenn das zweite Element kleiner ist,
     *                    andernfalls 0, wenn die Elemente gleich sind.
     */
    private static int compare(List<Map<String, Object>> dataToSort, int i, int j) {
        Comparable<Object> obj1;
        obj1 = (Comparable<Object>) dataToSort.get(i).get("id");
        Comparable<Object> obj2;
        obj2 = (Comparable<Object>) dataToSort.get(j).get("id");

        // Vergleich der beiden Objekte basierend auf dem "id"-Attribut
        return obj1.compareTo(obj2);
    }

    /**
     * Tauscht zwei Elemente in der Liste an den gegebenen Indizes miteinander aus.
     *
     * @param dataToSort Die zu sortierende Liste von Daten (Maps).
     * @param i           Der Index des ersten Elements in der Liste.
     * @param j           Der Index des zweiten Elements in der Liste.
     */
    private static void swap(List<Map<String, Object>> dataToSort, int i, int j) {
        // Temporäre Variable, um das Element an Position i zu speichern
        Map<String, Object> temp = dataToSort.get(i);
        // Setzen des Elementes an Position i auf das Element an Position j
        dataToSort.set(i, dataToSort.get(j));
        // Setzen des Elementes an Position j auf das zuvor gespeicherte Element
        dataToSort.set(j, temp);
    }

    /**
     * Gibt die Anzahl der Schritte zurück, die während des Heapsort-Sortiervorgangs durchgeführt wurden.
     * Diese Schritte entsprechen den Operationen im Algorithmus, wie Vergleichen, Vertauschen und
     * Anpassen der Elemente im Heap.
     *
     * @return Die Anzahl der Schritte im Heapsort-Sortiervorgang.
     */
    public static int getStepCount() {
        return stepCount;
    }

    /**
     * Setzt die Anzahl der Schritte auf null zurück. Diese Methode wird verwendet, um die Schrittzählung
     * vor einem neuen Heapsort-Sortiervorgang zu starten.
     */
    public static void resetStepCount() {
        stepCount = 0;
    }

    /**
     * Inkrementiert die Schrittzählung um eins. Diese Methode wird nach jedem Schritt im Heapsort-Algorithmus
     * aufgerufen, um die Anzahl der durchgeführten Schritte zu zählen.
     */
    private static void incrementStepCount() {
        stepCount++;
    }
}