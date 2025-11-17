package com.balancodoportuga.dao;

import com.balancodoportuga.model.Pagamento;
import com.balancodoportuga.model.Reserva;
import com.balancodoportuga.util.DB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PagamentoDAO {

    // Registrar um novo pagamento   
    public void registrarPagamento(Reserva reserva, double valorPago, String metodo) throws SQLException {

    String sqlInsert = """
        INSERT INTO pagamentos (reserva_id, valor, data_pagamento, metodo_pagamento)
        VALUES (?, ?, datetime('now'), ?)
    """;

    String sqlSoma = "SELECT SUM(valor) FROM pagamentos WHERE reserva_id = ?";

    String sqlAtualizarStatusPago =
            "UPDATE reservas SET status_pagamento = 'Pago' WHERE id = ?";

    String sqlAtualizarStatusParcial =
            "UPDATE reservas SET status_pagamento = 'Parcial' WHERE id = ?";

    try (Connection conn = DB.getConnection()) {
        conn.setAutoCommit(false);

        // Inserir pagamento
        try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
            ps.setInt(1, reserva.getId());
            ps.setDouble(2, valorPago);
            ps.setString(3, metodo);
            ps.executeUpdate();
        }

        // Calcular total pago até agora
        double totalPago = 0;
        try (PreparedStatement ps = conn.prepareStatement(sqlSoma)) {
            ps.setInt(1, reserva.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) totalPago = rs.getDouble(1);
        }

        // Atualizar status financeiro corretamente
        if (totalPago >= reserva.getTotal()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlAtualizarStatusPago)) {
                ps.setInt(1, reserva.getId());
                ps.executeUpdate();
            }
        } else {
            try (PreparedStatement ps = conn.prepareStatement(sqlAtualizarStatusParcial)) {
                ps.setInt(1, reserva.getId());
                ps.executeUpdate();
            }
        }

        conn.commit();
    }
}

    // Buscar todos os pagamentos de um cliente específico
    public List<Pagamento> getByCliente(int clienteId, ReservaDAO reservaDAO) throws SQLException {
        List<Pagamento> lista = new ArrayList<>();
        String sql = """
            SELECT p.* FROM pagamentos p
            INNER JOIN reservas r ON r.id = p.reserva_id
            WHERE r.cliente_id = ?
        """;

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Reserva reserva = reservaDAO.getById(rs.getInt("reserva_id"));
                if (reserva == null) continue;

                // Tratamento robusto para a data
                String dataStr = rs.getString("data_pagamento");
                LocalDate dataPagamento;
                if (dataStr != null && dataStr.length() >= 10) {
                    dataPagamento = LocalDate.parse(dataStr.substring(0, 10));
                } else {
                    dataPagamento = LocalDate.now();
                }

                Pagamento p = new Pagamento(
                        rs.getInt("id"),
                        reserva,
                        rs.getDouble("valor"),
                        dataPagamento,
                        rs.getString("metodo_pagamento")
                );
                lista.add(p);
            }
        }
        return lista;
    }

    // Buscar todos os pagamentos
    public List<Pagamento> getAll(ReservaDAO reservaDAO) throws SQLException {
        List<Pagamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM pagamentos";

        try (Connection conn = DB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Reserva reserva = reservaDAO.getById(rs.getInt("reserva_id"));
                if (reserva == null) continue;

                String dataStr = rs.getString("data_pagamento");
                LocalDate dataPagamento;
                if (dataStr != null && dataStr.length() >= 10) {
                    dataPagamento = LocalDate.parse(dataStr.substring(0, 10));
                } else {
                    dataPagamento = LocalDate.now();
                }

                Pagamento p = new Pagamento(
                        rs.getInt("id"),
                        reserva,
                        rs.getDouble("valor"),
                        dataPagamento,
                        rs.getString("metodo_pagamento")
                );
                lista.add(p);
            }
        }
        return lista;
    }

    public double getTotalPago(int reservaId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(valor), 0) FROM pagamentos WHERE reserva_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservaId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }
    public void inserirPagamentoMulta(int reservaId, double valor, String descricao) throws SQLException {

    String sqlInsert = """
        INSERT INTO pagamentos (reserva_id, valor, data_pagamento, metodo_pagamento)
        VALUES (?, ?, ?, ?)
    """;

    String sqlAtualizaTotal = "UPDATE reservas SET total = total + ? WHERE id = ?";

    try (Connection conn = DB.getConnection()) {

        conn.setAutoCommit(false);

        // ----------- INSERE A MULTA -----------
        try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
            ps.setInt(1, reservaId);
            ps.setDouble(2, valor);
            ps.setString(3, java.time.LocalDate.now().toString());
            ps.setString(4, "Multa: " + descricao);
            ps.executeUpdate();
        }

        // ----------- ATUALIZA O TOTAL -----------
        try (PreparedStatement ps = conn.prepareStatement(sqlAtualizaTotal)) {
            ps.setDouble(1, valor);
            ps.setInt(2, reservaId);
            ps.executeUpdate();
        }

        conn.commit();
    }
}

    public List<Pagamento> getByReservaId(int reservaId, ReservaDAO reservaDAO) throws SQLException {
        List<Pagamento> pagamentos = new ArrayList<>();
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM pagamentos WHERE reserva_id = ? ORDER BY data_pagamento DESC"
             )) {
            stmt.setInt(1, reservaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reserva r = reservaDAO.getById(reservaId);

                String dataStr = rs.getString("data_pagamento");
                LocalDate dataPagamento;
                if (dataStr != null && dataStr.length() >= 10) {
                    dataPagamento = LocalDate.parse(dataStr.substring(0, 10));
                } else {
                    dataPagamento = LocalDate.now();
                }

                pagamentos.add(new Pagamento(
                        rs.getInt("id"),
                        r,
                        rs.getDouble("valor"),
                        dataPagamento,
                        rs.getString("metodo_pagamento")
                ));
            }
        }
        return pagamentos;
    }
}