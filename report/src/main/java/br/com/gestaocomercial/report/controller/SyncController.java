package br.com.gestaocomercial.report.controller;

import br.com.gestaocomercial.report.model.dto.VendaSyncDTO;
import br.com.gestaocomercial.report.service.RelatorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final RelatorioService relatorioService;

    public SyncController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @PostMapping("/venda")
    public ResponseEntity<Void> receberVenda(@RequestBody VendaSyncDTO dto) {
        relatorioService.sincronizarVenda(dto);
        return ResponseEntity.ok().build();
    }
}