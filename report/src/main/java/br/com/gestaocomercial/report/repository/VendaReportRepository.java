package br.com.gestaocomercial.report.repository;

import br.com.gestaocomercial.report.model.VendaReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendaReportRepository extends JpaRepository<VendaReport, Long> {
}
