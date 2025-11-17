package com.balancodoportuga.util;

import javax.swing.*;

public class ValidadorCliente {

    public static boolean validar(String nome, String cpf, String email, String telefone,
                                  String endereco, String numero, String bairro, String cidade,
                                  String estado, String cnh) {

        // === NOME ===
        if (nome == null || nome.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O nome não pode ficar vazio!");
            return false;
        }
        if (nome.trim().length() < 3) {
            JOptionPane.showMessageDialog(null, "O nome deve conter pelo menos 3 caracteres!");
            return false;
        }

        // === CPF ===
        if (cpf == null || cpf.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O CPF é obrigatório!");
            return false;
        }
        if (!cpf.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(null, "CPF inválido! Deve conter exatamente 11 dígitos numéricos.");
            return false;
        }

        // === EMAIL ===
        if (email == null || email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O e-mail é obrigatório!");
            return false;
        }
        if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(null, "Formato de e-mail inválido!");
            return false;
        }

        // === TELEFONE ===
        if (telefone == null || telefone.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O telefone é obrigatório!");
            return false;
        }
        if (!telefone.matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(null, "Telefone inválido! Deve conter 10 ou 11 números.");
            return false;
        }

        // === ENDEREÇO ===
        if (endereco == null || endereco.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O endereço é obrigatório!");
            return false;
        }
        if (endereco.trim().length() < 3) {
            JOptionPane.showMessageDialog(null, "O endereço deve conter pelo menos 3 caracteres!");
            return false;
        }

        // === NÚMERO ===
        if (numero == null || numero.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O número é obrigatório!");
            return false;
        }
        if (!numero.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "O número deve conter apenas dígitos!");
            return false;
        }

        // === BAIRRO ===
        if (bairro == null || bairro.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O bairro é obrigatório!");
            return false;
        }

        // === CIDADE ===
        if (cidade == null || cidade.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "A cidade é obrigatória!");
            return false;
        }

        // === ESTADO ===
        if (estado == null || estado.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O estado é obrigatório!");
            return false;
        }
        if (!estado.matches("^[A-Za-z]{2}$")) {
            JOptionPane.showMessageDialog(null, "O estado deve conter exatamente 2 letras (ex: BA, SP, RJ).");
            return false;
        }

        // === CNH ===
        if (cnh == null || cnh.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "A CNH é obrigatória!");
            return false;
        }
        if (!cnh.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(null, "CNH inválida! Deve conter exatamente 11 dígitos numéricos.");
            return false;
        }

        return true;
    }
}

