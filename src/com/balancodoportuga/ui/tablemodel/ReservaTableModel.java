package com.balancodoportuga.ui.tablemodel;

import com.balancodoportuga.model.Reserva;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ReservaTableModel extends AbstractTableModel {

    private final String[] colunasCliente = {
            "Veículo", "Data Início", "Data Fim", "Total (R$)", "Status"
    };

    private final String[] colunasFuncionario = {
            "ID", "Cliente", "Placa", "Modelo",
            "Data Início", "Data Fim", "Total (R$)", "Status"
    };

    private List<Reserva> reservas = new ArrayList<>();
    private final boolean modoCliente;

    // ----------------------------
    // CONSTRUTORES
    // ----------------------------

    public ReservaTableModel(List<Reserva> reservas, boolean modoCliente) {
        this.reservas = (reservas != null) ? reservas : new ArrayList<>();
        this.modoCliente = modoCliente;
    }

    // ----------------------------
    // MÉTODOS DE SUPORTE
    // ----------------------------

    public void setReservas(List<Reserva> novas) {
        this.reservas = novas != null ? novas : new ArrayList<>();
        fireTableDataChanged();
    }

    public void limpar() {
        this.reservas.clear();
        fireTableDataChanged();
    }

    public void addReserva(Reserva r) {
        reservas.add(r);
        fireTableRowsInserted(reservas.size() - 1, reservas.size() - 1);
    }

    public Reserva getReservaAt(int index) {
        if (index < 0 || index >= reservas.size()) return null;
        return reservas.get(index);
    }

    // ----------------------------
    // OVERRIDES OBRIGATÓRIOS
    // ----------------------------

    @Override
    public int getRowCount() {
        return reservas.size();
    }

    @Override
    public int getColumnCount() {
        return modoCliente ? colunasCliente.length : colunasFuncionario.length;
    }

    @Override
    public String getColumnName(int column) {
        return modoCliente ? colunasCliente[column] : colunasFuncionario[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Reserva r = reservas.get(rowIndex);

        if (modoCliente) {
            return switch (columnIndex) {
                case 0 -> r.getVehicle().getModelo() + " (" + r.getVehicle().getPlaca() + ")";
                case 1 -> r.getDataInicio();
                case 2 -> r.getDataFim();
                case 3 -> String.format("R$ %.2f", r.getTotal());
                case 4 -> formatarStatus(r.getStatus());
                default -> "";
            };
        }

        // Funcionário
        return switch (columnIndex) {
        case 0 -> r.getId();
        case 1 -> r.getClient().getNome();
        case 2 -> r.getVehicle().getPlaca();
        case 3 -> r.getVehicle().getModelo();
        case 4 -> r.getDataInicio();
        case 5 -> r.getDataFim();
        case 6 -> String.format("R$ %.2f", r.getTotal());
        case 7 -> formatarStatus(r.getStatus());
        default -> "—";
        };
    }

    private String formatarStatus(String status) {
        if (status == null) return "";
        return switch (status.toLowerCase()) {
            case "solicitada" -> "📋 Solicitada";
            case "em andamento" -> "🕒 Em Andamento";
            case "concluída" -> "✅ Concluída";
            case "cancelada" -> "❌ Cancelada";
            default -> status;
        };
    }
}


