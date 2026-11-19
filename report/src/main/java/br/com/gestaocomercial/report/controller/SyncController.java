package br.com.gestaocomercial.report.controller;

import br.com.gestaocomercial.report.model.*;
import br.com.gestaocomercial.report.model.dto.*;
import br.com.gestaocomercial.report.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sync")
public class SyncController {

    private final VendaReportRepository vendaRep;
    private final ClienteReportRepository clienteRep;
    private final ProdutoReportRepository produtoRep;
    private final OrcamentoReportRepository orcamentoRep;

    public SyncController(VendaReportRepository vendaRep, ClienteReportRepository clienteRep,
                          ProdutoReportRepository produtoRep, OrcamentoReportRepository orcamentoRep) {
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
    @Transactional
    public ResponseEntity<Void> syncCliente(@RequestBody ClienteSyncDTO dto) {
        ClienteReport entity = new ClienteReport();
        BeanUtils.copyProperties(dto, entity);
        clienteRep.save(entity);

        if (dto.id() != null && dto.nome() != null) {
            orcamentoRep.atualizarNomeCliente(dto.id(), dto.nome());
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/produto")
    public ResponseEntity<Void> syncProduto(@RequestBody ProdutoSyncDTO dto) {
        ProdutoReport entity = new ProdutoReport();
        BeanUtils.copyProperties(dto, entity);
        if (entity.getAtivo() == null) entity.setAtivo(true);
        produtoRep.save(entity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orcamento")
    @Transactional
    public ResponseEntity<Void> syncOrcamento(@RequestBody OrcamentoSyncDTO dto) {
        orcamentoRep.upsert(
                dto.id(),
                dto.clienteId(),
                dto.clienteNome(),
                dto.dataCriacao(),
                dto.dataValidade(),
                dto.valor(),
                dto.desconto(),
                dto.status()
        );

        if (dto.id() != null && dto.itens() != null) {
            orcamentoRep.limparItens(dto.id());
            for (OrcamentoSyncDTO.ItemSyncDTO item : dto.itens()) {
                orcamentoRep.inserirItem(dto.id(), item.produtoId(), item.quantidade());
            }
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/venda/{id}/avaliacao")
    @Transactional
    public ResponseEntity<Void> syncAvaliacao(@PathVariable Integer id, @RequestBody Float nota) {
        if (id != null && nota != null) {
            vendaRep.atualizarNotaAvaliacao(id, nota);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/vendas/batch")
    @Transactional
    public ResponseEntity<Void> syncVendasBatch(@RequestBody List<VendaSyncDTO> dtos) {
        List<VendaReport> list = dtos.stream().map(dto -> {
            VendaReport e = new VendaReport();
            BeanUtils.copyProperties(dto, e);
            return e;
        }).toList();
        vendaRep.saveAll(list);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/clientes/batch")
    @Transactional
    public ResponseEntity<Void> syncClientesBatch(@RequestBody List<ClienteSyncDTO> dtos) {
        List<ClienteReport> list = dtos.stream().map(dto -> {
            ClienteReport e = new ClienteReport();
            BeanUtils.copyProperties(dto, e);
            return e;
        }).toList();
        clienteRep.saveAll(list);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/produtos/batch")
    @Transactional
    public ResponseEntity<Void> syncProdutosBatch(@RequestBody List<ProdutoSyncDTO> dtos) {
        List<ProdutoReport> list = dtos.stream().map(dto -> {
            ProdutoReport e = new ProdutoReport();
            BeanUtils.copyProperties(dto, e);
            if (e.getAtivo() == null) e.setAtivo(true);
            return e;
        }).toList();
        produtoRep.saveAll(list);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orcamentos/batch")
    @Transactional
    public ResponseEntity<Void> syncOrcamentosBatch(@RequestBody List<OrcamentoSyncDTO> dtos) {
        for (OrcamentoSyncDTO dto : dtos) {
            syncOrcamento(dto);
        }
        return ResponseEntity.ok().build();
    }
}