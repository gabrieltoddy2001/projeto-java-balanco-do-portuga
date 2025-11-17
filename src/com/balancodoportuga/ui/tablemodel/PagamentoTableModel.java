package com.balancodoportuga.ui.tablemodel;

import com.balancodoportuga.model.Pagamento;
import com.balancodoportuga.model.Reserva;


import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;


public class PagamentoTableModel extends AbstractTableModel {

    private final String[] colunas = {
            "ID", "Reserva", "Veículo", "Valor Pago", "Data", "Método", "Status Pagamento"
    };
public Reserva getReservaAt(int rowIndex) {
    if (rowIndex >= 0 && rowIndex < pagamentos.size()) {
        return pagamentos.get(rowIndex).getReserva();
    }
    return null;
}

    private List<Pagamento> pagamentos = new ArrayList<>();

    // =========================================================
    // 🔹 Construtor
    // =========================================================
    public PagamentoTableModel(List<Pagamento> pagamentos) {
        this.pagamentos = (pagamentos != null) ? pagamentos : new ArrayList<>();
    }

    
    // =========================================================
    // 🔹 Implementação padrão da TableModel
    // =========================================================

    @Override
    public int getRowCount() {
        return pagamentos.size();
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
        Pagamento p = pagamentos.get(rowIndex);
        if (p == null) return "—";

        return switch (columnIndex) {
            case 0 -> p.getId();
            case 1 -> formatarReserva(p);
            case 2 -> safe(p.getReserva() != null && p.getReserva().getVehicle() != null
                    ? p.getReserva().getVehicle().getModelo()
                    : "—");
            case 3 -> String.format("R$ %.2f", p.getValor());
            case 4 -> safe(p.getDataPagamento());
            case 5 -> safe(p.getMetodoPagamento());
            case 6 -> formatarStatus(p);
            default -> null;
        };
    }

    // =========================================================
    // 🧩 Métodos auxiliares de formatação
    // =========================================================

    /** Formata a identificação da reserva */
    private String formatarReserva(Pagamento p) {
        if (p.getReserva() == null || p.getReserva().getId() == 0) return "—";
        return "Reserva #" + p.getReserva().getId();
    }

    /** Garante que textos nulos ou vazios exibam “—” */
    private String safe(Object valor) {
        return (valor == null || valor.toString().isBlank()) ? "—" : valor.toString();
    }

    /** Traduz o status para o formato visual padronizado */
private String formatarStatus(Pagamento p) {
    if (p.getReserva() == null) return "—";
    String st = p.getReserva().getStatusPagamento();
    if (st == null || st.isBlank()) return "—";
    return switch (st.toLowerCase()) {
        case "pago"     -> "✅ Pago";
        case "parcial"  -> "🟡 Parcial";
        case "pendente" -> "🟥 Pendente";
        default         -> st.substring(0,1).toUpperCase() + st.substring(1);
    };
}



}
