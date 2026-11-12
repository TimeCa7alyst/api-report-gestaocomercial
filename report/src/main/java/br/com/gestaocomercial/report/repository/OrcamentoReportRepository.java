package br.com.gestaocomercial.report.repository;

import br.com.gestaocomercial.report.model.OrcamentoReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrcamentoReportRepository extends JpaRepository<OrcamentoReport, Integer>, JpaSpecificationExecutor<OrcamentoReport> {
}
