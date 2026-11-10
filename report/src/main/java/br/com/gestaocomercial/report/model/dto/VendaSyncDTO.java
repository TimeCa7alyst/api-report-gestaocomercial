package br.com.gestaocomercial.report.model.dto;

import java.math.BigDecimal;

public record VendaSyncDTO(
        Long id,
        String nomeCliente,
        BigDecimal valorTotal
) {


}
