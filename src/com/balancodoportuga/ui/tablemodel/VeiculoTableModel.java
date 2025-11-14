package com.balancodoportuga.ui.tablemodel;

import com.balancodoportuga.model.Veiculo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;


public class VeiculoTableModel extends AbstractTableModel {

    private final String[] colunas = {
            "ID", "Modelo", "Marca", "Placa", "Ano", "Diária (R$)"
    };

    private List<Veiculo> veiculos = new ArrayList<>();

    // ===============================================================
    // 🔹 Métodos principais
    // ===============================================================

    /** Define a lista de veículos e atualiza a tabela */
    public void setVeiculos(List<Veiculo> veiculos) {
        this.veiculos = (veiculos != null) ? veiculos : new ArrayList<>();
        fireTableDataChanged();
    }

    /** Retorna um veículo específico da linha selecionada */
    public Veiculo getVeiculo(int linha) {
        if (linha < 0 || linha >= veiculos.size()) return null;
        return veiculos.get(linha);
    }

    // ===============================================================
    // 🔹 Implementação da AbstractTableModel
    // ===============================================================

    @Override
    public int getRowCount() {
        return veiculos.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public String getColumnName(int coluna) {
        return colunas[coluna];
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        Veiculo v = veiculos.get(linha);
        if (v == null) return "—";

        return switch (coluna) {
            case 0 -> v.getId();
            case 1 -> safe(v.getModelo());
            case 2 -> safe(v.getMarca());
            case 3 -> safe(v.getPlaca());
            case 4 -> v.getAno() == 0 ? "—" : v.getAno();
            case 5 -> String.format("R$ %.2f", v.getDiaria());
            default -> "—";
        };
    }

    // ===============================================================
    // 🧩 Métodos auxiliares
    // ===============================================================

    /** Evita valores nulos ou vazios */
    private String safe(Object valor) {
        return (valor == null || valor.toString().isBlank()) ? "—" : valor.toString().trim();
    }
}


