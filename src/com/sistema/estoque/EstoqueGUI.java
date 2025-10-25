package com.sistema.estoque;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class EstoqueGUI extends JFrame {
    private final ProdutoDAO dao = new ProdutoDAO();
    private final DefaultListModel<Produto> listModel = new DefaultListModel<>();
    private JList<Produto> list;
    private JTextField txtNome, txtPreco, txtQtd, txtCategoria;
    private String selectedId = null;

    public EstoqueGUI() {
        setTitle("Sistema de Estoque — MongoDB + Redis");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 560);
        setLocationRelativeTo(null);

        var root = new JPanel(new BorderLayout(12,12));
        root.setBorder(new EmptyBorder(12,12,12,12));
        setContentPane(root);

        var form = new JPanel(new GridLayout(4,2,8,8));
        form.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        form.add(txtNome);

        form.add(new JLabel("Preço:"));
        txtPreco = new JTextField();
        form.add(txtPreco);

        form.add(new JLabel("Quantidade:"));
        txtQtd = new JTextField();
        form.add(txtQtd);

        form.add(new JLabel("Categoria:"));
        txtCategoria = new JTextField();
        form.add(txtCategoria);

        var btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        var btnAdd = new JButton("Adicionar");
        var btnUpdate = new JButton("Atualizar");
        var btnDelete = new JButton("Excluir");
        var btnRefresh = new JButton("Recarregar");
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);

        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(e -> {
            Produto p = list.getSelectedValue();
            if (p != null) {
                selectedId = p.getId();
                txtNome.setText(p.getNome());
                txtPreco.setText(String.valueOf(p.getPreco()));
                txtQtd.setText(String.valueOf(p.getQuantidade()));
                txtCategoria.setText(p.getCategoria());
            }
        });

        root.add(form, BorderLayout.NORTH);
        root.add(new JScrollPane(list), BorderLayout.CENTER);
        root.add(btnPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(this::onAdd);
        btnUpdate.addActionListener(this::onUpdate);
        btnDelete.addActionListener(this::onDelete);
        btnRefresh.addActionListener(e -> loadProdutos());

        loadProdutos();
    }

    private void onAdd(ActionEvent e) {
        try {
            Produto p = new Produto(
                    txtNome.getText(),
                    Double.parseDouble(txtPreco.getText()),
                    Integer.parseInt(txtQtd.getText()),
                    txtCategoria.getText()
            );
            dao.criar(p);
            clearForm();
            loadProdutos();
            JOptionPane.showMessageDialog(this, "Produto adicionado!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdate(ActionEvent e) {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na lista.");
            return;
        }
        try {
            Produto p = new Produto(
                    txtNome.getText(),
                    Double.parseDouble(txtPreco.getText()),
                    Integer.parseInt(txtQtd.getText()),
                    txtCategoria.getText()
            );
            boolean ok = dao.atualizar(selectedId, p);
            if (ok) {
                clearForm();
                loadProdutos();
                JOptionPane.showMessageDialog(this, "Produto atualizado!");
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível atualizar.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete(ActionEvent e) {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na lista.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Excluir este produto?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = dao.excluir(selectedId);
            if (ok) {
                clearForm();
                loadProdutos();
                JOptionPane.showMessageDialog(this, "Produto excluído!");
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível excluir.");
            }
        }
    }

    private void loadProdutos() {
        listModel.clear();
        List<Produto> produtos = dao.listarTodos();
        for (Produto p : produtos) listModel.addElement(p);
    }

    private void clearForm() {
        selectedId = null;
        txtNome.setText("");
        txtPreco.setText("");
        txtQtd.setText("");
        txtCategoria.setText("");
        list.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EstoqueGUI().setVisible(true));
    }
}
