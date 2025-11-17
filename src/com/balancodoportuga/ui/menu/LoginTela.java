package com.balancodoportuga.ui.menu;

import com.balancodoportuga.controller.LoginController;
import com.balancodoportuga.model.Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginTela extends JFrame {

    private JTextField campoLogin;
    private JPasswordField campoSenha;
    private final LoginController controller = new LoginController();

    //TEMA PORTUGAL
    private final Color verdePortugal = new Color(0, 102, 51);
    private final Color vermelhoPortugal = new Color(178, 0, 0);
    private final Color douradoSuave = new Color(255, 215, 0);
    private final Color fundoEscuro = new Color(28, 28, 28);

    public LoginTela() {

        // Config janela
        setTitle("Balanço do Portuga - Login");
        setSize(420, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        // Painel principal
        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painelPrincipal.setBackground(fundoEscuro);

        // Título
        JLabel titulo = new JLabel("Balanço do Portuga", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(douradoSuave);
        painelPrincipal.add(titulo, BorderLayout.NORTH);

        // Painel do formulário
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBackground(fundoEscuro);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblLogin = new JLabel("Usuário ou E-mail:");
        lblLogin.setForeground(Color.WHITE);
        lblLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelForm.add(lblLogin, gbc);

        campoLogin = new JTextField(18);
        estilizarCampo(campoLogin);
        gbc.gridx = 1;
        painelForm.add(campoLogin, gbc);

        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setForeground(Color.WHITE);
        lblSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        painelForm.add(lblSenha, gbc);

        campoSenha = new JPasswordField(18);
        estilizarCampo(campoSenha);
        gbc.gridx = 1;
        painelForm.add(campoSenha, gbc);

        painelPrincipal.add(painelForm, BorderLayout.CENTER);

        // Botões
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        botoes.setBackground(fundoEscuro);

        JButton btnEntrar = criarBotao("Entrar", verdePortugal);
        JButton btnSair = criarBotao("Sair", vermelhoPortugal);

        botoes.add(btnEntrar);
        botoes.add(btnSair);

        painelPrincipal.add(botoes, BorderLayout.SOUTH);
        add(painelPrincipal);

        // Ações
        btnEntrar.addActionListener(e -> realizarLogin());
        btnSair.addActionListener(e -> System.exit(0));

        // Pressionar Enter faz login
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    realizarLogin();
                }
            }
        };

        campoLogin.addKeyListener(enterListener);
        campoSenha.addKeyListener(enterListener);
    }

    // Método para estilizar campos
    private void estilizarCampo(JTextField campo) {
        campo.setBackground(new Color(45, 45, 45));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createLineBorder(douradoSuave, 1));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    // Método para criar botões com estilo
    private JButton criarBotao(String texto, Color corBase) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setBackground(corBase);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        botao.setPreferredSize(new Dimension(120, 35));

        // efeito hover
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(corBase.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(corBase);
            }
        });

        return botao;
    }

    // Lógica de login
    private void realizarLogin() {
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim();

        try {
            Object resultado = controller.autenticarUsuario(login, senha);

            if (resultado instanceof String && resultado.equals("admin")) {
                JOptionPane.showMessageDialog(this, "Bem-vindo, Administrador!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                new FuncionarioMenu().setVisible(true);
                dispose();
            } else if (resultado instanceof Cliente cliente) {
                JOptionPane.showMessageDialog(this, "Bem-vindo, " + cliente.getNome() + "!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                new ClienteMenu(cliente).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos!",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao tentar logar: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginTela().setVisible(true));
    }
}