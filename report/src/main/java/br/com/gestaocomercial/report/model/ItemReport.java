package br.com.gestaocomercial.report.model;

import jakarta.persistence.*;

@Entity
@Table(name = "item_report")
public class ItemReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "orcamento_id")
    private Integer orcamentoId;

    @Column(name = "produto_id")
    private Integer produtoId;

    @Column(name = "quantidade")
    private Integer quantidade;

    public ItemReport() {}

    public ItemReport(Integer orcamentoId, Integer produtoId, Integer quantidade) {
        this.orcamentoId = orcamentoId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrcamentoId() {
        return orcamentoId;
    }

    public void setOrcamentoId(Integer orcamentoId) {
        this.orcamentoId = orcamentoId;
    }

    public Integer getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}