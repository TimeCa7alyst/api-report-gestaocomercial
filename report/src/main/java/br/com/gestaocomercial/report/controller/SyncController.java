package br.com.gestaocomercial.report.controller;

import br.com.gestaocomercial.report.model.*;
import br.com.gestaocomercial.report.model.dto.*;
import br.com.gestaocomercial.report.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sync")
public class SyncController {

    private final VendaReportRepository vendaRep;
    private final ClienteReportRepository clienteRep;
    private final ProdutoReportRepository produtoRep;
    private final OrcamentoReportRepository orcamentoRep;

    public SyncController(VendaReportRepository vendaRep, ClienteReportRepository clienteRep, ProdutoReportRepository produtoRep, OrcamentoReportRepository orcamentoRep) {
        this.vendaRep = vendaRep;
        this.clienteRep = clienteRep;
        this.produtoRep = produtoRep;
        this.orcamentoRep = orcamentoRep;
    }

    @PostMapping("/venda")
    public ResponseEntity<Void> syncVenda(@RequestBody VendaSyncDTO dto) {
        VendaReport entity = new VendaReport();
        BeanUtils.copyProperties(dto, entity);
        vendaRep.save(entity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cliente")
    public ResponseEntity<Void> syncCliente(@RequestBody ClienteSyncDTO dto) {
        ClienteReport entity = new ClienteReport();
        BeanUtils.copyProperties(dto, entity);
        clienteRep.save(entity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/produto")
    public ResponseEntity<Void> syncProduto(@RequestBody ProdutoSyncDTO dto) {
        ProdutoReport entity = new ProdutoReport();
        BeanUtils.copyProperties(dto, entity);
        produtoRep.save(entity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orcamento")
    public ResponseEntity<Void> syncOrcamento(@RequestBody OrcamentoSyncDTO dto) {
        OrcamentoReport entity = new OrcamentoReport();
        BeanUtils.copyProperties(dto, entity);
        orcamentoRep.save(entity);
        return ResponseEntity.ok().build();
    }
}