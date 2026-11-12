package br.com.gestaocomercial.report.repository;

import br.com.gestaocomercial.report.model.VendaReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VendaReportRepository extends JpaRepository<VendaReport, Integer>, JpaSpecificationExecutor<VendaReport> {
}
