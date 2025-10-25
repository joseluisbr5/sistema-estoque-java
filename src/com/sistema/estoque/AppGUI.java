package com.sistema.estoque;

import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AppGUI extends JFrame {
    private final ProdutoDAO dao = new ProdutoDAO();
    private final AuthService authService = new AuthService();
    private String usuarioAtual = null;
    private String tokenAtual = null;

    private JTextField nomeField, precoField, qtdField;
    private JComboBox<String> categoriaBox;
    private JTable tabela;
    private JButton delBtn;
    private String idSelecionado = null;

    public AppGUI() {
        super("📦 Sistema de Estoque — MongoDB + Redis");
        FlatMacLightLaf.setup();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 580);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        add(criarPainelLogin(), BorderLayout.CENTER);
    }

    private JPanel criarPainelLogin() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel label = new JLabel("👋 Bem-vindo! Faça login para acessar o sistema");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(label, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("Usuário:"), gbc);
        JTextField userField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(userField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton btnLogin = new JButton("Entrar");
        estilizarBotao(btnLogin, new Color(33, 150, 243));
        btnLogin.addActionListener(e -> {
            String user = userField.getText().trim();
            if (user.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Digite o nome de usuário!");
                return;
            }
            tokenAtual = authService.login(user);
            usuarioAtual = user;
            JOptionPane.showMessageDialog(this, "✅ Sessão iniciada! Bem-vindo, " + user);
            getContentPane().removeAll();
            add(criarPainelPrincipal(), BorderLayout.CENTER);
            revalidate();
            repaint();
        });
        panel.add(btnLogin, gbc);

        return panel;
    }

    private JPanel criarPainelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // CABEÇALHO
        JPanel header = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("📦 Sistema de Estoque");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(titulo, BorderLayout.WEST);

        JButton logout = new JButton("Sair");
        estilizarBotao(logout, new Color(244, 67, 54));
        logout.addActionListener(e -> {
            authService.logout(usuarioAtual);
            usuarioAtual = null;
            tokenAtual = null;
            JOptionPane.showMessageDialog(this, "Sessão encerrada!");
            getContentPane().removeAll();
            add(criarPainelLogin(), BorderLayout.CENTER);
            revalidate();
            repaint();
        });
        header.add(logout, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // FORMULÁRIO DE CADASTRO
        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Novo Produto"));

        form.add(new JLabel("Nome:"));
        nomeField = new JTextField();
        form.add(nomeField);

        form.add(new JLabel("Preço (R$):"));
        precoField = new JTextField();
        form.add(precoField);

        form.add(new JLabel("Quantidade:"));
        qtdField = new JTextField();
        form.add(qtdField);

        form.add(new JLabel("Categoria:"));
        categoriaBox = new JComboBox<>(new String[]{
                "Periféricos 🖱️",
                "Hardware 💻",
                "Monitores 🖥️",
                "Celulares 📱",
                "Acessórios 🎧",
                "Outros 📦"
        });
        form.add(categoriaBox);

        JButton addBtn = new JButton("➕ Adicionar Produto");
        estilizarBotao(addBtn, new Color(46, 204, 113));
        addBtn.addActionListener(e -> adicionarProduto());
        form.add(addBtn);

        panel.add(form, BorderLayout.WEST);

        // TABELA DE PRODUTOS
        String[] colunas = {"ID", "Nome", "Preço", "Qtd", "Categoria"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        tabela = new JTable(model);
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() != -1) {
                idSelecionado = tabela.getValueAt(tabela.getSelectedRow(), 0).toString();
                delBtn.setEnabled(true);
                delBtn.setText("🗑️ Excluir Produto (" + idSelecionado.substring(0, 6) + "...)");
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Lista de Produtos"));
        panel.add(scroll, BorderLayout.CENTER);

        // RODAPÉ COM BOTÕES
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton listarBtn = new JButton("🔄 Atualizar Lista");
        estilizarBotao(listarBtn, new Color(33, 150, 243));
        listarBtn.addActionListener(e -> listarProdutos());
        bottomPanel.add(listarBtn);

        delBtn = new JButton("🗑️ Excluir Produto");
        estilizarBotao(delBtn, new Color(255, 87, 34));
        delBtn.setEnabled(false);
        delBtn.addActionListener(e -> excluirProduto());
        bottomPanel.add(delBtn);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        listarProdutos();
        return panel;
    }

    private void estilizarBotao(JButton btn, Color cor) {
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(cor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(cor);
            }
        });
    }

    private void adicionarProduto() {
        if (!authService.verificarSessao(usuarioAtual, tokenAtual)) {
            JOptionPane.showMessageDialog(this, "Sessão expirada! Faça login novamente.");
            return;
        }

        try {
            String nome = nomeField.getText().trim();
            double preco = Double.parseDouble(precoField.getText().trim());
            int qtd = Integer.parseInt(qtdField.getText().trim());
            String cat = (String) categoriaBox.getSelectedItem();

            Produto p = new Produto(nome, preco, qtd, cat);
            dao.criar(p);
            JOptionPane.showMessageDialog(this, "✅ Produto adicionado com sucesso!");
            listarProdutos();

            nomeField.setText("");
            precoField.setText("");
            qtdField.setText("");
            categoriaBox.setSelectedIndex(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar produto: " + ex.getMessage());
        }
    }

    private void listarProdutos() {
        DefaultTableModel model = (DefaultTableModel) tabela.getModel();
        model.setRowCount(0);
        List<Produto> produtos = dao.listarTodos();
        for (Produto p : produtos) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getNome(),
                    String.format("R$ %.2f", p.getPreco()),
                    p.getQuantidade(),
                    p.getCategoria()
            });
        }
        idSelecionado = null;
        delBtn.setEnabled(false);
        delBtn.setText("🗑️ Excluir Produto");
    }

    private void excluirProduto() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela!");
            return;
        }

        int opt = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o produto selecionado?",
                "Confirmação", JOptionPane.YES_NO_OPTION);

        if (opt == JOptionPane.YES_OPTION) {
            boolean ok = dao.excluir(idSelecionado);
            if (ok) {
                JOptionPane.showMessageDialog(this, "🗑️ Produto excluído com sucesso!");
                listarProdutos();
            } else {
                JOptionPane.showMessageDialog(this, "Produto não encontrado!");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppGUI().setVisible(true));
    }
}
