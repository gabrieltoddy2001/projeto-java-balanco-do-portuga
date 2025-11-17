package com.balancodoportuga.util;

import javax.swing.*;
import java.time.LocalDate;

public class ValidadorReserva {

    public static boolean validar(LocalDate inicio, LocalDate fim) {
        if(inicio == null || fim == null) {
            JOptionPane.showMessageDialog(null, "Datas inválidas!");
            return false;
        }

        if(fim.isBefore(inicio)) {
            JOptionPane.showMessageDialog(null, "Data de fim não pode ser antes da data de início!");
            return false;
        }

        return true;
    }
}
