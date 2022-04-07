package com.company;

import java.awt.*;
import javax.swing.*;

public class SnowEater extends JFrame {

    public SnowEater() {

        initUI();
    }

    private void initUI() {

        //ustawiam ikonę okna
        ImageIcon ic = new ImageIcon("src/resources/kopara_G.png");
        Image winicn = ic.getImage();
        setIconImage(winicn);

        //podpinam planszę
        add(new Board());

        setResizable(false);
        //pack sprawia że okno ma rozmiar z Board.java
        pack();

        setTitle("Odśnieżara");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            JFrame ex = new SnowEater();
            ex.setVisible(true);
        });
    }
}
