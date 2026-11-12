package br.com.gestaocomercial.report.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VendaSyncDTO(Integer id, Integer orcamentoId, Integer clienteId,
                           LocalDate dataCriacao, String statusPagamento, BigDecimal valorFinal,
                           Float notaAvaliacao) {}
