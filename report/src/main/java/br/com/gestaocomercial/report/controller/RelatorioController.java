package br.com.gestaocomercial.report.controller;

import br.com.gestaocomercial.report.model.VendaReport;
import br.com.gestaocomercial.report.service.RelatorioService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/vendas/exportar/pdf")
    public ResponseEntity<byte[]> exportarPdfVendas(
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) String status) {

        List<VendaReport> vendasFiltradas = relatorioService.relatorioVendasFiltrado(dataInicio, dataFim, status);

        byte[] pdf = relatorioService.exportarVendasPdf(vendasFiltradas);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vendas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/vendas/exportar/excel")
    public ResponseEntity<byte[]> exportarExcelVendas(
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) String status) {

        List<VendaReport> vendasFiltradas = relatorioService.relatorioVendasFiltrado(dataInicio, dataFim, status);

        byte[] excel = relatorioService.exportarVendasExcel(vendasFiltradas);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vendas.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}