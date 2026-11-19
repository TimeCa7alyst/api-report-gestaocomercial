package br.com.gestaocomercial.report.repository;

import br.com.gestaocomercial.report.model.ProdutoReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface ProdutoReportRepository extends JpaRepository<ProdutoReport, Integer>, JpaSpecificationExecutor<ProdutoReport> {
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO produto_report (id, nome, valor_atual, ativo, ultima_atualizacao)
        VALUES (:id, :nome, :valorAtual, :ativo, NOW(6))
        ON DUPLICATE KEY UPDATE
            nome = VALUES(nome),
            valor_atual = VALUES(valor_atual),
            ativo = VALUES(ativo),
            ultima_atualizacao = NOW(6)
        """, nativeQuery = true)
    void upsert(@Param("id") Integer id,
                @Param("nome") String nome,
                @Param("valorAtual") BigDecimal valorAtual,
                @Param("ativo") Boolean ativo);
}
