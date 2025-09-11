/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import exception.SaldoInsuficienteException;
import model.Conta;
import model.ContaCorrente;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Vitor
 */
public class ContaService {
    


    public List<ContaCorrente> lerConta(String caminho) throws IOException {
        try{
            Path arquivo = Paths.get(caminho);
           
            List<String> linhas = Files.readAllLines(arquivo);
            
            List<ContaCorrente> contas = new ArrayList<>();

          
             for (String linha : linhas) {
           
            String[] partes = linha.split(",");
               
            int numero = Integer.parseInt(partes[0].trim());
            String titular = partes[1].trim();
            double saldo = Double.parseDouble(partes[2].trim());

            ContaCorrente conta = new ContaCorrente(numero, titular, saldo);
            contas.add(conta);
        }
        return contas;
            


        }catch(IOException e){
            System.out.println("Erro ao ler arquivo");
            throw e;
        }
    }

    public void sacarValor(ContaCorrente conta, double valor) throws SaldoInsuficienteException {
    if (valor > conta.getSaldo()) {
        throw new SaldoInsuficienteException("Saldo insuficiente para saque.");
    }
    conta.setSaldo(conta.getSaldo() - valor);
    }
    public void depositar(ContaCorrente conta, double valor){
    conta.setSaldo(conta.getSaldo() + valor);
    }

    public void atualizarConta(List<ContaCorrente> contas, String caminho) throws IOException {
    Path arquivo = Paths.get(caminho);

   
    Files.write(arquivo, new byte[0]);

    for (ContaCorrente conta : contas) {
        String linha = conta.getNumero() + "," + conta.getTitular() + "," + conta.getSaldo() + "\n";
        Files.write(arquivo, linha.getBytes(), java.nio.file.StandardOpenOption.APPEND);
    }
    
    
    }

   
    public List<ContaCorrente> contas1000(String caminho) throws IOException{
        ContaService cs = new ContaService();
       
        List<ContaCorrente> todas = lerConta("conta.txt");

        List<ContaCorrente> filt = todas.stream()
                            .filter(c -> c.getSaldo() > 1000)
                            .collect(Collectors.toList());
                            
        return filt;

    }
    public Optional<ContaCorrente> maiorConta(String caminho) throws IOException {
    List<ContaCorrente> todas = lerConta(caminho);
    return todas.stream()
                .reduce((c1, c2) -> c1.getSaldo() >= c2.getSaldo() ? c1 : c2);
               
}
   

}
