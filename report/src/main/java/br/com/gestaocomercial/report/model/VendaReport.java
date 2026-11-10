package br.com.gestaocomercial.report.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_venda_report")
public class VendaReport {

    @Id
    private Long id;
    private String nomeCliente;
    private BigDecimal valorTotal;
    private LocalDateTime dataVenda;

    public VendaReport() {}

    public VendaReport(Long id, String nomeCliente, BigDecimal valorTotal, LocalDateTime dataVenda) {
        this.id = id;
        this.nomeCliente = nomeCliente;
        this.valorTotal = valorTotal;
        this.dataVenda = dataVenda;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public LocalDateTime getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDateTime dataVenda) { this.dataVenda = dataVenda; }
}