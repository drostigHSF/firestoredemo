// Import-Anweisungen für benötigte Android- und Firebase-Klassen
package de.codeyourapp.theheapsort;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Diese Klasse stellt einen benutzerdefinierten GestureListener bereit, der auf Wischgesten reagiert.
 * In diesem Fall wird die Geste erkannt, wenn der Benutzer vom rechten Bildschirmrand zur Mitte wischt.
 */
public class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

    private final MainActivity mainActivity;

    /**
     * Konstruktor der Klasse.
     *
     * @param context Der Kontext, in dem der GestureListener erstellt wird.
     * @throws IllegalArgumentException Falls der übergebene Kontext nicht vom Typ MainActivity ist.
     */
    public SwipeGestureListener(Context context) {
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
        } else {
            throw new IllegalArgumentException("Context must be an instance of MainActivity");
        }
    }
    /**
     * Wird aufgerufen, wenn eine Wischgeste erkannt wird.
     *
     * @param e1        Das erste MotionEvent-Objekt (Startpunkt der Geste).
     * @param e2        Das zweite MotionEvent-Objekt (Endpunkt der Geste).
     * @param velocityX Die Geschwindigkeit in X-Richtung der Geste.
     * @param velocityY Die Geschwindigkeit in Y-Richtung der Geste.
     * @return true, wenn die Geste erfolgreich verarbeitet wurde; false, wenn nicht.
     */

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float deltaX = e2.getX() - e1.getX();
        float deltaY = e2.getY() - e1.getY();

        // Vergrößern des Startbereichs für die Geste, z.B. auf 300 Pixel vom rechten Bildschirmrand
        float startAreaWidth = 300;

        // Prüfen, ob die Geste vom rechten Bildschirmrand zur Mitte erfolgt
        if (Math.abs(deltaX) > Math.abs(deltaY) && e1.getX() > (mainActivity.getResources().getDisplayMetrics().widthPixels - startAreaWidth)) {
            // Wenn die Geste vom rechten Bildschirmrand zur Mitte erkannt wird, dann wird der Informationsblock angezeigt.
            mainActivity.showInformationBlock();
            return true;
        }
        return false;
    }
}
