package br.inf.ufg.sempreufg.cli;

import br.inf.ufg.sempreufg.auxiliar.ArquivoParaImportar;
import br.inf.ufg.sempreufg.conexao.Conexao;

import java.util.List;

public class ImportarEgressos {

    private static Conexao conexao = null;
    private static ArquivoParaImportar arquivoParaImportar = null;

    public static void main(String[] args) {
        conexao = new Conexao();
        arquivoParaImportar = new ArquivoParaImportar();
        List<String> arquivo = ArquivoParaImportar.GetArquivoParaImportar();
        for (String string: arquivo) {
            conexao.recordBD(string);
        }
    }
}
