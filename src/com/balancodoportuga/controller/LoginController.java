package com.balancodoportuga.controller;

import com.balancodoportuga.dao.ClienteDAO;
import com.balancodoportuga.model.Cliente;

public class LoginController {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    // Autentica usuário. valida o parâmetro para cada caso quando for logar.
    public Object autenticarUsuario(String login, String senha) throws Exception {
        //Funcionário padrão
        if (login.equalsIgnoreCase("admin") && senha.equals("362651")) {
            return "admin";
        }
        //Cliente
        Cliente cliente = clienteDAO.buscarPorEmail(login);
        if (cliente != null && cliente.getCpf().equals(senha)) {
            return cliente;
        }
        return null; // login inválido
    }
}
