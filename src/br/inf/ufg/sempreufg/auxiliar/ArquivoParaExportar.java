package br.inf.ufg.sempreufg.auxiliar;

import br.inf.ufg.sempreufg.model.CursoUFG;

import java.sql.*;

//import com.google.gson.Gson;

public class ArquivoParaExportar {

    Connection connect;

    public ArquivoParaExportar(Connection c) {
        this.connect = c;
    }

    public String esportarAquivo() throws SQLException {
        Statement st = connect.createStatement();
        String busca = "15";
        String json = "";
        try (ResultSet rs = st.executeQuery("SELECT \n"
                + "  curso_ufg.cufg_id, \n"
                + "  curso_ufg.uafg_id, \n"
                + "  curso_ufg.arco_id, \n"
                + "  curso_ufg.cufg_nome, \n"
                + "  curso_ufg.cufg_nivel, \n"
                + "  curso_ufg.cufg_data_criacao, \n"
                + "  curso_ufg.cufg_presencial, \n"
                + "  curso_ufg.curs_turno\n"
                + "FROM \n"
                + "  public.curso_ufg WHERE cufg_id = " + busca + ";")) {
            ResultSetMetaData rsmd = rs.getMetaData();

            String cufg_id, uafg_id, arco_id, cufg_nome, cufg_nivel, curs_turno;
            boolean cufg_presencial;
            Date cufg_data_criacao;
            while (rs.next()) {
                cufg_id = rs.getString(1);
                uafg_id = rs.getString(2);
                arco_id = rs.getString(3);
                cufg_nome = rs.getString(4);
                cufg_nivel = rs.getString(5);
                cufg_data_criacao = rs.getDate(6);
                cufg_presencial = rs.getBoolean(7);
                curs_turno = rs.getString(8);
                System.out.println(cufg_id);
                System.out.println(uafg_id);
                System.out.println(arco_id);
                System.out.println(cufg_nome);
                System.out.println(cufg_nivel);
                System.out.println(cufg_presencial);
                System.out.println(cufg_data_criacao);
                System.out.println(curs_turno);
                System.out.println("");
                CursoUFG curso = new CursoUFG(cufg_id, uafg_id, arco_id, cufg_nome, cufg_nivel, curs_turno, cufg_presencial, cufg_data_criacao);
                //Gson gson = new Gson();
                //json = gson.toJson(curso);
                System.out.println(json);
            }
        }
        st.close();
      return json;
    }
}
