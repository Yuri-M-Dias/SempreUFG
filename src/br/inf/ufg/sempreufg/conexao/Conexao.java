package br.inf.ufg.sempreufg.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de conexão com o Postgree SQL
 */
public class Conexao {

    private static Connection conexao = null;

    /**
     * Inicia uma conexão com o banco de dados
     */
    public static Connection instanciaConexao() {
        if (conexao == null) {
            try {
                Class.forName("org.postgresql.Driver");
                conexao = DriverManager.getConnection("jdbc:postgresql://localhost:5433/SempreUFG", "postgres", "Mv@@1106");
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return conexao;
    }
}