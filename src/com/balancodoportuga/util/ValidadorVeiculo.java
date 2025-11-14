package com.balancodoportuga.util;

import javax.swing.*;
import java.time.Year;
import java.util.regex.Pattern;

/**
 * Classe utilitária responsável pela validação de dados de veículos.
 * Utilizada nas camadas Service e Controller antes de persistir alterações no banco.
 */
public class ValidadorVeiculo {

    /**
     * 🔹 Valida todos os campos principais de um veículo.
     *
     * @param modelo Modelo do veículo
     * @param marca  Marca do veículo
     * @param placa  Placa (antiga ou Mercosul)
     * @param anoStr Ano (string numérica)
     * @return true se todos os campos forem válidos
     */
    public static boolean validar(String modelo, String marca, String placa, String anoStr) {
       
// --- Placa ---
if (placa == null || placa.isBlank()) {
    JOptionPane.showMessageDialog(null, "O campo 'Placa' é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
    return false;
}

// 🔧 remove lixo da máscara (underscores e espaços)
placa = placa.toUpperCase().trim().replaceAll("[_\\s]", "");

// Formatos válidos: antigo (ABC-1234) e Mercosul (ABC1D23)
String placaRegexAntiga = "^[A-Z]{3}-\\d{4}$";
String placaRegexMercosul = "^[A-Z]{3}\\d[A-Z]\\d{2}$";

if (!Pattern.matches(placaRegexAntiga, placa) && !Pattern.matches(placaRegexMercosul, placa)) {
    JOptionPane.showMessageDialog(null,
            "Formato de placa inválido!\nUse:\n• ABC-1234 (modelo antigo)\n• ABC1D23 (padrão Mercosul)",
            "Validação de Placa", JOptionPane.WARNING_MESSAGE);
    return false;
}



        // --- Ano ---
        int anoAtual = Year.now().getValue();
        int ano;
        try {
            ano = Integer.parseInt(anoStr.replaceAll("\\D", ""));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Ano inválido. Digite apenas números.", "Validação de Ano", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (ano < 1900 || ano > anoAtual + 1) {
            JOptionPane.showMessageDialog(null,
                    "Ano fora do intervalo permitido (1900–" + (anoAtual + 1) + ").",
                    "Validação de Ano", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * 🔹 Valida o valor da diária do veículo.
     * @param diaria valor numérico da diária
     * @return true se o valor for válido (> 0)
     */
    public static boolean validarDiaria(double diaria) {
        if (diaria <= 0) {
            JOptionPane.showMessageDialog(null,
                    "O valor da diária deve ser maior que zero.",
                    "Validação de Diária", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * 🔹 Valida somente a placa — útil para validações pontuais.
     */
    public static boolean validarPlaca(String placa) {
        if (placa == null || placa.isBlank()) return false;
        String antiga = "^[A-Z]{3}-\\d{4}$";
        String mercosul = "^[A-Z]{3}\\d[A-Z]\\d{2}$";
        return Pattern.matches(antiga, placa) || Pattern.matches(mercosul, placa);
    }

    /**
     * 🔹 Valida apenas o ano — útil em verificações isoladas.
     */
    public static boolean validarAno(String anoStr) {
        int anoAtual = Year.now().getValue();
        try {
            int ano = Integer.parseInt(anoStr.replaceAll("\\D", ""));
            return ano >= 1900 && ano <= anoAtual + 1;
        } catch (Exception e) {
            return false;
        }
    }
}



