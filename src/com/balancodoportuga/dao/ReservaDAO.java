package com.balancodoportuga.dao;

import com.balancodoportuga.model.Cliente;
import com.balancodoportuga.model.Reserva;
import com.balancodoportuga.model.Veiculo;
import com.balancodoportuga.util.DB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    public void atualizarStatus(int id, String novoStatus) throws SQLException {
    String sql = "UPDATE reservas SET status = ? WHERE id = ?";
    try (Connection conn = DB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, novoStatus);
        ps.setInt(2, id);
        ps.executeUpdate();
    }
}

    // Inserir nova reserva
    public void insert(Reserva r) throws SQLException {
    String sqlVerificar = """
        SELECT COUNT(*) FROM reservas 
        WHERE veiculo_id = ? 
          AND status IN ('Solicitada', 'Em andamento')
          AND (
                (date(data_inicio) <= date(?) AND date(data_fim) >= date(?)) OR
                (date(data_inicio) <= date(?) AND date(data_fim) >= date(?)) OR
                (date(data_inicio) >= date(?) AND date(data_fim) <= date(?))
          )
    """;

    String sqlInsert = """
        INSERT INTO reservas (cliente_id, veiculo_id, data_inicio, data_fim, total, status)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

    try (Connection conn = DB.getConnection()) {
        // Verificar se já existe uma reserva ativa no mesmo período
        try (PreparedStatement psVerificar = conn.prepareStatement(sqlVerificar)) {
            psVerificar.setInt(1, r.getVehicle().getId());
            psVerificar.setString(2, r.getDataInicio().toString());
            psVerificar.setString(3, r.getDataInicio().toString());
            psVerificar.setString(4, r.getDataFim().toString());
            psVerificar.setString(5, r.getDataFim().toString());
            psVerificar.setString(6, r.getDataInicio().toString());
            psVerificar.setString(7, r.getDataFim().toString());

            ResultSet rs = psVerificar.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("O veículo já está reservado neste período para outra locação ativa.");
            }
        }

        // Inserir nova reserva
        try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
            ps.setInt(1, r.getClient().getId());
            ps.setInt(2, r.getVehicle().getId());
            ps.setString(3, r.getDataInicio().toString());
            ps.setString(4, r.getDataFim().toString());
            ps.setDouble(5, r.getTotal());
            ps.setString(6, r.getStatus());
            ps.executeUpdate();
        }
    }
}

    // Atualiza status automaticamente
    public void atualizarStatusAutomaticamente() throws SQLException {
        String sqlEmAndamento = """
            UPDATE reservas
            SET status = 'Em andamento'
            WHERE status = 'Solicitada'
            AND date('now') BETWEEN date(data_inicio) AND date(data_fim)
        """;

        String sqlConcluida = """
            UPDATE reservas
            SET status = 'Concluída'
            WHERE status = 'Em andamento'
            AND date('now') > date(data_fim)
        """;

        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sqlEmAndamento);
            stmt.executeUpdate(sqlConcluida);
        }
    }

    // Buscar todas reservas
    public List<Reserva> getAll(List<Cliente> clients, List<Veiculo> vehicles) throws SQLException {
        atualizarStatusAutomaticamente();
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM reservas";

        try (Connection conn = DB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int clientId = rs.getInt("cliente_id");
                int vehicleId = rs.getInt("veiculo_id");

                Cliente c = clients.stream().filter(cl -> cl.getId() == clientId).findFirst().orElse(null);
                Veiculo v = vehicles.stream().filter(ve -> ve.getId() == vehicleId).findFirst().orElse(null);

                Reserva r = new Reserva(
    rs.getInt("id"),
    c,
    v,
    LocalDate.parse(rs.getString("data_inicio")),
    LocalDate.parse(rs.getString("data_fim")),
    rs.getDouble("total"),
    rs.getString("status"),
    rs.getString("status_pagamento")
);

                lista.add(r);
            }
        }
        return lista;
    }

    // Buscar reservas de um cliente
    public List<Reserva> getByClientId(int clientId, List<Cliente> clients, List<Veiculo> vehicles) throws SQLException {
        atualizarStatusAutomaticamente();
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE cliente_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int vehicleId = rs.getInt("veiculo_id");

                    Cliente c = clients.stream().filter(cl -> cl.getId() == clientId).findFirst().orElse(null);
                    Veiculo v = vehicles.stream().filter(ve -> ve.getId() == vehicleId).findFirst().orElse(null);

                    Reserva r = new Reserva(
    rs.getInt("id"),
    c,
    v,
    LocalDate.parse(rs.getString("data_inicio")),
    LocalDate.parse(rs.getString("data_fim")),
    rs.getDouble("total"),
    rs.getString("status"),
    rs.getString("status_pagamento")
);

                    lista.add(r);
                }
            }
        }
        return lista;
    }
    
    //verifica se uma reserva ja existe no mesmo período
    public boolean verificarConflitoReserva(int veiculoId, LocalDate inicio, LocalDate fim) throws SQLException {
    String sql = "SELECT COUNT(*) FROM reservas WHERE veiculo_id = ? AND " +
                 "(data_inicio <= ? AND data_fim >= ?) " +
                 "AND status NOT IN ('Cancelada', 'Concluída')";
    try (Connection conn = DB.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, veiculoId);
        stmt.setDate(2, Date.valueOf(fim));
        stmt.setDate(3, Date.valueOf(inicio));
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0; // existe conflito
        }
    }
    return false;
}

    // Cancelar reserva
    public void cancelarReserva(int id) throws SQLException {
        String sql = "UPDATE reservas SET status = 'Cancelada' WHERE id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    // Cancelar reserva antes da data definida do cliente (calculo)
    public void cancelarReservaComValor(int id, double total) throws SQLException {
    String sql = "UPDATE reservas SET status = 'Cancelada', total = ? WHERE id = ?";

    try (Connection conn = DB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setDouble(1, total);
        ps.setInt(2, id);
        ps.executeUpdate();
    }
}


    // Concluir reserva: atualiza total (com multa se necessário) e seta status para 'Concluída'
    public void concluirReserva(int id, double novoTotal) throws SQLException {
        String sql = "UPDATE reservas SET total = ?, status = 'Concluída' WHERE id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, novoTotal);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
    
    //atualiza o valor total de uma reserva
    public void atualizarValorTotal(int reservaId, double novoTotal) throws Exception {
    String sql = "UPDATE reservas SET total = ? WHERE id = ?";
    try (Connection conn = DB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setDouble(1, novoTotal);
        ps.setInt(2, reservaId);
        ps.executeUpdate();
    }
}

    // Atualizar status de pagamento da reserva
public void atualizarStatusPagamento(int id, String novoStatus) throws SQLException {
    String sql = "UPDATE reservas SET status_pagamento = ? WHERE id = ?";
    try (Connection conn = DB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, novoStatus);
        ps.setInt(2, id);
        ps.executeUpdate();
    }
}

// Obter status de pagamento de uma reserva
public String getStatusPagamento(int id) throws SQLException {
    String sql = "SELECT status_pagamento FROM reservas WHERE id = ?";
    try (Connection conn = DB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("status_pagamento");
        }
    }
    return "Não Pago";
}

    //buscar reserva por ID (necessário para PagamentoDAO)
    public Reserva getById(int id) throws SQLException {
        String sql = "SELECT * FROM reservas WHERE id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int clienteId = rs.getInt("cliente_id");
                int veiculoId = rs.getInt("veiculo_id");

                ClienteDAO cDao = new ClienteDAO();
                VeiculoDAO vDao = new VeiculoDAO();

                Cliente cliente = cDao.getAll().stream()
                        .filter(c -> c.getId() == clienteId)
                        .findFirst()
                        .orElse(null);

                Veiculo veiculo = vDao.getAll().stream()
                        .filter(v -> v.getId() == veiculoId)
                        .findFirst()
                        .orElse(null);

                if (cliente == null || veiculo == null)
                    return null;

                return new Reserva(
                        rs.getInt("id"),
                        cliente,
                        veiculo,
                        LocalDate.parse(rs.getString("data_inicio")),
                        LocalDate.parse(rs.getString("data_fim")),
                        rs.getDouble("total"),
                        rs.getString("status"),
                        rs.getString("status_pagamento")
                );
            }
        }
        return null;
    }
}