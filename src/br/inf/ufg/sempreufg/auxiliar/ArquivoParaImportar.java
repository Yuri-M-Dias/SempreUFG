package br.inf.ufg.sempreufg.auxiliar;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilizada leitura dos arquivos a serem importados.
 */
public class ArquivoParaImportar {

    private static ArquivoLog arquivoLog = new ArquivoLog();
    private static List<String> arquivo = new ArrayList<>();

    public ArquivoParaImportar(String caminhoArquivo) {
        BufferedReader bufferedReader = readFileTXT(caminhoArquivo);
        if (bufferedReader == null) {
            arquivoLog.GravaMensagemDeErro("Erro na abertura do arquivo de Log.");
        }
        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                arquivo.add(line);
                line = bufferedReader.readLine();
                try {
                    arquivo.add(line);
                } catch (NullPointerException e) {
                }
            }
        } catch (IOException e) {
            ArquivoLog.GravaMensagemDeErro(e.getMessage());
        }
    }

    private static BufferedReader readFileTXT(String caminhoArquivo) {
        if (caminhoArquivo == null || "".equals(caminhoArquivo)) {
            caminhoArquivo = Parametros.nomeArquivoImportar;
        } else if (caminhoArquivo.startsWith(".")) {
            File f = new File(".");
            caminhoArquivo = buscaEnderecoArquivo(f.getAbsolutePath())
                    + caminhoArquivo.substring(2, caminhoArquivo.length())
                    + Parametros.barraLateralEnderecoArquivo
                    + Parametros.nomeArquivoImportar;
        } else {
            caminhoArquivo = caminhoArquivo
                    + Parametros.barraLateralEnderecoArquivo
                    + Parametros.nomeArquivoImportar;
        }
        try {
            FileReader fileInputStream = new FileReader(caminhoArquivo);
            BufferedReader bufferedReader = new BufferedReader(fileInputStream);
            return bufferedReader;
        } catch (FileNotFoundException e) {
            ArquivoLog.GravaMensagemDeErro(e.getMessage());
        }
        return null;
    }

    private static String buscaEnderecoArquivo(String endereco) {
        return endereco.substring(0, (endereco.length() - 1));
    }

    public static List<String> GetArquivoParaImportar() {
        return arquivo;
    }
}
