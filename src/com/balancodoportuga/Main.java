package com.balancodoportuga;

import com.balancodoportuga.ui.menu.LoginTela;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
   

    SwingUtilities.invokeLater(() -> new LoginTela().setVisible(true));
    
}
}
