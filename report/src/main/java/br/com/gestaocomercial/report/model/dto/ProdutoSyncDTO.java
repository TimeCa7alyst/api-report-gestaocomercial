package br.com.gestaocomercial.report.model.dto;

import java.math.BigDecimal;

public record ProdutoSyncDTO(Integer id, String nome, BigDecimal valorAtual) {}
