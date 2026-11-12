package br.com.gestaocomercial.report.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "produto_report")
public class ProdutoReport {
    @Id
    private Integer id;
    private String nome;
    private BigDecimal valorAtual;

    public ProdutoReport() {}

    public ProdutoReport(Integer id, String nome, BigDecimal valorAtual) {
        this.id = id;
        this.nome = nome;
        this.valorAtual = valorAtual;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(BigDecimal valorAtual) {
        this.valorAtual = valorAtual;
    }
}
