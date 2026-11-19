package br.com.gestaocomercial.report.repository;

import br.com.gestaocomercial.report.model.ItemReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemReportRepository extends JpaRepository<ItemReport, Integer> {

    @Modifying
    void deleteByOrcamentoId(Integer orcamentoId);
}