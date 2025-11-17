package com.balancodoportuga.ui.menu;

import com.balancodoportuga.ui.forms.ClienteForm;
import com.balancodoportuga.ui.forms.PagamentoForm;
import com.balancodoportuga.ui.forms.VeiculoForm;
import com.balancodoportuga.ui.forms.ReservaForm;

import javax.swing.*;
import java.awt.*;

public class FuncionarioMenu extends JFrame {

    //TEMA PORTUGAL
    private final Color fundo = new Color(25, 25, 25);
    private final Color dourado = new Color(255, 215, 0);
    private final Color vermelho = new Color(178, 0, 0);

    public FuncionarioMenu() {

        // ===== CONFIGURAÇÕES DA JANELA =====
        setTitle("Balanço do Portuga - Painel do Funcionário");
        setSize(900, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ===== PAINEL PRINCIPAL =====
        JPanel painelPrincipal = new JPanel(new BorderLayout(0, 20));
        painelPrincipal.setBackground(fundo);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(painelPrincipal);

        // ===== CABEÇALHO =====
        JLabel titulo = new JLabel("Painel do Funcionário", SwingConstants.CENTER);
        titulo.setForeground(dourado);
        titulo.setFont(new Font("Segoe UI Semibold", Font.BOLD, 26));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(fundo);
        header.add(titulo, BorderLayout.CENTER);

        JPanel linhaDecorativa = new JPanel();
        linhaDecorativa.setPreferredSize(new Dimension(100, 3));
        linhaDecorativa.setBackground(dourado);
        header.add(linhaDecorativa, BorderLayout.SOUTH);

        painelPrincipal.add(header, BorderLayout.NORTH);

        // ===== ÁREA CENTRAL (cards) =====
        JPanel painelCards = new JPanel(new GridLayout(2, 2, 25, 25));
        painelCards.setBackground(fundo);

        painelCards.add(criarCard("Gerenciar Clientes", "👥", new Color(0, 150, 250), () -> {
            dispose();
            new ClienteForm().setVisible(true);
        }));

        painelCards.add(criarCard("Gerenciar Veículos", "🚗", new Color(0, 180, 80), () -> {
            dispose();
            new VeiculoForm().setVisible(true);
        }));

        painelCards.add(criarCard("Gerenciar Reservas", "📅", new Color(200, 160, 0), () -> {
            dispose();
            new ReservaForm().setVisible(true);
        }));

        painelCards.add(criarCard("Gerenciar Pagamentos", "💳", new Color(120, 0, 200), () -> {
            dispose();
            new PagamentoForm(this).setVisible(true);
        }));

        painelPrincipal.add(painelCards, BorderLayout.CENTER);

        // ===== RODAPÉ =====
        JPanel rodapePanel = new JPanel(new BorderLayout());
        rodapePanel.setBackground(fundo);

        // botão sair
        JButton btnSair = new JButton("🚶 Sair");
        btnSair.setBackground(vermelho);
        btnSair.setForeground(Color.WHITE);
        btnSair.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSair.setFocusPainted(false);
        btnSair.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSair.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        btnSair.setPreferredSize(new Dimension(100, 32));

        btnSair.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSair.setBackground(vermelho.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSair.setBackground(vermelho);
            }
        });

        btnSair.addActionListener(e -> {
            dispose();
            new LoginTela().setVisible(true);
        });

        JPanel sairPanel = new JPanel();
        sairPanel.setBackground(fundo);
        sairPanel.add(btnSair);
        rodapePanel.add(sairPanel, BorderLayout.NORTH);

        JLabel rodape = new JLabel("© 2025 - Desenvolvido por Gabriel’s", SwingConstants.CENTER);
        rodape.setForeground(Color.GRAY);
        rodape.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        rodapePanel.add(rodape, BorderLayout.SOUTH);

        painelPrincipal.add(rodapePanel, BorderLayout.SOUTH);
    }

    private JButton criarCard(String texto, String emoji, Color corBase, Runnable acao) {
        JButton card = new JButton("<html><center>" + emoji + "<br><br>" + texto + "</center></html>");
        card.setFont(new Font("Segoe UI", Font.BOLD, 16));
        card.setBackground(corBase);
        card.setForeground(Color.WHITE);
        card.setFocusPainted(false);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(25, 15, 25, 15)
        ));

        // Efeito hover
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(corBase.brighter());
                card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(corBase);
                card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            }
        });

        // Ação real
        card.addActionListener(e -> acao.run());

        return card;
    }
}