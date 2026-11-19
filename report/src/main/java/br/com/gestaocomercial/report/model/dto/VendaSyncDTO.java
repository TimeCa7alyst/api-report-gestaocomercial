package br.com.gestaocomercial.report.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VendaSyncDTO(Integer id, Integer orcamentoId, Integer clienteId,
                           LocalDate dataCriacao, String statusPagamento, BigDecimal valorFinal,
                           @JsonAlias({"nota", "avaliacao"}) Float notaAvaliacao) {}
