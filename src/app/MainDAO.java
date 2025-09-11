package app;

import model.ContaCorrente;
import service.ContaService;
import dao.ContaCorrenteDao;
import exception.SaldoInsuficienteException;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class MainDAO {

    public static void main(String[] args) {
        ContaService cs = new ContaService();       
        ContaCorrenteDao dao = new ContaCorrenteDao(); 

        try {
           
            List<ContaCorrente> contas = cs.lerConta("conta.txt");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao acessar arquivo: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        while (true) {
            String opcao = JOptionPane.showInputDialog(
                    "Escolha uma operação:\n" +
                    "1 - Adicionar Conta\n" +
                    "2 - Sacar\n" +
                    "3 - Depositar\n" +
                    "4 - Listar Contas\n" +
                    "5 - Sair"
            );

            if (opcao == null) break; // Cancelou

            try {
                List<ContaCorrente> contas = cs.lerConta("conta.txt"); 

                if (opcao.equals("1")) { // Adicionar Conta
                    int numero = Integer.parseInt(JOptionPane.showInputDialog("Número da Conta:"));

           
                   

                    String titular = JOptionPane.showInputDialog("Nome do Titular:");
                    double saldo = Double.parseDouble(JOptionPane.showInputDialog("Saldo Inicial:"));

                    ContaCorrente nova = new ContaCorrente(numero, titular, saldo);
                    contas.add(nova);

                    cs.atualizarConta(contas, "conta.txt"); 
                    dao.inserir(nova); 

                    JOptionPane.showMessageDialog(null, "Conta adicionada com sucesso!");

                } else if (opcao.equals("2")) { // Sacar
                    int numero = Integer.parseInt(JOptionPane.showInputDialog("Número da conta:"));
                    ContaCorrente conta = contas.stream()
                            .filter(c -> c.getNumero() == numero)
                            .findFirst().orElse(null);

                    if (conta == null) {
                        JOptionPane.showMessageDialog(null, "Conta não encontrada!");
                        continue;
                    }

                    double valor = Double.parseDouble(JOptionPane.showInputDialog("Valor para saque:"));
                    try {
                        cs.sacarValor(conta, valor);
                        dao.atualizarSaldo(conta.getNumero(), conta.getSaldo());
                        cs.atualizarConta(contas, "conta.txt");

                        JOptionPane.showMessageDialog(null, "Saque realizado com sucesso!");
                    } catch (SaldoInsuficienteException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }

                } else if (opcao.equals("3")) { // Depositar
                    int numero = Integer.parseInt(JOptionPane.showInputDialog("Número da conta:"));
                    ContaCorrente conta = contas.stream()
                            .filter(c -> c.getNumero() == numero)
                            .findFirst().orElse(null);

                    if (conta == null) {
                        JOptionPane.showMessageDialog(null, "Conta não encontrada!");
                        continue;
                    }

                    double valor = Double.parseDouble(JOptionPane.showInputDialog("Valor para depósito:"));
                    cs.depositar(conta, valor);
                    dao.atualizarSaldo(conta.getNumero(), conta.getSaldo());
                    cs.atualizarConta(contas, "conta.txt");

                    JOptionPane.showMessageDialog(null, "Depósito realizado com sucesso!");

                } else if (opcao.equals("4")) { // Listar Contas
                    StringBuilder sb = new StringBuilder();
                    contas.forEach(c -> sb.append("Número: ").append(c.getNumero())
                            .append(" | Titular: ").append(c.getTitular())
                            .append(" | Saldo: R$ ").append(String.format("%.2f", c.getSaldo()))
                            .append("\n"));
                    JOptionPane.showMessageDialog(null, sb.toString(), "Todas as Contas", JOptionPane.INFORMATION_MESSAGE);

                } else if (opcao.equals("5")) { // Sair
                    break;

                } else {
                    JOptionPane.showMessageDialog(null, "Opção inválida!");
                }

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Erro ao acessar arquivo: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
