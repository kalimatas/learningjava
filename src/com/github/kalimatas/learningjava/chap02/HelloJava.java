package com.github.kalimatas.learningjava.chap02;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class HelloJava
{
    public static void main(String[] args) {
        JFrame frame = new JFrame("Hello java again...");

        frame.add(new HelloComponent2("Hello java again..."));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(420, 180);
        frame.setVisible(true);
    }
}

class HelloComponent2 extends JComponent
    implements MouseMotionListener, ActionListener, Runnable
{
    String message;
    int messageX = 125, messageY = 84;

    JButton button;

    boolean isBlinking;

    int colorIndex;
    static Color[] colors = {Color.red, Color.green, Color.yellow, Color.blue};

    public HelloComponent2(String message) {
        this.setLayout(new FlowLayout());

        this.message = message;

        this.button = new JButton("Change color ebte!");
        this.button.addActionListener(this);
        this.add(this.button);

        addMouseMotionListener(this);

        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(isBlinking ? getBackground() : getCurrentColor());
        g.drawString(this.message, this.messageX, this.messageY);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.messageX = e.getX();
        this.messageY = e.getY();

        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.button) {
            changeColor();
        }
    }

    synchronized private void changeColor() {
        if (++colorIndex == colors.length) {
            colorIndex = 0;
        }

        setForeground(getCurrentColor());
        repaint();
    }

    synchronized private Color getCurrentColor() {
        return colors[colorIndex];
    }

    @Override
    public void run() {
        try {
            while (true) {
                isBlinking = !isBlinking;
                repaint();
                Thread.sleep(300);
            }
        } catch (InterruptedException e) {}
    }
}