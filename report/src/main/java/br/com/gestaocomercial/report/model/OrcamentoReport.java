package br.com.gestaocomercial.report.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.SQLInsert;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "orcamento_report")
@SQLInsert(sql = """
    INSERT INTO orcamento_report (cliente_id, cliente_nome, data_criacao, data_validade, desconto, status_orcamento, valor_total, id, ultima_atualizacao)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(6))
    ON DUPLICATE KEY UPDATE
        cliente_id = VALUES(cliente_id),
        cliente_nome = VALUES(cliente_nome),
        data_criacao = VALUES(data_criacao),
        data_validade = VALUES(data_validade),
        desconto = VALUES(desconto),
        status_orcamento = VALUES(status_orcamento),
        valor_total = VALUES(valor_total),
        ultima_atualizacao = NOW(6)
    """)
public class OrcamentoReport implements Persistable<Integer> {

    @Id
    private Integer id;
    private Integer clienteId;
    private String clienteNome;
    private LocalDate dataCriacao;
    private LocalDate dataValidade;
    private BigDecimal valorTotal;
    private BigDecimal desconto;
    private String statusOrcamento;

    public OrcamentoReport() {}

    public OrcamentoReport(Integer id, Integer clienteId, String clienteNome, LocalDate dataCriacao,
                           LocalDate dataValidade, BigDecimal valorTotal, BigDecimal desconto, String statusOrcamento) {
        this.id = id;
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
        this.dataCriacao = dataCriacao;
        this.dataValidade = dataValidade;
        this.valorTotal = valorTotal;
        this.desconto = desconto;
        this.statusOrcamento = statusOrcamento;
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

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public String getStatusOrcamento() {
        return statusOrcamento;
    }

    public void setStatusOrcamento(String statusOrcamento) {
        this.statusOrcamento = statusOrcamento;
    }
}