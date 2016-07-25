package br.inf.ufg.sempreufg.auxiliar;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *Classe utilizada leitura dos arquivos a serem importados.
 */
public class ArquivoParaImportar {

    private static ArquivoLog arquivoLog = new ArquivoLog();
    private static Strings strings = new Strings();
    private static List<String> arquivo = new ArrayList<>();

    public ArquivoParaImportar() {
        BufferedReader bufferedReader = readFileTXT();
        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                arquivoLog.GravaMensagemDeErro(line);
                try {
                    arquivo.add(line);
                } catch (NullPointerException e) {}
            }
        } catch (IOException e) {
            arquivoLog.GravaMensagemDeErro(e.getMessage());
        }
    }

    private static BufferedReader readFileTXT() {
        Scanner scanner = new Scanner(System.in);
        String address = scanner.nextLine();

        if ((address.equals("")) || (address == null)) {
            address = strings.nomeArquivoImportar;
        } else if(address.substring(0, 1).equals(".")) {
            File f = new File(".");
            address = buscaEnderecoArquivo(f.getAbsolutePath())
                    + address.substring(2,address.length())
                    + strings.barraLateralEnderecoArquivo
                    + strings.nomeArquivoImportar;
        } else {
            address = address
                    + strings.barraLateralEnderecoArquivo
                    + strings.nomeArquivoImportar;
        }

        try {
            FileReader fileInputStream = new FileReader(address);
            BufferedReader bufferedReader = new BufferedReader(fileInputStream);
            return bufferedReader;
        } catch (FileNotFoundException e) {
            arquivoLog.GravaMensagemDeErro(e.getMessage());
        }
        return null;
    }

    private static String buscaEnderecoArquivo(String endereco) {
        return endereco.substring(0,(endereco.length()-1));
    }

    public static List<String> GetArquivoParaImportar(){
        return arquivo;
    }
}
