package br.com.gestaocomercial.report.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "orcamento_report")
public class OrcamentoReport {

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
