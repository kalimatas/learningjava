package com.github.kalimatas.learningjava.chap02;

import javax.swing.*;
import java.awt.*;

public class HelloComponent extends JComponent
{
    public void paintComponent(Graphics g) {
        g.drawString("Hello java again", 125, 95);
    }
}
