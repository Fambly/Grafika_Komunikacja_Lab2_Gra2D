package com.company;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    boolean DEBUG = false;

//    Skalowanie - zwiększa rozmiar jednego pola i rozdzielczość tekstur.
//    Wybierz między 1, 2 lub 3.
    protected final int SCALE = 2;

    private final int B_WIDTH = 320 * SCALE;
    private final int B_HEIGHT = 320 * SCALE;
    private final int DOT_SIZE = 16 * SCALE;
    private final int DELAY = 100;

    //Reprezentacja odśnieżarki
    private final int[] x = new int[1];
    private final int[] y = new int[1];

    //inicjalizuję miejsca na planszy gdzie będę mógł wstawić śnieg do odśnieżania
    private final int[] xt = new int[20];
    private final int[] yt = new int[18];

    //tablica 2d z odśnieżonymi polami, ustawionymi na 0
    private final int[][] Clrki = new int[20][18];
    private int score;

    //koordynaty przeszkód
    private int lamp1_x;
    private int lamp1_y;
    private int lamp2_x;
    private int lamp2_y;
    private int lamp3_x;
    private int lamp3_y;
    private int lamp4_x;
    private int lamp4_y;
    private int SM1_x;
    private int SM1_y;
    private int SM2_x;
    private int SM2_y;
    private int obs11_x;
    private int obs11_y;
    private int obs12_x;
    private int obs12_y;
    private int obs13_x;
    private int obs13_y;
    private int obs21_x;
    private int obs21_y;
    private int obs22_x;
    private int obs22_y;
    private int pot_x;
    private int pot_y;
    private int wire_x;
    private int wire_y;

    //sprawdzanie który przycisk jest wciśnięty
    private boolean leftDirection = false;
    private boolean rightDirection = false;
    private boolean upDirection = false;
    private boolean downDirection = false;

    //będę tego używał jako zapamiętania ostatniej pozycji, że np 0 to lewo, 1 prawo, itd.
    private int last;

    //sprawdza czy gra się skończyła
    private boolean inGame = true;

    private Timer timer;

    //inicjalizacja tekstur
    private Image clear;
    private Image kopG;
    private Image kopD;
    private Image kopL;
    private Image kopP;
    private Image obst1;
    private Image obst2;
    private Image lamp;
    private Image snwman;
    private Image tile;
    private Image pot;
    private Image wire;
    private Image icon;

    public Board() {
        initBoard();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.white);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));

        loadImages();
        initGame();
    }

    private void loadImages() {

        ImageIcon kg = null, kd = null, kl = null, kp = null, lp = null, o1 = null, o2 = null, snm = null, tl = null, pt = null, wr = null, clr = null;
        //w zależności od skali ładuję tekstury:
        switch (SCALE) {
            case 1 -> {
                kg = new ImageIcon("src/resources/kopara_G.png");
                kd = new ImageIcon("src/resources/kopara_D.png");
                kl = new ImageIcon("src/resources/kopara_L.png");
                kp = new ImageIcon("src/resources/kopara_P.png");
                lp = new ImageIcon("src/resources/lamp.png");
                o1 = new ImageIcon("src/resources/obst1.png");
                o2 = new ImageIcon("src/resources/obs2.png");
                snm = new ImageIcon("src/resources/snowman.png");
                tl = new ImageIcon("src/resources/Tile.png");
                pt = new ImageIcon("src/resources/pot.png");
                wr = new ImageIcon("src/resources/wirewall.png");
                clr = new ImageIcon("src/resources/Cleared.png");
            }
            case 2 -> {
                kg = new ImageIcon("src/resources/kopara_G_S2.png");
                kd = new ImageIcon("src/resources/kopara_D_S2.png");
                kl = new ImageIcon("src/resources/kopara_L_S2.png");
                kp = new ImageIcon("src/resources/kopara_P_S2.png");
                lp = new ImageIcon("src/resources/lamp_S2.png");
                o1 = new ImageIcon("src/resources/obst1_S2.png");
                o2 = new ImageIcon("src/resources/obs2_S2.png");
                snm = new ImageIcon("src/resources/snowman_S2.png");
                tl = new ImageIcon("src/resources/Tile_S2.png");
                pt = new ImageIcon("src/resources/pot_S2.png");
                wr = new ImageIcon("src/resources/wirewall_S2.png");
                clr = new ImageIcon("src/resources/Cleared_S2.png");
            }
            case 3 -> {
                kg = new ImageIcon("src/resources/kopara_G_S3.png");
                kd = new ImageIcon("src/resources/kopara_D_S3.png");
                kl = new ImageIcon("src/resources/kopara_L_S3.png");
                kp = new ImageIcon("src/resources/kopara_P_S3.png");
                lp = new ImageIcon("src/resources/lamp_S3.png");
                o1 = new ImageIcon("src/resources/obst1_S3.png");
                o2 = new ImageIcon("src/resources/obs2_S3.png");
                snm = new ImageIcon("src/resources/snowman_S3.png");
                tl = new ImageIcon("src/resources/Tile_S3.png");
                pt = new ImageIcon("src/resources/pot_S3.png");
                wr = new ImageIcon("src/resources/wirewall_S3.png");
                clr = new ImageIcon("src/resources/Cleared_S3.png");
            }
            default -> {
                System.out.println("Błąd zmiennej SCALE.");
                System.exit(2);
            }
        }
        kopG = kg.getImage();
        kopD = kd.getImage();
        kopL = kl.getImage();
        kopP = kp.getImage();
        lamp = lp.getImage();
        obst1 = o1.getImage();
        obst2 = o2.getImage();
        snwman = snm.getImage();
        tile = tl.getImage();
        pot = pt.getImage();
        wire = wr.getImage();
        clear = clr.getImage();
        icon = kg.getImage();
    }

    private void initGame() {

        setStatProps();

//          ustawiam pozycję początkową odśnieżarki
            x[0] = 0;
            y[0] = DOT_SIZE;

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void setStatProps(){

        //ustawiam koordynaty zmiennych statycznych
        pot_x = 0;
        pot_y = 0;
        wire_x = 0;
        wire_y = B_HEIGHT-DOT_SIZE;
        lamp1_x = DOT_SIZE*5;
        lamp1_y = DOT_SIZE*5;
        lamp2_x = DOT_SIZE*14;
        lamp2_y = DOT_SIZE*5;
        lamp3_x = DOT_SIZE*5;
        lamp3_y = DOT_SIZE*14;
        lamp4_x = DOT_SIZE*14;
        lamp4_y = DOT_SIZE*14;
        SM1_x = DOT_SIZE*16;
        SM1_y = DOT_SIZE*3;
        SM2_x = DOT_SIZE*8;
        SM2_y = DOT_SIZE*9;
        obs11_x = DOT_SIZE*4;
        obs11_y = DOT_SIZE*6;
        obs12_x = DOT_SIZE*4;
        obs12_y = DOT_SIZE*10;
        obs13_x = DOT_SIZE*13;
        obs13_y = DOT_SIZE*7;
        obs21_x = DOT_SIZE*6;
        obs21_y = DOT_SIZE*9;
        obs22_x = DOT_SIZE*15;
        obs22_y = DOT_SIZE*11;

        for (int i = 0; i < 20; i++) {
            xt[i] = i*DOT_SIZE;
        }
        for (int i = 0; i < 18; i++) {
            yt[i] = (i+1)*DOT_SIZE;
        }

    }



    private void doDrawing(Graphics g) {

        //rysowanie komponentów:
        //siatki
        g.drawImage(wire, wire_x, wire_y, this);
        //długiej donicy z rośnilami
        g.drawImage(pot, pot_x, pot_y, this);

        //śniegu do odśnieżania
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 20; j++) {
                g.drawImage(tile,xt[j],yt[i],this);
            }
        }

        //informacje dla mnie przy debugowaniu
        if (DEBUG) {
            System.out.println("x: " + x[0] + " y: " + y[0]);
            System.out.println(x[0] / DOT_SIZE);
            System.out.println(y[0] / DOT_SIZE - 1);
        }

        //jeśli pozycja koparki w tablicy Clrki jest 0 to zmienia ją na 1
        if (Clrki[x[0]/DOT_SIZE][y[0]/DOT_SIZE-1] == 0){
            if (DEBUG)
            {
                System.out.println(x[0] / DOT_SIZE);
                System.out.println(y[0] / DOT_SIZE - 1);
            }
            Clrki[x[0]/DOT_SIZE][y[0]/DOT_SIZE-1] = 1;
        }

        //jeśli Clrki jest 1 to rysuję teksturę odśnieżoną i dodaję punkty
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 20; j++) {
                if (Clrki[j][i] == 1){
                    g.drawImage(clear,j*DOT_SIZE,i*DOT_SIZE+DOT_SIZE,this);
                    score++;
                }
            }
        }

        //jak wynik będzie = maxymalnej ilości pól do odśnieżenia to kończy grę
        if (score == 349) {
            inGame = false;
            gameOver(g);
        }

        //wyswietlanie wyniku
        showScore(g);
        if (DEBUG)
        System.out.println("WYNIK: "+score);
        score = 0;

        //rysowanie obiektów
        g.drawImage(lamp,lamp1_x,lamp1_y,this);
        g.drawImage(lamp,lamp2_x,lamp2_y,this);
        g.drawImage(lamp,lamp3_x,lamp3_y,this);
        g.drawImage(lamp,lamp4_x,lamp4_y,this);
        g.drawImage(snwman,SM1_x,SM1_y,this);
        g.drawImage(snwman,SM2_x,SM2_y,this);
        g.drawImage(obst1,obs11_x,obs11_y,this);
        g.drawImage(obst1,obs12_x,obs12_y,this);
        g.drawImage(obst1,obs13_x,obs13_y,this);
        g.drawImage(obst2,obs21_x,obs21_y,this);
        g.drawImage(obst2,obs22_x,obs22_y,this);

        //tu wykorzystuję int last. Zapamiętuje on ostatni klawisz i zostawia tak nam koparkę.
        if (inGame) {
            if (leftDirection) {
                g.drawImage(kopL, x[0], y[0], this);
                last = 1;
            }
            if (rightDirection) {
                g.drawImage(kopP, x[0], y[0], this);
                last = 0;
            }
            if (upDirection) {
                g.drawImage(kopG, x[0], y[0], this);
                last = 2;
            }
            if (downDirection) {
                g.drawImage(kopD, x[0], y[0], this);
                last = 3;
            }
            else {
                switch (last){
                    case 0:
                        g.drawImage(kopP, x[0], y[0], this);
                        break;
                    case 1:
                        g.drawImage(kopL, x[0], y[0], this);
                        break;
                    case 2:
                        g.drawImage(kopG, x[0], y[0], this);
                        break;
                    case 3:
                        g.drawImage(kopD, x[0], y[0], this);
                        break;
                }
            }

            Toolkit.getDefaultToolkit().sync();

        } else {
            gameOver(g);
        }
    }

    //metoda służąca do pokazywania wyniku w %
    private void showScore(Graphics g){
        double prc = Math.round(score/349.0*100);
        String scr = prc + "%";
        if (DEBUG)
        System.out.println(scr);

        Font scft = new Font("Helvetica", Font.BOLD, 8*SCALE);

        g.setColor(Color.lightGray);
        g.fillRect(17*DOT_SIZE,0,3*DOT_SIZE, (int) (0.7*DOT_SIZE));
        g.setColor(Color.blue);
        g.setFont(scft);
        g.drawString(scr, 17*DOT_SIZE, DOT_SIZE/2);
    }

    private void gameOver(Graphics g){
        String msg = "Game won";
        Font small = new Font("Helvetica", Font.BOLD, 14*SCALE);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.setColor(Color.gray);
        g.fillRect((B_WIDTH - metr.stringWidth(msg))/2,(B_HEIGHT - 2* DOT_SIZE)/2,metr.stringWidth(msg), (int) (DOT_SIZE+0.5*DOT_SIZE));
        g.setColor(Color.cyan);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
    }

    //poruszanie się
    private void move() {
        if ((leftDirection) && (x[0]!=0)) {
            x[0] -= DOT_SIZE;
        }

        if ((rightDirection) && (x[0]!=B_WIDTH-DOT_SIZE)) {
            x[0] += DOT_SIZE;
        }

        if ((upDirection) && (y[0]!=DOT_SIZE)) {
            y[0] -= DOT_SIZE;
        }

        if ((downDirection) && (y[0]!=B_HEIGHT-2*DOT_SIZE)) {
            y[0] += DOT_SIZE;
        }
    }

    private void checkCollision() {

        //Wypisywanie kolizji. Co prawda ponoć można byłoby zrobić klasę sprite i wszystko ładnie krótko zrobić,
//        ale doszedłem do tego jak już tylko kolizji mi brakowało do ukończenia gry, więc zostałem przy
//        prymitywnym ctrl c ctrl v
        if (x[0] == lamp1_x + DOT_SIZE && y[0] == lamp1_y && leftDirection){
            x[0] = lamp1_x + 2*DOT_SIZE;
        }
        if (x[0] == lamp1_x - DOT_SIZE && y[0] == lamp1_y && rightDirection){
            x[0] = lamp1_x - 2*DOT_SIZE;
        }
        if (x[0] == lamp1_x && y[0] == lamp1_y - DOT_SIZE && downDirection){
            y[0] = lamp1_y - 2*DOT_SIZE;
        }
        if (x[0] == lamp1_x && y[0] == lamp1_y + DOT_SIZE && upDirection){
            y[0] = lamp1_y + 2*DOT_SIZE;
        }

        if (x[0] == lamp2_x + DOT_SIZE && y[0] == lamp2_y && leftDirection){
            x[0] = lamp2_x + 2*DOT_SIZE;
        }
        if (x[0] == lamp2_x - DOT_SIZE && y[0] == lamp2_y && rightDirection){
            x[0] = lamp2_x - 2*DOT_SIZE;
        }
        if (x[0] == lamp2_x && y[0] == lamp2_y - DOT_SIZE && downDirection){
            y[0] = lamp2_y - 2*DOT_SIZE;
        }
        if (x[0] == lamp2_x && y[0] == lamp2_y + DOT_SIZE && upDirection){
            y[0] = lamp2_y + 2*DOT_SIZE;
        }

        if (x[0] == lamp3_x + DOT_SIZE && y[0] == lamp3_y && leftDirection){
            x[0] = lamp3_x + 2*DOT_SIZE;
        }
        if (x[0] == lamp3_x - DOT_SIZE && y[0] == lamp3_y && rightDirection){
            x[0] = lamp3_x - 2*DOT_SIZE;
        }
        if (x[0] == lamp3_x && y[0] == lamp3_y - DOT_SIZE && downDirection){
            y[0] = lamp3_y - 2*DOT_SIZE;
        }
        if (x[0] == lamp3_x && y[0] == lamp3_y + DOT_SIZE && upDirection){
            y[0] = lamp3_y + 2*DOT_SIZE;
        }

        if (x[0] == lamp4_x + DOT_SIZE && y[0] == lamp4_y && leftDirection){
            x[0] = lamp4_x + 2*DOT_SIZE;
        }
        if (x[0] == lamp4_x - DOT_SIZE && y[0] == lamp4_y && rightDirection){
            x[0] = lamp4_x - 2*DOT_SIZE;
        }
        if (x[0] == lamp4_x && y[0] == lamp4_y - DOT_SIZE && downDirection){
            y[0] = lamp4_y - 2*DOT_SIZE;
        }
        if (x[0] == lamp4_x && y[0] == lamp4_y + DOT_SIZE && upDirection){
            y[0] = lamp4_y + 2*DOT_SIZE;
        }

        if (x[0] == SM1_x + DOT_SIZE && y[0] == SM1_y && leftDirection){
            x[0] = SM1_x + 2*DOT_SIZE;
        }
        if (x[0] == SM1_x - DOT_SIZE && y[0] == SM1_y && rightDirection){
            x[0] = SM1_x - 2*DOT_SIZE;
        }
        if (x[0] == SM1_x && y[0] == SM1_y - DOT_SIZE && downDirection){
            y[0] = SM1_y - 2*DOT_SIZE;
        }
        if (x[0] == SM1_x && y[0] == SM1_y + DOT_SIZE && upDirection){
            y[0] = SM1_y + 2*DOT_SIZE;
        }

        if (x[0] == SM2_x + DOT_SIZE && y[0] == SM2_y && leftDirection){
            x[0] = SM2_x + 2*DOT_SIZE;
        }
        if (x[0] == SM2_x - DOT_SIZE && y[0] == SM2_y && rightDirection){
            x[0] = SM2_x - 2*DOT_SIZE;
        }
        if (x[0] == SM2_x && y[0] == SM2_y - DOT_SIZE && downDirection){
            y[0] = SM2_y - 2*DOT_SIZE;
        }
        if (x[0] == SM2_x && y[0] == SM2_y + DOT_SIZE && upDirection){
            y[0] = SM2_y + 2*DOT_SIZE;
        }

        if (x[0] == obs11_x + DOT_SIZE && y[0] == obs11_y && leftDirection){
            x[0] = obs11_x + 2*DOT_SIZE;
        }
        if (x[0] == obs11_x - DOT_SIZE && y[0] == obs11_y && rightDirection){
            x[0] = obs11_x - 2*DOT_SIZE;
        }
        if (x[0] == obs11_x && y[0] == obs11_y - DOT_SIZE && downDirection){
            y[0] = obs11_y - 2*DOT_SIZE;
        }
        if (x[0] == obs11_x && y[0] == obs11_y + DOT_SIZE && upDirection){
            y[0] = obs11_y + 2*DOT_SIZE;
        }

        if (x[0] == obs12_x + DOT_SIZE && y[0] == obs12_y && leftDirection){
            x[0] = obs12_x + 2*DOT_SIZE;
        }
        if (x[0] == obs12_x - DOT_SIZE && y[0] == obs12_y && rightDirection){
            x[0] = obs12_x - 2*DOT_SIZE;
        }
        if (x[0] == obs12_x && y[0] == obs12_y - DOT_SIZE && downDirection){
            y[0] = obs12_y - 2*DOT_SIZE;
        }
        if (x[0] == obs12_x && y[0] == obs12_y + DOT_SIZE && upDirection) {
            y[0] = obs12_y + 2 * DOT_SIZE;
        }

        if (x[0] == obs13_x + DOT_SIZE && y[0] == obs13_y && leftDirection){
            x[0] = obs13_x + 2*DOT_SIZE;
        }
        if (x[0] == obs13_x - DOT_SIZE && y[0] == obs13_y && rightDirection){
            x[0] = obs13_x - 2*DOT_SIZE;
        }
        if (x[0] == obs13_x && y[0] == obs13_y - DOT_SIZE && downDirection){
            y[0] = obs13_y - 2*DOT_SIZE;
        }
        if (x[0] == obs13_x && y[0] == obs13_y + DOT_SIZE && upDirection) {
            y[0] = obs13_y + 2 * DOT_SIZE;
        }

        if (x[0] == obs21_x + DOT_SIZE && y[0] == obs21_y && leftDirection){
            x[0] = obs21_x + 2*DOT_SIZE;
        }
        if (x[0] == obs21_x - DOT_SIZE && y[0] == obs21_y && rightDirection){
            x[0] = obs21_x - 2*DOT_SIZE;
        }
        if (x[0] == obs21_x && y[0] == obs21_y - DOT_SIZE && downDirection){
            y[0] = obs21_y - 2*DOT_SIZE;
        }
        if (x[0] == obs21_x && y[0] == obs21_y + DOT_SIZE && upDirection) {
            y[0] = obs21_y + 2 * DOT_SIZE;
        }

        if (x[0] == obs22_x + DOT_SIZE && y[0] == obs22_y && leftDirection){
            x[0] = obs22_x + 2*DOT_SIZE;
        }
        if (x[0] == obs22_x - DOT_SIZE && y[0] == obs22_y && rightDirection){
            x[0] = obs22_x - 2*DOT_SIZE;
        }
        if (x[0] == obs22_x && y[0] == obs22_y - DOT_SIZE && downDirection){
            y[0] = obs22_y - 2*DOT_SIZE;
        }
        if (x[0] == obs22_x && y[0] == obs22_y + DOT_SIZE && upDirection) {
            y[0] = obs22_y + 2 * DOT_SIZE;
        }



        if (y[0] >= B_HEIGHT-2*DOT_SIZE) {
            y[0] = B_HEIGHT-2*DOT_SIZE;
        }

        if (y[0] <= DOT_SIZE) {
            y[0] = DOT_SIZE;
        }

        if (x[0] >= B_WIDTH) {
            x[0] = B_WIDTH;
        }

        if (x[0] < 0) {
            x[0] = 0;
        }

        if (!inGame) {
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {
            checkCollision();
            move();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }

        @Override
        public void keyReleased(KeyEvent e){
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_LEFT
            || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_UP){
                downDirection = false;
                upDirection = false;
                leftDirection = false;
                rightDirection = false;
            }
        }
    }
}
