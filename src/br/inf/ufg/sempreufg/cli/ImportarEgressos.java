package br.inf.ufg.sempreufg.cli;

import br.inf.ufg.sempreufg.conexao.Conexao;
import sun.dc.pr.PRError;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class ImportarEgressos {

    private static Connection connection;
    private static PreparedStatement preparedStatement;

    private static void createConnection() {
        try {
            connection = Conexao.instanciaConexao();
            preparedStatement =  connection.prepareStatement(
                    "INSERT INTO public.area_conhecimento(" +
                            "arco_arc_id, arco_nome_area, arco_codigo_area)" +
                            "VALUES (?, ?, ?);");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static BufferedReader readFileTXT() {
        Scanner scanner = new Scanner(System.in);
        String address = scanner.nextLine();

        try {
            FileReader fileInputStream = new FileReader(address);
            BufferedReader bufferedReader = new BufferedReader(fileInputStream);
            return bufferedReader;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void recordBD(String recordAreaConhecimento) {

        String enter = recordAreaConhecimento;
        enter = enter.substring(6,enter.length());
        String[] parameters = enter.split(";",numberOfFields(enter));

        try {
            createConnection();
            preparedStatement.setInt(1,Integer.parseInt(parameters[0]));
            preparedStatement.setString(2,parameters[1]);
            preparedStatement.setInt(3,Integer.parseInt(parameters[2]));
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

    public static void main(String[] args) {
        BufferedReader bufferedReader = readFileTXT();
        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                recordBD(line);
                try {
                    line = bufferedReader.readLine();
                } catch (NullPointerException e) {}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
