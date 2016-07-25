package br.inf.ufg.sempreufg.cli;

import br.inf.ufg.sempreufg.auxiliar.ArquivoParaImportar;
import br.inf.ufg.sempreufg.conexao.Conexao;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ImportarEgressos {

    private static ArquivoParaImportar arquivoParaImportar = null;
    private static Connection conexaoSQL = null;

    public static void main(String[] args) {
        String caminho = "";
        if (args.length > 0) {
            caminho = args[0];
        }
        Conexao conexao = new Conexao();
        arquivoParaImportar = new ArquivoParaImportar(caminho);
        List<String> arquivo = ArquivoParaImportar.GetArquivoParaImportar();
        conexaoSQL = conexao.getConexao();
        if (conexaoSQL == null) {
            throw new IllegalArgumentException("Problema ao conseguir a conexão com " +
                    "o BD.");
        }
        try {
            conexaoSQL.setAutoCommit(false);
            for (String registro : arquivo) {
                if (registro.startsWith("Reg.1")) {
                    inserirReg1(registro);
                } else if (registro.startsWith("Reg.2")) {
                    inserirReg2(registro);
                } else {
                    throw new IllegalArgumentException("Arquivo com formato errado.");
                }
            }
            conexaoSQL.commit();
        } catch (Exception e) {
            try {
                conexaoSQL.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                conexaoSQL.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void inserirReg1(String registro) throws SQLException, ParseException {
        List<String> listaCampos = Arrays.stream(registro.split("\\")).collect(toList());
        listaCampos.remove(0);//Elimina "Reg.1"
        String nomeEgresso = listaCampos.get(0);
        String tipoDocumento = listaCampos.get(1);
        String numeroDocumento = listaCampos.get(2);
        String dataNascimento = listaCampos.get(3);
        //Falta localização nos dados, usando a primeira.
        String inserirEgressoSQL = "INSERT INTO public.egresso(loge_id, egre_nome, " +
                "egre_tipo_doc_identidade, egre_numero_doc_identidade," +
                " egre_data_nascimento, egre_visibilidade_dados) " +
                "VALUES (1, ?, ?, ?, ?, 'Privado')";
        try (PreparedStatement preparedStatement = conexaoSQL.prepareStatement
                (inserirEgressoSQL)) {
            preparedStatement.setString(1, nomeEgresso);
            preparedStatement.setString(2, tipoDocumento);
            preparedStatement.setString(3, numeroDocumento);
            java.sql.Date dateSQL = convertStringToSQLDate(dataNascimento);
            preparedStatement.setDate(4, dateSQL);
            preparedStatement.executeUpdate();
        }
        //Pega o id do curso da UFG...
        String nomeCursoUFG = listaCampos.get(4);
        String procuraCursoUFGSQL = "SELECT cufg_id FROM public.curso_ufg " +
                "WHERE nome = ?";
        String cursoID = null;
        try (PreparedStatement preparedStatement = conexaoSQL.prepareStatement
                (procuraCursoUFGSQL)) {
            preparedStatement.setString(1, nomeCursoUFG);
            ResultSet resultSet = preparedStatement.executeQuery();
            cursoID = resultSet.getString(1);
        }
        if (cursoID == null) {
            throw new SecurityException("Curso não existe no BD!");
        }
        String procuraEgressoSQL = "SELECT egre_id FROM public.egresso " +
                "WHERE egre_nome = ?";
        String egressoId = null;
        try (PreparedStatement preparedStatement = conexaoSQL.prepareStatement
                (procuraEgressoSQL)) {
            preparedStatement.setString(1, nomeEgresso);
            ResultSet resultSet = preparedStatement.executeQuery();
            egressoId = resultSet.getString(1);
        }
        if (egressoId == null) {
            throw new SecurityException("Falha ao inserir egresso.");
        }
        String mesAnoInicio = listaCampos.get(5);
        String mesAnoFim = listaCampos.get(6);
        String numeroMatriculaCurso = listaCampos.get(7);
        String tituloTrabalhoFinal = listaCampos.get(8);
        String inserirHistoricoUfgSQL = "INSERT INTO public.historico_na_ufg(" +
                "egre_id, curs_id, hifg_mes_ano_de_inicio, " +
                "hifg_mes_ano_de_fim, " +
                "hifg_numero_matricula_curso, hifg_titulo_do_trabalho_final)" +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conexaoSQL.prepareStatement
                (inserirHistoricoUfgSQL)) {
            preparedStatement.setInt(1, Integer.valueOf(egressoId));
            preparedStatement.setInt(2, Integer.valueOf(cursoID));
            preparedStatement.setInt(3, Integer.valueOf(mesAnoInicio));
            preparedStatement.setInt(4, Integer.valueOf(mesAnoFim));
            preparedStatement.setInt(5, Integer.valueOf(numeroMatriculaCurso));
            preparedStatement.setString(6, tituloTrabalhoFinal);
            preparedStatement.executeUpdate();
        }
    }

    private static void inserirReg2(String registro) throws SQLException, ParseException {
        List<String> listaCampos = Arrays.stream(registro.split("\\")).collect(toList());
        listaCampos.remove(0);//Elimina "Reg.2"
        String tipoDocumento = listaCampos.get(0);
        String numeroDocumento = listaCampos.get(1);
        String procuraEgressoSQL = "SELECT egre_id FROM public.egresso " +
                "WHERE egre_tipo_doc_identidade = ? AND " +
                "egre_numero_doc_identidade = ?";
        String egressoId = null;
        try (PreparedStatement preparedStatement = conexaoSQL.prepareStatement
                (procuraEgressoSQL)) {
            preparedStatement.setString(1, tipoDocumento);
            preparedStatement.setString(2, numeroDocumento);
            ResultSet resultSet = preparedStatement.executeQuery();
            egressoId = resultSet.getString(1);
        }
        if (egressoId == null) {
            throw new SecurityException("Falha ao encontrar egresso.");
        }
        String cursoUFGId = listaCampos.get(2);
        String procuraHistoricoUFGId = "SELECT hifg_id FROM public.historico_na_ufg " +
                "WHERE egre_id = ? AND " +
                "curs_id = ?";
        String historicoId = null;
        try (PreparedStatement preparedStatement = conexaoSQL.prepareStatement
                (procuraHistoricoUFGId)) {
            preparedStatement.setInt(1, Integer.parseInt(egressoId));
            preparedStatement.setInt(2, Integer.parseInt(cursoUFGId));
            ResultSet resultSet = preparedStatement.executeQuery();
            historicoId = resultSet.getString(1);
        }
        if (historicoId == null) {
            throw new SecurityException("Esse egresso não cursou este curso " +
                    "na UFG!");
        }
        String tipoRealizacao = listaCampos.get(3);
        String dataInicio = listaCampos.get(4);
        String dataFim = listaCampos.get(5);
        String descricao = listaCampos.get(6);
        String inserirRealizacaoSQL = "INSERT INTO public.realizacao_de_programa_academico(" +
                "hifg_id, rpac_tipo, rpac_data_inicio, " +
                "rpac_data_fim, rpac_descricao) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conexaoSQL.prepareStatement
                (inserirRealizacaoSQL)) {
            preparedStatement.setInt(1, Integer.valueOf(historicoId));
            preparedStatement.setString(2, tipoRealizacao);
            preparedStatement.setDate(3, convertStringToSQLDate(dataInicio));
            preparedStatement.setDate(4, convertStringToSQLDate(dataFim));
            preparedStatement.setString(5, descricao);
            preparedStatement.executeUpdate();
        }
    }

    private static java.sql.Date convertStringToSQLDate(String dataNascimento) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(dataNascimento);
        java.sql.Date dateSQL = new java.sql.Date(date.getTime());
        return dateSQL;
    }

}

