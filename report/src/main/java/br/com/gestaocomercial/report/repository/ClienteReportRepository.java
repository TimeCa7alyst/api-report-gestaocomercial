package br.com.gestaocomercial.report.repository;

import br.com.gestaocomercial.report.model.ClienteReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface ClienteReportRepository extends JpaRepository<ClienteReport, Integer>, JpaSpecificationExecutor<ClienteReport> {
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO cliente_report (id, nome, tipo, data_cadastro, status_cliente, cidade, estado, email, ultima_atualizacao)
        VALUES (:id, :nome, :tipo, :dataCadastro, :statusCliente, :cidade, :estado, :email, NOW(6))
        ON DUPLICATE KEY UPDATE
            nome = VALUES(nome),
            tipo = VALUES(tipo),
            status_cliente = VALUES(status_cliente),
            cidade = VALUES(cidade),
            estado = VALUES(estado),
            email = VALUES(email),
            ultima_atualizacao = NOW(6)
        """, nativeQuery = true)
    void upsert(@Param("id") Integer id,
                @Param("nome") String nome,
                @Param("tipo") String tipo,
                @Param("dataCadastro") LocalDate dataCadastro,
                @Param("statusCliente") String statusCliente,
                @Param("cidade") String cidade,
                @Param("estado") String estado,
                @Param("email") String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE orcamento_report SET cliente_nome = :nome WHERE cliente_id = :clienteId", nativeQuery = true)
    void atualizarNomeNosOrcamentos(@Param("clienteId") Integer clienteId, @Param("nome") String nome);
}
