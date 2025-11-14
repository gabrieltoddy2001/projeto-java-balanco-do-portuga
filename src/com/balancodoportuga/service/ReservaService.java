package com.balancodoportuga.service;

import com.balancodoportuga.dao.ClienteDAO;
import com.balancodoportuga.dao.ReservaDAO;
import com.balancodoportuga.dao.VeiculoDAO;
import com.balancodoportuga.model.Reserva;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReservaService {

    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    // Retorna todas as reservas de um cliente
    public List<Reserva> listarPorCliente(int clienteId) throws SQLException {
    ClienteDAO cdao = new ClienteDAO();
    VeiculoDAO vdao = new VeiculoDAO();

    return reservaDAO.getByClientId(
            clienteId,
            cdao.getAll(),
            vdao.getAll()
    );
}


    // Cancela uma reserva pelo ID
    public void cancelar(int reservaId) throws SQLException {
        reservaDAO.cancelarReserva(reservaId);
    }

    // Busca reserva ativa (Solicitada ou Em andamento)
    public Reserva buscarAtiva(int clienteId) throws SQLException {
        List<Reserva> reservas = listarPorCliente(clienteId);
        return reservas.stream()
                .filter(r -> r.getStatus().equalsIgnoreCase("Em andamento")
                          || r.getStatus().equalsIgnoreCase("Solicitada"))
                .findFirst()
                .orElse(null);
    }

    // Filtra reservas conforme os critérios de busca da interface
    public List<Reserva> filtrarReservas(List<Reserva> listaOriginal,
                                         String termo, String status,
                                         LocalDate inicio, LocalDate fim) {
        return listaOriginal.stream()
                .filter(r -> termo == null || termo.isBlank()
                        || r.getVehicle().getModelo().toLowerCase().contains(termo.toLowerCase())
                        || r.getVehicle().getPlaca().toLowerCase().contains(termo.toLowerCase()))
                .filter(r -> "Todos".equalsIgnoreCase(status) || r.getStatus().equalsIgnoreCase(status))
                .filter(r -> (inicio == null || !r.getDataInicio().isBefore(inicio)) &&
                             (fim == null || !r.getDataFim().isAfter(fim)))
                .collect(Collectors.toList());
    }

    // Atualiza status automaticamente (reservas vencidas, etc)
    public void atualizarStatusAutomaticamente() throws SQLException {
        reservaDAO.atualizarStatusAutomaticamente();
    }

    public double calcularMulta(Reserva r) {
        long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(r.getDataFim(), LocalDate.now());
        return diasAtraso > 0 ? r.getVehicle().getDiaria() * 0.3 * diasAtraso : 0;
    }

    // Calcula o total com multa (caso haja atraso)
    public double calcularTotalComMulta(Reserva r) {
        return r.getTotal() + calcularMulta(r);
    }

    // Gera um relatório resumido de reservas
    public String gerarRelatorioReservas(List<Reserva> reservas) {
        long total = reservas.size();
        long emAndamento = reservas.stream().filter(r -> r.getStatus().equalsIgnoreCase("Em andamento")).count();
        long solicitadas = reservas.stream().filter(r -> r.getStatus().equalsIgnoreCase("Solicitada")).count();
        long concluidas = reservas.stream().filter(r -> r.getStatus().equalsIgnoreCase("Concluída")).count();
        long canceladas = reservas.stream().filter(r -> r.getStatus().equalsIgnoreCase("Cancelada")).count();

        double valorTotal = reservas.stream()
                .filter(r -> r.getStatus().equalsIgnoreCase("Concluída"))
                .mapToDouble(Reserva::getTotal)
                .sum();

        double mediaValor = concluidas > 0 ? valorTotal / concluidas : 0.0;

        return String.format("""
                📊 RELATÓRIO DE RESERVAS

                Total: %d
                ├─ Em Andamento: %d
                ├─ Solicitadas: %d
                ├─ Concluídas: %d
                └─ Canceladas: %d

                💰 Valor Total Arrecadado: R$ %.2f
                💵 Média por Reserva Concluída: R$ %.2f
                """, total, emAndamento, solicitadas, concluidas, canceladas, valorTotal, mediaValor);
    }
}
