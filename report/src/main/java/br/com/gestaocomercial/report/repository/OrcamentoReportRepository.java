package br.com.gestaocomercial.report.repository;

import br.com.gestaocomercial.report.model.OrcamentoReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface OrcamentoReportRepository extends JpaRepository<OrcamentoReport, Integer>, JpaSpecificationExecutor<OrcamentoReport> {
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO orcamento_report (id, cliente_id, cliente_nome, data_criacao, data_validade, valor_total, desconto, status_orcamento, ultima_atualizacao)
        VALUES (:id, :clienteId, :clienteNome, :dataCriacao, :dataValidade, :valorTotal, :desconto, :statusOrcamento, NOW(6))
        ON DUPLICATE KEY UPDATE
            cliente_id = VALUES(cliente_id),
            cliente_nome = VALUES(cliente_nome),
            data_validade = VALUES(data_validade),
            valor_total = VALUES(valor_total),
            desconto = VALUES(desconto),
            status_orcamento = VALUES(status_orcamento),
            ultima_atualizacao = NOW(6)
        """, nativeQuery = true)
    void upsert(@Param("id") Integer id,
                @Param("clienteId") Integer clienteId,
                @Param("clienteNome") String clienteNome,
                @Param("dataCriacao") LocalDate dataCriacao,
                @Param("dataValidade") LocalDate dataValidade,
                @Param("valorTotal") BigDecimal valorTotal,
                @Param("desconto") BigDecimal desconto,
                @Param("statusOrcamento") String statusOrcamento);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM item_report WHERE orcamento_id = :orcamentoId", nativeQuery = true)
    void limparItens(@Param("orcamentoId") Integer orcamentoId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO item_report (orcamento_id, produto_id, quantidade) VALUES (:orcamentoId, :produtoId, :quantidade)", nativeQuery = true)
    void inserirItem(@Param("orcamentoId") Integer orcamentoId,
                     @Param("produtoId") Integer produtoId,
                     @Param("quantidade") Integer quantidade);

    @Modifying
    @Transactional
    @Query("UPDATE OrcamentoReport o SET o.clienteNome = :nome WHERE o.clienteId = :clienteId")
    void atualizarNomeCliente(@Param("clienteId") Integer clienteId, @Param("nome") String nome);
}
