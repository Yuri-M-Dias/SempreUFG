package br.inf.ufg.sempreufg.model;

import java.sql.Date;

public class CursoUFG {
    
    private String cufg_id, uafg_id, arco_id, cufg_nome, cufg_nivel, curs_turno;
    private boolean cufg_presencial;
    private Date cufg_data_criacao;

    public CursoUFG(String cufg_id, String uafg_id, String arco_id, String cufg_nome, String cufg_nivel, String curs_turno, boolean cufg_presencial, Date cufg_data_criacao) {
        this.cufg_id = cufg_id;
        this.uafg_id = uafg_id;
        this.arco_id = arco_id;
        this.cufg_nome = cufg_nome;
        this.cufg_nivel = cufg_nivel;
        this.cufg_data_criacao = cufg_data_criacao;
        this.cufg_presencial = cufg_presencial;
        this.curs_turno = curs_turno;
    }
}
