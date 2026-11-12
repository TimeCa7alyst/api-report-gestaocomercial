package br.com.gestaocomercial.report.controller;

import br.com.gestaocomercial.report.model.ClienteReport;
import br.com.gestaocomercial.report.model.OrcamentoReport;
import br.com.gestaocomercial.report.model.VendaReport;
import br.com.gestaocomercial.report.service.RelatorioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/vendas")
    public ResponseEntity<Page<VendaReport>> listarVendasPaginadas(
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) String status,
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<VendaReport> pagina = relatorioService.relatorioVendasFiltrado(dataInicio, dataFim, status, pageable);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/vendas/exportar/pdf")
    public ResponseEntity<byte[]> exportarPdfVendas(
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) String status,
            @PageableDefault(page = 0, size = 50, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<VendaReport> pagina = relatorioService.relatorioVendasFiltrado(dataInicio, dataFim, status, pageable);
        byte[] pdf = relatorioService.exportarVendasPdf(pagina.getContent());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vendas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/vendas/exportar/excel")
    public ResponseEntity<byte[]> exportarExcelVendas(
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) String status,
            @PageableDefault(page = 0, size = 50, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<VendaReport> pagina = relatorioService.relatorioVendasFiltrado(dataInicio, dataFim, status, pageable);
        byte[] excel = relatorioService.exportarVendasExcel(pagina.getContent());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vendas.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }


    @GetMapping("/clientes")
    public ResponseEntity<Page<ClienteReport>> listarClientesPaginados(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cidade,
            @PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ClienteReport> pagina = relatorioService.relatorioClientesFiltrado(status, cidade, pageable);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/clientes/exportar/pdf")
    public ResponseEntity<byte[]> exportarPdfClientes(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cidade,
            @PageableDefault(page = 0, size = 50, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ClienteReport> pagina = relatorioService.relatorioClientesFiltrado(status, cidade, pageable);
        byte[] pdf = relatorioService.exportarClientesPdf(pagina.getContent());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clientes.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/clientes/exportar/excel")
    public ResponseEntity<byte[]> exportarExcelClientes(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cidade,
            @PageableDefault(page = 0, size = 50, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ClienteReport> pagina = relatorioService.relatorioClientesFiltrado(status, cidade, pageable);
        byte[] excel = relatorioService.exportarClientesExcel(pagina.getContent());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clientes.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @GetMapping("/orcamentos")
    public ResponseEntity<Page<OrcamentoReport>> listarOrcamentosPaginados(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<OrcamentoReport> pagina = relatorioService.relatorioOrcamentoFiltrado(status, dataInicio, dataFim, pageable);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/orcamentos/exportar/pdf")
    public ResponseEntity<byte[]> exportarPdfOrcamentos(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @PageableDefault(page = 0, size = 50, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<OrcamentoReport> pagina = relatorioService.relatorioOrcamentoFiltrado(status, dataInicio, dataFim, pageable);
        byte[] pdf = relatorioService.exportarOrcamentosPdf(pagina.getContent());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orcamentos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/orcamentos/exportar/excel")
    public ResponseEntity<byte[]> exportarExcelOrcamentos(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @PageableDefault(page = 0, size = 50, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<OrcamentoReport> pagina = relatorioService.relatorioOrcamentoFiltrado(status, dataInicio, dataFim, pageable);
        byte[] excel = relatorioService.exportarOrcamentosExcel(pagina.getContent());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orcamentos.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}