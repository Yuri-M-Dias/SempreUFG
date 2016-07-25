package br.inf.ufg.sempreufg.auxiliar;

import sun.print.PrinterJobWrapper;

import java.io.*;

/**
 * Classe que manipula os registros de Log da aplicação.
 */
public class ArquivoLog {

    private static PrintWriter printWriter = null;

    private static void AbreLog() {
        try {
            FileWriter fileWriter = new FileWriter(Strings.nomeArquivoLog,true);
            printWriter = new PrintWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void FinalizaLog() {
        printWriter.close();
    }

    public static void GravaMensagemDeErro(String erro){
        AbreLog();
        printWriter.println("Erro: " + erro);
        FinalizaLog();
    }

    public static void GravaDadoInconsistente(String dado) {
        AbreLog();
        printWriter.println("Dado " + dado + " inconsistente");
        FinalizaLog();
    }

}
