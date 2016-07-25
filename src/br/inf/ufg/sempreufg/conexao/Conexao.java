package br.inf.ufg.sempreufg.conexao;

import br.inf.ufg.sempreufg.auxiliar.ArquivoLog;
import br.inf.ufg.sempreufg.auxiliar.Parametros;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Classe de conexão com o Postgree SQL
 */
public class Conexao {

    private static PreparedStatement preparedStatement;
    private static Connection conexao = null;
    private static ArquivoLog arquivoLog = new ArquivoLog();

    /**
     * Inicia uma conexão com o banco de dados
     */
    public Conexao() {
        if (conexao == null) {
            try {
                Class.forName(Parametros.nomeClassDriverConexaoPostgres);
                conexao = DriverManager.getConnection("" +
                                Parametros.enderecoPostgres,
                        Parametros.nomeLoginPostgres,
                        Parametros.senhaLoginPostgres);
            } catch (ClassNotFoundException e) {
                ArquivoLog.GravaMensagemDeErro(e.getMessage());
            } catch (SQLException e) {
                ArquivoLog.GravaMensagemDeErro(e.getMessage());
            }
        }
    }

    /**
     * Retorna a conexão com o BD.
     * @return conexão com o BD.
     */
    public Connection getConexao() {
        return conexao;
    }
}
