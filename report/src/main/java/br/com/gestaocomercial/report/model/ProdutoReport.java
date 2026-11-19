package br.com.gestaocomercial.report.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.SQLInsert;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;

@Entity
@Table(name = "produto_report")
@SQLInsert(sql = """
    INSERT INTO produto_report (ativo, nome, valor_atual, id, ultima_atualizacao)
    VALUES (?, ?, ?, ?, NOW(6))
    ON DUPLICATE KEY UPDATE
        ativo = VALUES(ativo),
        nome = VALUES(nome),
        valor_atual = VALUES(valor_atual),
        ultima_atualizacao = NOW(6)
    """)
public class ProdutoReport implements Persistable<Integer> {

    @Id
    private Integer id;
    private String nome;
    private BigDecimal valorAtual;
    private Boolean ativo = true;

    public ProdutoReport() {}

    public ProdutoReport(Integer id, String nome, BigDecimal valorAtual) {
        this.id = id;
        this.nome = nome;
        this.valorAtual = valorAtual;
        this.ativo = true;
    }

    public ProdutoReport(Integer id, String nome, BigDecimal valorAtual, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.valorAtual = valorAtual;
        this.ativo = ativo;
    }

    @Override
    @Transient
    public boolean isNew() {
        return true;
    }

    @Override
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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}