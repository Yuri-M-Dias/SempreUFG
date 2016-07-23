package br.inf.ufg.sempreufg.model;

public class HistoricoUFG {

    private int arco_id;
    private int arco_arc_id;
    private String nome_area;
    private int codigo_area;

    public HistoricoUFG(int arco_id, int arco_arc_id, String nome_area,
                        int codigo_area) {
        this.arco_id = arco_id;
        this.arco_arc_id = arco_arc_id;
        this.nome_area = nome_area;
        this.codigo_area = codigo_area;
    }

    public int getArco_id() {
        return arco_id;
    }

    public void setArco_id(int arco_id) {
        this.arco_id = arco_id;
    }

    public int getArco_arc_id() {
        return arco_arc_id;
    }

    public void setArco_arc_id(int arco_arc_id) {
        this.arco_arc_id = arco_arc_id;
    }

    public String getNome_area() {
        return nome_area;
    }

    public void setNome_area(String nome_area) {
        this.nome_area = nome_area;
    }

    public int getCodigo_area() {
        return codigo_area;
    }

    public void setCodigo_area(int codigo_area) {
        this.codigo_area = codigo_area;
    }
}
