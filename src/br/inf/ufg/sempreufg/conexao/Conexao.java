package br.inf.ufg.sempreufg.conexao;

import br.inf.ufg.sempreufg.auxiliar.ArquivoLog;
import br.inf.ufg.sempreufg.auxiliar.Strings;

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
                Class.forName(Strings.nomeClassDriverConexaoPostgres);
                conexao = DriverManager.getConnection("" +
                                Strings.enderecoPostgres,
                        Strings.nomeLoginPostgres,
                        Strings.senhaLoginPostgres);
            } catch (ClassNotFoundException e) {
                ArquivoLog.GravaMensagemDeErro(e.getMessage());
            } catch (SQLException e) {
                ArquivoLog.GravaMensagemDeErro(e.getMessage());
            }
        }
        createConnection();
    }

    public Connection getConexao() {
        return conexao;
    }

    private static void createConnection() {
        try {
            preparedStatement = conexao.prepareStatement(
                    "INSERT INTO public.area_conhecimento(" +
                            "arco_arc_id, arco_nome_area, arco_codigo_area)" +
                            "VALUES (?, ?, ?);");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void recordBD(String recordAreaConhecimento) {
        String enter = recordAreaConhecimento;
        enter = enter.substring(6, enter.length());
        String[] parameters = enter.split(";", numberOfFields(enter));

        try {
            createConnection();
            preparedStatement.setInt(1, Integer.parseInt(parameters[0]));
            preparedStatement.setString(2, parameters[1]);
            preparedStatement.setInt(3, Integer.parseInt(parameters[2]));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static int numberOfFields(String string) {
        int count = 1;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == ';') {
                count++;
            }
        }
        return count;
    }
}
