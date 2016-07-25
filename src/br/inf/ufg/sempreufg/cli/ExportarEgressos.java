package br.inf.ufg.sempreufg.cli;


import br.inf.ufg.sempreufg.auxiliar.ArquivoParaExportar;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ExportarEgressos {
    
    private static Connection c;

    public static void main(String[] args) throws SQLException, InterruptedException, FileNotFoundException, UnsupportedEncodingException {
        PostgreSQLJDBC();
        ArquivoParaExportar arq = new ArquivoParaExportar(c);
        
        PrintWriter pw = new PrintWriter(args[0], "UTF-8");
        pw.write(arq.esportarAquivo());
        pw.close();
    }
    
    public static void PostgreSQLJDBC (){
      try {
         c = DriverManager
            .getConnection("jdbc:postgresql://localhost:5432/teste",
            "usuario", "senha");
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println(e.getClass().getName()+": "+e.getMessage());
         System.exit(0);
      }
      System.out.println("Opened database successfully");
   }
}