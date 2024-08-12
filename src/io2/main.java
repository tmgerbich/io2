package io2;
import javax.swing.*;

public class main {
    public static void main(String[] args) {

        // Run the GUI creation on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUI();
            }
        });


    }
}
