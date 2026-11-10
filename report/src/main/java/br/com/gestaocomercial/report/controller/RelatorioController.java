package br.com.gestaocomercial.report.controller;

import br.com.gestaocomercial.report.service.RelatorioService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/vendas/excel")
    public ResponseEntity<byte[]> downloadExcel() {
        byte[] bytes = relatorioService.exportarVendasExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_vendas.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    @GetMapping("/vendas/pdf")
    public ResponseEntity<byte[]> downloadPdf() {
        byte[] bytes = relatorioService.exportarVendasPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_vendas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }
}