package br.com.gestaocomercial.report.repository;

import br.com.gestaocomercial.report.model.ProdutoReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProdutoReportRepository extends JpaRepository<ProdutoReport, Integer>, JpaSpecificationExecutor<ProdutoReport> {
}
