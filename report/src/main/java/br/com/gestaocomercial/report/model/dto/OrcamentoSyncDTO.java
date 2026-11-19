package br.com.gestaocomercial.report.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrcamentoSyncDTO(
        Integer id,
        Integer clienteId,
        String clienteNome,
        LocalDate dataCriacao,
        LocalDate dataValidade,
        @JsonAlias({"valorTotal", "valor_total", "preco"}) BigDecimal valor,
        BigDecimal desconto,
        @JsonAlias({"statusOrcamento", "status_orcamento"}) String status,
        List<ItemSyncDTO> itens
) {
    public record ItemSyncDTO(Integer produtoId, Integer quantidade) {}
}