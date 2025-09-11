package app;

import model.ContaCorrente;
import service.ContaService;
import exception.SaldoInsuficienteException;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        ContaService cs = new ContaService();

        try {
           
            List<ContaCorrente> todasContas = cs.lerConta("conta.txt");

            
            StringBuilder sbTodas = new StringBuilder();
            todasContas.forEach(c -> sbTodas.append(
                    "Número: " + c.getNumero() +
                    " | Titular: " + c.getTitular() +
                    " | Saldo: R$ " + String.format("%.2f", c.getSaldo()) + "\n"
            ));
            JOptionPane.showMessageDialog(null, sbTodas.toString(), "Todas as contas", JOptionPane.INFORMATION_MESSAGE);

            // conta>1000
            List<ContaCorrente> contasFiltradas = cs.contas1000("conta.txt");
            StringBuilder sbFiltradas = new StringBuilder();
            contasFiltradas.forEach(c -> sbFiltradas.append(
                    "Número: " + c.getNumero() +
                    " | Titular: " + c.getTitular() +
                    " | Saldo: R$ " + String.format("%.2f", c.getSaldo()) + "\n"
            ));
            JOptionPane.showMessageDialog(null, sbFiltradas.toString(), "Contas com saldo > 1000", JOptionPane.INFORMATION_MESSAGE);

            // maior
            Optional<ContaCorrente> maiorDelas = cs.maiorConta("conta.txt");
            StringBuilder sbMaior = new StringBuilder();
            maiorDelas.ifPresent(c -> sbMaior.append(
                    "Número: " + c.getNumero() +
                    " | Titular: " + c.getTitular() +
                    " | Saldo: R$ " + String.format("%.2f", c.getSaldo()) + "\n"
            ));
            if (maiorDelas.isPresent()) {
                JOptionPane.showMessageDialog(null, sbMaior.toString(), "Conta com maior saldo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Não há contas cadastradas.", "Conta com maior saldo", JOptionPane.INFORMATION_MESSAGE);
            }

            // Seleciona a conta pelo número
            String numeroStr = JOptionPane.showInputDialog("Informe o número da conta que deseja acessar:");
            int numeroConta = Integer.parseInt(numeroStr);
            ContaCorrente contaSelect = todasContas.stream()
                    .filter(c -> c.getNumero() == numeroConta)
                    .findFirst()
                    .orElse(null);

            if (contaSelect != null) {
                JOptionPane.showMessageDialog(null,
                        "Conta encontrada!\nNúmero: " + contaSelect.getNumero() +
                        "\nTitular: " + contaSelect.getTitular() +
                        "\nSaldo: R$ " + String.format("%.2f", contaSelect.getSaldo())
                );
            } else {
                JOptionPane.showMessageDialog(null, "Erro, não existe conta com esse número");
                return;
            }

            // Solicita valor para saque
            String valorStr = JOptionPane.showInputDialog("Informe o valor para saque:");
            double valor = Double.parseDouble(valorStr);

            try {
                cs.sacarValor(contaSelect, valor); // realiza saque na conta selecionada
                JOptionPane.showMessageDialog(null, "Saque realizado com sucesso!");
            } catch (SaldoInsuficienteException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }

            // Atualiza arquivo com todas as contas
            cs.atualizarConta(todasContas, "conta_atualizada.txt");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao acessar arquivo: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
