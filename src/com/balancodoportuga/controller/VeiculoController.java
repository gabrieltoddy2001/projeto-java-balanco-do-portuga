package com.balancodoportuga.controller;

import com.balancodoportuga.dao.ClienteDAO;
import com.balancodoportuga.dao.VeiculoDAO;
import com.balancodoportuga.dao.ReservaDAO;
import com.balancodoportuga.model.Veiculo;
import com.balancodoportuga.model.Reserva;

import java.sql.SQLException;
import java.util.List;

public class VeiculoController {

    private final VeiculoDAO veiculoDAO = new VeiculoDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();

    // Lista todos os veículos
    public List<Veiculo> listarVeiculos() throws SQLException {
        return veiculoDAO.getAll();
    }

    // Adiciona um veículo
    public void adicionarVeiculo(String placa, String modelo, String marca, String cor,
                                 String ano, String diaria) throws Exception {

        int anoInt = Integer.parseInt(ano);
        double diariaDouble = Double.parseDouble(diaria);

        Veiculo v = new Veiculo(0, modelo, marca, placa, anoInt, diariaDouble);
        veiculoDAO.insert(v);
    }

    // Edita um veículo
    public void editarVeiculo(int id, String placa, String modelo, String marca, String cor,
                              String ano, String diaria) throws Exception {

        int anoInt = Integer.parseInt(ano);
        double diariaDouble = Double.parseDouble(diaria);

        Veiculo v = new Veiculo(id, modelo, marca, placa, anoInt, diariaDouble);
        veiculoDAO.update(v);
    }

    // Exclui um veículo com validação correta de reservas associadas
    public void excluirVeiculo(int id) throws SQLException {

        List<Reserva> reservas = reservaDAO.getAll(
        new ClienteDAO().getAll(),
        new VeiculoDAO().getAll()
);


        boolean possuiReservaAtiva = reservas.stream().anyMatch(
                r -> r.getVehicle() != null &&
                     r.getVehicle().getId() == id &&
                     !r.getStatus().equalsIgnoreCase("Concluída") &&
                     !r.getStatus().equalsIgnoreCase("Cancelada")
        );

        if (possuiReservaAtiva) {
            throw new SQLException("O veículo não pode ser excluído pois possui uma reserva ativa ou pendente.");
        }

        veiculoDAO.delete(id);
    }
}