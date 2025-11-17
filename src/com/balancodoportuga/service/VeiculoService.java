package com.balancodoportuga.service;

import com.balancodoportuga.dao.VeiculoDAO;
import com.balancodoportuga.model.Veiculo;
import com.balancodoportuga.util.ValidadorVeiculo;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class VeiculoService {

    private final VeiculoDAO dao = new VeiculoDAO();

    // Retorna todos os veículos cadastrados
    public List<Veiculo> listarTodos() throws SQLException {
        return dao.getAll();
    }

    // Cadastra um novo veículo com validação de dados
    public void adicionar(String modelo, String marca, String placa, String anoStr, double diaria) throws SQLException {
        if (!ValidadorVeiculo.validar(modelo, marca, placa, anoStr)) return;
        if (!ValidadorVeiculo.validarDiaria(diaria)) return;

        // Verifica duplicidade de placa
        if (dao.buscarPorPlaca(placa) != null) {
            JOptionPane.showMessageDialog(null, "Já existe um veículo com esta placa!");
            return;
        }

        Veiculo novo = new Veiculo(0, modelo, marca, placa, Integer.parseInt(anoStr), diaria);
        dao.insert(novo);
    }

    // Atualiza um veículo existente
    public void atualizar(Veiculo veiculo, String modelo, String marca, String placa, String anoStr, double diaria) throws SQLException {
        if (!ValidadorVeiculo.validar(modelo, marca, placa, anoStr)) return;
        if (!ValidadorVeiculo.validarDiaria(diaria)) return;

        Veiculo atualizado = new Veiculo(
                veiculo.getId(),
                modelo,
                marca,
                placa,
                Integer.parseInt(anoStr),
                diaria
        );
        dao.update(atualizado);
    }

    // não deixa excluir se tem reserva
    public void excluir(Veiculo veiculo) throws SQLException {
        if (dao.veiculoTemReservaAtiva(veiculo.getId())) {
            JOptionPane.showMessageDialog(null,
                    "Este veículo possui reserva ativa e não pode ser excluído.",
                    "Operação não permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        dao.delete(veiculo.getId());
    }

    //  Busca veículos com filtro de texto
    public List<Veiculo> filtrar(List<Veiculo> lista, String termo) {
        if (lista == null || termo == null || termo.isBlank()) return lista;

        String filtro = termo.trim().toLowerCase();
        return lista.stream()
                .filter(v ->
                        v.getModelo().toLowerCase().contains(filtro) ||
                        v.getMarca().toLowerCase().contains(filtro) ||
                        v.getPlaca().toLowerCase().contains(filtro))
                .toList();
    }
}