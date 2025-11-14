package com.balancodoportuga.ui.tablemodel;

import com.balancodoportuga.model.Cliente;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;


public class ClienteTableModel extends AbstractTableModel {

    private final String[] colunas = {
            "ID", "Nome", "CPF", "E-mail", "Telefone", "Endereço Completo"
    };

    private List<Cliente> clientes = new ArrayList<>();

    // =======================================================
    // 🔹 Métodos principais
    // =======================================================

    public void setClients(List<Cliente> clients) {
    this.clientes = clients;
    fireTableDataChanged();
}


    public Cliente getClienteAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= clientes.size()) return null;
        return clientes.get(rowIndex);
    }

    // =======================================================
    // 🔹 Implementação padrão da AbstractTableModel
    // =======================================================

    @Override
    public int getRowCount() {
        return clientes.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public String getColumnName(int column) {
        return colunas[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Cliente c = clientes.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> c.getId();
            case 1 -> safe(c.getNome());
            case 2 -> safe(c.getCpf());
            case 3 -> safe(c.getEmail());
            case 4 -> safe(c.getTelefone());
            case 5 -> formatarEndereco(c);
            default -> null;
        };
    }

    // =======================================================
    // 🧩 Métodos auxiliares
    // =======================================================

    /** Evita NullPointer em strings */
    private String safe(String valor) {
        return (valor == null || valor.isBlank()) ? "—" : valor.trim();
    }

    /** Monta o endereço completo de forma segura e formatada */
    private String formatarEndereco(Cliente c) {
        String endereco = safe(c.getEndereco());
        String numero = safe(c.getNumero());
        String bairro = safe(c.getBairro());
        String cidade = safe(c.getCidade());
        String estado = safe(c.getEstado());

        return String.format("%s, %s - %s - %s/%s",
                endereco, numero, bairro, cidade, estado);
    }
}

