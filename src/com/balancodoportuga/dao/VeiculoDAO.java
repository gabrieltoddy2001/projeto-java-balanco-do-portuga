package com.balancodoportuga.dao;

import com.balancodoportuga.model.Veiculo;
import com.balancodoportuga.util.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {

    public void insert(Veiculo v) throws SQLException {
        String sql = "INSERT INTO veiculos(modelo, marca, placa, ano, diaria) VALUES (?,?,?,?,?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, v.getModelo());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getPlaca());
            ps.setInt(4, v.getAno());
            ps.setDouble(5, v.getDiaria());

            ps.executeUpdate();
        }
    }

    public void update(Veiculo v) throws SQLException {
        String sql = "UPDATE veiculos SET modelo=?, marca=?, placa=?, ano=?, diaria=? WHERE id=?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, v.getModelo());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getPlaca());
            ps.setInt(4, v.getAno());
            ps.setDouble(5, v.getDiaria());
            ps.setInt(6, v.getId());

            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM veiculos WHERE id=?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Veiculo buscarPorPlaca(String placa) throws SQLException {
        String sql = "SELECT * FROM veiculos WHERE placa = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, placa);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Veiculo(
                            rs.getInt("id"),
                            rs.getString("modelo"),
                            rs.getString("marca"),
                            rs.getString("placa"),
                            rs.getInt("ano"),
                            rs.getDouble("diaria")
                    );
                }
            }
        }
        return null;
    }

    public Veiculo getById(int id) throws SQLException {
        String sql = "SELECT * FROM veiculos WHERE id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Veiculo(
                        rs.getInt("id"),
                        rs.getString("modelo"),
                        rs.getString("marca"),
                        rs.getString("placa"),
                        rs.getInt("ano"),
                        rs.getDouble("diaria")
                );
            }
        }
        return null;
    }

    public List<Veiculo> getAll() throws SQLException {
        List<Veiculo> veiculos = new ArrayList<>();

        String sql = "SELECT * FROM veiculos";

        try (Connection conn = DB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                veiculos.add(new Veiculo(
                        rs.getInt("id"),
                        rs.getString("modelo"),
                        rs.getString("marca"),
                        rs.getString("placa"),
                        rs.getInt("ano"),
                        rs.getDouble("diaria")
                ));
            }
        }
        return veiculos;
    }

    public boolean veiculoTemReservaAtiva(int veiculoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservas WHERE veiculo_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, veiculoId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }
}