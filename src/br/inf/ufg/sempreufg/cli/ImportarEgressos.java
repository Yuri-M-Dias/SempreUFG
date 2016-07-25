package br.inf.ufg.sempreufg.cli;

import br.inf.ufg.sempreufg.auxiliar.ArquivoLog;
import br.inf.ufg.sempreufg.auxiliar.ArquivoParaImportar;
import br.inf.ufg.sempreufg.conexao.Conexao;

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
    private static ArquivoLog arquivoLog = null;
    private static Connection conexaoSQL = null;

    public static void main(String[] args) {
        String caminho = "";
        if (args.length > 0) {
            caminho = args[0];
        }
        arquivoLog = new ArquivoLog();
        Conexao conexao = new Conexao();
        arquivoParaImportar = new ArquivoParaImportar(caminho);
        List<String> arquivo = ArquivoParaImportar.GetArquivoParaImportar();
        conexaoSQL = conexao.getConexao();
        if (conexaoSQL == null) {
            ArquivoLog.GravaMensagemDeErro("Problema ao conseguir a" +
                    "conexão com o BD.");
            throw new SecurityException("Erro ocorreu. Detalhes no log.");
        }
        Long numeroEgressosInseridos = 0L;
        ArquivoLog.GravaMensagemDeErro("Começando uma nova inserção...");
        try {
            conexaoSQL.setAutoCommit(false);
            for (String registro : arquivo) {
                if (registro.startsWith("Reg.1")) {
                    inserirReg1(registro);
                    numeroEgressosInseridos++;
                } else if (registro.startsWith("Reg.2")) {
                    inserirReg2(registro);
                } else {
                    ArquivoLog.gravaMensagemSucesso("Arquivo com formato" +
                            "errado. Linha: " + registro);
                }
                System.out.println("Coluna inserida: " + registro);
            }
            conexaoSQL.commit();
        } catch (Exception e) {
            try {
                conexaoSQL.rollback();
                numeroEgressosInseridos = 0L;
            } catch (SQLException e1) {
                ArquivoLog.GravaMensagemDeErro(e1.getMessage());
            }
            ArquivoLog.GravaMensagemDeErro(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                conexaoSQL.close();
            } catch (SQLException e) {
                ArquivoLog.GravaMensagemDeErro(e.getMessage());
            }
        }
        ArquivoLog.gravaMensagemSucesso("Sucesso: " + numeroEgressosInseridos
                        + " egressos foram inseridos.");
    }

    private static void inserirReg1(String registro) throws SQLException, ParseException {
        List<String> listaCampos = Arrays.stream(registro.split("\\\\"))
                .collect(toList());
        listaCampos.remove(0);//Elimina "Reg.1"
        String nomeEgresso = listaCampos.get(0);
        String tipoDocumento = listaCampos.get(1);
        String numeroDocumento = listaCampos.get(2);
        String dataNascimento = listaCampos.get(3);
        //Falta localização nos dados, usando a primeira.
        String inserirEgressoSQL = "INSERT INTO public.egresso(loge_id, " +
                "egre_nome, " + "egre_tipo_doc_identidade, " +
                "egre_numero_doc_identidade," +
                " egre_data_nascimento, egre_visibilidade_dados) " +
                "VALUES (1, ?, ?, ?, ?, 'Privado')";
        Long egressoId = null;
        try (PreparedStatement preparedStatement = conexaoSQL.prepareStatement
                (inserirEgressoSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, nomeEgresso);
            preparedStatement.setString(2, tipoDocumento);
            preparedStatement.setString(3, numeroDocumento);
            java.sql.Date dateSQL = convertStringToSQLDate(dataNascimento);
            preparedStatement.setDate(4, dateSQL);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Não consegui criar o usuário.");
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    egressoId = generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Não consegui criar o usuário.");
                }
            }
        } catch (ParseException e) {
            ArquivoLog.GravaMensagemDeErro(e.getMessage());
        }
        String nomeCursoUFG = listaCampos.get(4);
        String procuraCursoUFGSQL = "SELECT cufg_id FROM public.curso_ufg " +
                "WHERE cufg_nome = ?";
        Long cursoID = null;
        try (PreparedStatement preparedStatement = conexaoSQL.prepareStatement
                (procuraCursoUFGSQL)) {
            preparedStatement.setString(1, nomeCursoUFG);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                throw new SQLException("Curso não existe no BD.");
            }
            cursoID = resultSet.getLong("cufg_id");
        }
        if (cursoID == null) {
            ArquivoLog.GravaMensagemDeErro("Curso não existe no BD!");
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
            preparedStatement.setLong(1, egressoId);
            preparedStatement.setLong(2, cursoID);
            preparedStatement.setInt(3, Integer.valueOf(mesAnoInicio));
            preparedStatement.setInt(4, Integer.valueOf(mesAnoFim));
            preparedStatement.setInt(5, Integer.valueOf(numeroMatriculaCurso));
            preparedStatement.setString(6, tituloTrabalhoFinal);
            preparedStatement.executeUpdate();
        }
    }

    private static void inserirReg2(String registro) throws SQLException, ParseException {
        List<String> listaCampos = Arrays.stream(registro.split("\\\\"))
                .collect(toList());
        listaCampos.remove(0);//Elimina "Reg.2"
        String tipoDocumento = listaCampos.get(0);
        String numeroDocumento = listaCampos.get(1);
        String procuraEgressoSQL = "SELECT egre_id FROM public.egresso " +
                "WHERE egre_tipo_doc_identidade = ? AND " +
                "egre_numero_doc_identidade = ?";
        Long egressoId = null;
        try (PreparedStatement preparedStatement = conexaoSQL.prepareStatement
                (procuraEgressoSQL)) {
            preparedStatement.setString(1, tipoDocumento);
            preparedStatement.setString(2, numeroDocumento);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                throw new SQLException("Egresso não existe no BD.");
            }
            egressoId = resultSet.getLong("egre_id");
        }
        if (egressoId == null) {
            throw new SecurityException("Falha ao encontrar egresso.");
        }
        String nomeCursoUFG = listaCampos.get(2);
        String procuraCursoUFGSQL = "SELECT cufg_id FROM public.curso_ufg " +
                "WHERE cufg_nome = ?";
        Long cursoID = null;
        try (PreparedStatement preparedStatement = conexaoSQL.prepareStatement
                (procuraCursoUFGSQL)) {
            preparedStatement.setString(1, nomeCursoUFG);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                throw new SQLException("Curso não existe no BD.");
            }
            cursoID = resultSet.getLong("cufg_id");
        }
        String procuraHistoricoUFGId = "SELECT hifg_id FROM public.historico_na_ufg " +
                "WHERE egre_id = ? AND " +
                "curs_id = ?";
        Long historicoId = null;
        try (PreparedStatement preparedStatement = conexaoSQL.prepareStatement
                (procuraHistoricoUFGId)) {
            preparedStatement.setLong(1, egressoId);
            preparedStatement.setLong(2, cursoID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                throw new SQLException("Egresso não cursou este curso: " + nomeCursoUFG);
            }
            historicoId = resultSet.getLong("hifg_id");
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
                "VALUES (?, ?::t_programa_academico, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conexaoSQL.prepareStatement
                (inserirRealizacaoSQL)) {
            preparedStatement.setLong(1, historicoId);
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

