package br.com.gestaocomercial.report.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "venda_report")
public class VendaReport {

    @Id
    private Integer id;
    private Integer orcamentoId;
    private Integer clienteId;
    private LocalDate dataCriacao;
    private String statusPagamento;
    private BigDecimal valorFinal;
    private Float notaAvaliacao;

    public VendaReport() {}

    public VendaReport(Integer id, Integer orcamentoId, Integer clienteId, LocalDate dataCriacao,
                       String statusPagamento, BigDecimal valorFinal, Float notaAvaliacao) {
        this.id = id;
        this.orcamentoId = orcamentoId;
        this.clienteId = clienteId;
        this.dataCriacao = dataCriacao;
        this.statusPagamento = statusPagamento;
        this.valorFinal = valorFinal;
        this.notaAvaliacao = notaAvaliacao;
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

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(String statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public Float getNotaAvaliacao() {
        return notaAvaliacao;
    }

    public void setNotaAvaliacao(Float notaAvaliacao) {
        this.notaAvaliacao = notaAvaliacao;
    }
}