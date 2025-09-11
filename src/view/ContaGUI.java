package view;

import model.ContaCorrente;
import service.ContaService;
import dao.ContaCorrenteDao;
import exception.SaldoInsuficienteException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContaGUI extends JFrame {

    private ContaService cs = new ContaService();
    private ContaCorrenteDao dao = new ContaCorrenteDao();
    private List<ContaCorrente> contas;

   
public ContaGUI() {
    super("Sistema de Contas");
    setSize(400, 200);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    try {
        contas = cs.lerConta("conta.txt");
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Erro ao acessar arquivo: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        contas = new ArrayList<>();
    }


    for (ContaCorrente c : contas) {
        try {
            dao.inserir(c);
        } catch (Exception ex) {
            System.out.println("Conta " + c.getNumero() + " já está no banco.");
        }
    }

    setLayout(new BorderLayout());

    JPanel painel = new JPanel(new GridLayout(2, 1, 10, 10));
    JButton btnConsultar = new JButton("Consultar Conta");
    JButton btnAdicionar = new JButton("Adicionar Conta");

    painel.add(btnConsultar);
    painel.add(btnAdicionar);

    add(painel, BorderLayout.CENTER);

    btnConsultar.addActionListener(e -> consultarConta());
    btnAdicionar.addActionListener(e -> adicionarConta());
}

    private void consultarConta() {
        String input = JOptionPane.showInputDialog(this, "Número da conta:");
        if (input == null) return;

        try {
            int numero = Integer.parseInt(input);
            ContaCorrente conta = contas.stream()
                    .filter(c -> c.getNumero() == numero)
                    .findFirst()
                    .orElse(null);

            if (conta == null) {
                JOptionPane.showMessageDialog(this, "Conta não encontrada!");
                return;
            }

            JPanel painel = new JPanel(new GridLayout(4, 2, 5, 5));
            painel.add(new JLabel("Número:"));
            painel.add(new JLabel(String.valueOf(conta.getNumero())));
            painel.add(new JLabel("Titular:"));
            painel.add(new JLabel(conta.getTitular()));
            painel.add(new JLabel("Saldo:"));
            JLabel lblSaldo = new JLabel(String.format("R$ %.2f", conta.getSaldo()));
            painel.add(lblSaldo);

            JButton btnSacar = new JButton("Sacar");
            JButton btnDepositar = new JButton("Depositar");
            painel.add(btnSacar);
            painel.add(btnDepositar);

            JDialog dialog = new JDialog(this, "Conta " + conta.getNumero(), true);
            dialog.setSize(300, 200);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            dialog.add(painel, BorderLayout.CENTER);

            btnSacar.addActionListener(ev -> {
                String valorStr = JOptionPane.showInputDialog(dialog, "Valor para saque:");
                if (valorStr == null) return;
                try {
                    double valor = Double.parseDouble(valorStr);
                    cs.sacarValor(conta, valor);
                    dao.atualizarSaldo(conta.getNumero(), conta.getSaldo());
                    cs.atualizarConta(contas, "conta.txt");
                    lblSaldo.setText(String.format("R$ %.2f", conta.getSaldo()));
                    JOptionPane.showMessageDialog(dialog, "Saque realizado com sucesso!");
                } catch (SaldoInsuficienteException ex) {
                    JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });

            btnDepositar.addActionListener(ev -> {
                String valorStr = JOptionPane.showInputDialog(dialog, "Valor para depósito:");
                if (valorStr == null) return;
                try {
                    double valor = Double.parseDouble(valorStr);
                    cs.depositar(conta, valor);
                    dao.atualizarSaldo(conta.getNumero(), conta.getSaldo());
                    cs.atualizarConta(contas, "conta.txt");
                    lblSaldo.setText(String.format("R$ %.2f", conta.getSaldo()));
                    JOptionPane.showMessageDialog(dialog, "Depósito realizado com sucesso!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });

            dialog.setVisible(true);

          } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Número inválido!");
        }
    }

    private void adicionarConta() {
        JTextField txtNumero = new JTextField();
        JTextField txtTitular = new JTextField();
        JTextField txtSaldo = new JTextField();

        JPanel painel = new JPanel(new GridLayout(3, 2, 5, 5));
        painel.add(new JLabel("Número da conta:"));
        painel.add(txtNumero);
        painel.add(new JLabel("Titular:"));
        painel.add(txtTitular);
        painel.add(new JLabel("Saldo inicial:"));
        painel.add(txtSaldo);

        int result = JOptionPane.showConfirmDialog(this, painel,
                "Adicionar Nova Conta", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int numero = Integer.parseInt(txtNumero.getText().trim());
                String titular = txtTitular.getText().trim();
                double saldo = Double.parseDouble(txtSaldo.getText().trim());

                boolean existe = contas.stream().anyMatch(c -> c.getNumero() == numero);
                if (existe) {
                    JOptionPane.showMessageDialog(this, "Número de conta já existe!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ContaCorrente conta = new ContaCorrente(numero, titular, saldo);
                contas.add(conta);
                cs.atualizarConta(contas, "conta.txt");
                dao.inserir(conta);

                JOptionPane.showMessageDialog(this, "Conta adicionada com sucesso!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Número ou saldo inválidos!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ContaGUI().setVisible(true));
    }
}
