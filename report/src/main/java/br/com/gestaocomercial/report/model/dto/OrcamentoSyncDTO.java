package br.com.gestaocomercial.report.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrcamentoSyncDTO(Integer id, Integer clienteId, String clienteNome,
                               LocalDate dataCriacao, LocalDate dataValidade, BigDecimal valorTotal,
                               BigDecimal desconto, String statusOrcamento) {}
