package com.balancodoportuga.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {

    private static final String URL = "jdbc:sqlite:balancodoportuga.db";

    static {
        // Executado automaticamente quando a classe é carregada
        try (Connection conn = getConnection()) {
            criarTabelas(conn);
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar o banco: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private static void criarTabelas(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {

            // === Tabela de Clientes ===
            st.execute("""
    CREATE TABLE IF NOT EXISTS clients (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        nome TEXT NOT NULL,
        cpf TEXT NOT NULL UNIQUE,
        cnh TEXT NOT NULL UNIQUE,
        email TEXT NOT NULL,
        telefone TEXT,
        endereco TEXT,
        numero TEXT,
        bairro TEXT,
        cidade TEXT,
        estado TEXT
    );
""");


            // === Tabela de Veículos ===
            st.execute("""
                CREATE TABLE IF NOT EXISTS veiculos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    modelo TEXT NOT NULL,
                    marca TEXT NOT NULL,
                    placa TEXT NOT NULL UNIQUE,
                    ano INTEGER NOT NULL CHECK(ano>= 1950),
                    diaria REAL NOT NULL CHECK(diaria > 0)
                )
            """);

            // === Tabela de Reservas ===
            st.execute("""
                CREATE TABLE IF NOT EXISTS reservas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cliente_id INTEGER NOT NULL,
                    veiculo_id INTEGER NOT NULL,
                    data_inicio TEXT NOT NULL,
                    data_fim TEXT NOT NULL,
                    total REAL NOT NULL,
                    status TEXT NOT NULL,
                    status_pagamento TEXT DEFAULT 'Não Pago',
                    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
                    FOREIGN KEY (veiculo_id) REFERENCES veiculos(id)
                )   
            """);

            // === Tabela de Pagamentos ===
            st.execute("""
                CREATE TABLE IF NOT EXISTS pagamentos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    reserva_id INTEGER NOT NULL,
                    valor REAL NOT NULL,
                    data_pagamento TEXT NOT NULL,
                    metodo_pagamento TEXT NOT NULL,
                    FOREIGN KEY (reserva_id) REFERENCES reservas(id)
                )
            """);
        }
    }
}

