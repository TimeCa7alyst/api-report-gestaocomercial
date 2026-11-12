package br.com.gestaocomercial.report.repository;

import br.com.gestaocomercial.report.model.ClienteReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClienteReportRepository extends JpaRepository<ClienteReport, Integer>, JpaSpecificationExecutor<ClienteReport> {
}
