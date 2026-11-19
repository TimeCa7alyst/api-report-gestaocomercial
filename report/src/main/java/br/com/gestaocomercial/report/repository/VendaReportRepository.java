package br.com.gestaocomercial.report.repository;

import br.com.gestaocomercial.report.model.VendaReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface VendaReportRepository extends JpaRepository<VendaReport, Integer>, JpaSpecificationExecutor<VendaReport> {
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO venda_report (id, orcamento_id, cliente_id, data_criacao, status_pagamento, valor_final, nota_avaliacao, ultima_atualizacao)
        VALUES (:id, :orcamentoId, :clienteId, :dataCriacao, :statusPagamento, :valorFinal, :notaAvaliacao, NOW(6))
        ON DUPLICATE KEY UPDATE
            status_pagamento = VALUES(status_pagamento),
            valor_final = VALUES(valor_final),
            nota_avaliacao = VALUES(nota_avaliacao),
            ultima_atualizacao = NOW(6)
        """, nativeQuery = true)
    void upsert(@Param("id") Integer id,
                @Param("orcamentoId") Integer orcamentoId,
                @Param("clienteId") Integer clienteId,
                @Param("dataCriacao") LocalDate dataCriacao,
                @Param("statusPagamento") String statusPagamento,
                @Param("valorFinal") BigDecimal valorFinal,
                @Param("notaAvaliacao") Float notaAvaliacao);

    @Modifying
    @Transactional
    @Query(value = "UPDATE venda_report SET nota_avaliacao = :nota, ultima_atualizacao = NOW(6) WHERE id = :vendaId", nativeQuery = true)
    void atualizarNotaAvaliacao(@Param("vendaId") Integer vendaId, @Param("nota") Float nota);
}