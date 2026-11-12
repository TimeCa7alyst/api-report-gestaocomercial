package br.com.gestaocomercial.report.service;

import br.com.gestaocomercial.report.model.*;
import br.com.gestaocomercial.report.model.dto.*;
import br.com.gestaocomercial.report.repository.*;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RelatorioService {

    private final VendaReportRepository vendaRep;
    private final ClienteReportRepository clienteRep;
    private final OrcamentoReportRepository orcamentoRep;
    private final ProdutoReportRepository produtoRep;

    public RelatorioService(VendaReportRepository vendaRep,
                            ClienteReportRepository clienteRep,
                            OrcamentoReportRepository orcamentoRep,
                            ProdutoReportRepository produtoRep) {
        this.vendaRep = vendaRep;
        this.clienteRep = clienteRep;
        this.orcamentoRep = orcamentoRep;
        this.produtoRep = produtoRep;
    }

    public void sincronizarVenda(VendaSyncDTO dto) {
        VendaReport relatorio = vendaRep.findById(dto.id()).orElse(new VendaReport());
        BeanUtils.copyProperties(dto, relatorio);
        vendaRep.save(relatorio);
    }

    public void sincronizarCliente(ClienteSyncDTO dto) {
        ClienteReport relatorio = clienteRep.findById(dto.id()).orElse(new ClienteReport());
        BeanUtils.copyProperties(dto, relatorio);
        clienteRep.save(relatorio);
    }

    public void sincronizarOrcamento(OrcamentoSyncDTO dto) {
        OrcamentoReport relatorio = orcamentoRep.findById(dto.id()).orElse(new OrcamentoReport());
        BeanUtils.copyProperties(dto, relatorio);
        orcamentoRep.save(relatorio);
    }

    public void sincronizarProduto(ProdutoSyncDTO dto) {
        ProdutoReport relatorio = produtoRep.findById(dto.id()).orElse(new ProdutoReport());
        BeanUtils.copyProperties(dto, relatorio);
        produtoRep.save(relatorio);
    }

    public List<VendaReport> relatorioVendasFiltrado(LocalDate dataInicio, LocalDate dataFim, String statusPagamento) {
        Specification<VendaReport> spec = (root, query, cb) -> cb.conjunction();

        if (dataInicio != null && dataFim != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("dataCriacao"), dataInicio, dataFim));
        }
        if (statusPagamento != null && !statusPagamento.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("statusPagamento"), statusPagamento));
        }

        return vendaRep.findAll(spec);
    }

    public List<OrcamentoReport> relatorioOrcamentoFiltrado(String status, LocalDate dataCriacao, LocalDate dataValidade) {
        Specification<OrcamentoReport> spec = ((root, query, cb) -> cb.conjunction());

        if (dataCriacao != null && dataValidade != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("dataCriacao"), dataCriacao, dataValidade));
        }

        if (status != null && !status.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("statusOrcamento"), status));
        }
        return orcamentoRep.findAll(spec);
    }

    public List<ClienteReport> relatorioClientesFiltrado(String statusCliente, String cidade) {
        Specification<ClienteReport> spec = ((root, query, cb) -> cb.conjunction());

        if (statusCliente != null && !statusCliente.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("statusCliente"), statusCliente));
        }
        if (cidade != null && !cidade.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("cidade"), cidade));
        }

        return clienteRep.findAll(spec);
    }

    public byte[] exportarVendasExcel(List<VendaReport> vendas) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Relatório de Vendas");
            Row headerRow = sheet.createRow(0);

            String[] colunas = {"ID Venda", "ID Orçamento", "ID Cliente", "Status", "Valor Final", "Data", "Nota"};
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(colunas[i]);
            }

            int rowIdx = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (VendaReport venda : vendas) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(venda.getId());
                row.createCell(1).setCellValue(venda.getOrcamentoId());
                row.createCell(2).setCellValue(venda.getClienteId());
                row.createCell(3).setCellValue(venda.getStatusPagamento());
                row.createCell(4).setCellValue(venda.getValorFinal() != null ? venda.getValorFinal().doubleValue() : 0.0);
                row.createCell(5).setCellValue(venda.getDataCriacao() != null ? venda.getDataCriacao().format(formatter) : "");
                row.createCell(6).setCellValue(venda.getNotaAvaliacao() != null ? venda.getNotaAvaliacao() : 0.0);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar arquivo Excel", e);
        }
    }

    public byte[] exportarVendasPdf(List<VendaReport> vendas) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Relatório de Vendas (Filtrado)", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.addCell("ID Venda");
            table.addCell("ID Cliente");
            table.addCell("Status");
            table.addCell("Valor Final");
            table.addCell("Data");
            table.addCell("Nota Avaliação");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (VendaReport venda : vendas) {
                table.addCell(String.valueOf(venda.getId()));
                table.addCell(String.valueOf(venda.getClienteId()));
                table.addCell(venda.getStatusPagamento());
                table.addCell(venda.getValorFinal() != null ? "R$ " + venda.getValorFinal().toString() : "R$ 0.00");
                table.addCell(venda.getDataCriacao() != null ? venda.getDataCriacao().format(formatter) : "");
                table.addCell(venda.getNotaAvaliacao() != null ? String.valueOf(venda.getNotaAvaliacao()) : "-");
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }

    public byte[] exportarClientesPdf(List<ClienteReport> clientes) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Relatório de Clientes", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.addCell("ID");
            table.addCell("Nome");
            table.addCell("Tipo");
            table.addCell("Status");
            table.addCell("Cidade/UF");
            table.addCell("Email");

            for (ClienteReport c : clientes) {
                table.addCell(String.valueOf(c.getId()));
                table.addCell(c.getNome());
                table.addCell(c.getTipo());
                table.addCell(c.getStatusCliente());
                table.addCell((c.getCidade() != null ? c.getCidade() : "") + " / " + (c.getEstado() != null ? c.getEstado() : ""));
                table.addCell(c.getEmail() != null ? c.getEmail() : "");
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF de clientes", e);
        }
    }

    public byte[] exportarClientesExcel(List<ClienteReport> clientes) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Clientes");
            Row headerRow = sheet.createRow(0);

            String[] colunas = {"ID", "Nome", "Tipo", "Status", "Cidade", "Estado", "Email", "Data Cadastro"};
            for (int i = 0; i < colunas.length; i++) {
                headerRow.createCell(i).setCellValue(colunas[i]);
            }

            int rowIdx = 1;
            for (ClienteReport c : clientes) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(c.getId());
                row.createCell(1).setCellValue(c.getNome());
                row.createCell(2).setCellValue(c.getTipo());
                row.createCell(3).setCellValue(c.getStatusCliente());
                row.createCell(4).setCellValue(c.getCidade());
                row.createCell(5).setCellValue(c.getEstado());
                row.createCell(6).setCellValue(c.getEmail());
                row.createCell(7).setCellValue(c.getDataCadastro() != null ? c.getDataCadastro().toString() : "");
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel de clientes", e);
        }
    }

    public byte[] exportarOrcamentosPdf(List<OrcamentoReport> orcamentos) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            Paragraph title = new Paragraph("Relatório de Orçamentos", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.addCell("ID");
            table.addCell("Cliente");
            table.addCell("Status");
            table.addCell("Valor Total");
            table.addCell("Desconto");
            table.addCell("Data Criação");

            for (OrcamentoReport o : orcamentos) {
                table.addCell(String.valueOf(o.getId()));
                table.addCell(o.getClienteNome());
                table.addCell(o.getStatusOrcamento());
                table.addCell("R$ " + o.getValorTotal());
                table.addCell("R$ " + o.getDesconto());
                table.addCell(o.getDataCriacao() != null ? o.getDataCriacao().toString() : "");
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF de orçamentos", e);
        }
    }

    public byte[] exportarOrcamentosExcel(List<OrcamentoReport> orcamentos) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Orçamentos");
            Row headerRow = sheet.createRow(0);

            String[] colunas = {"ID", "ID Cliente", "Nome Cliente", "Status", "Valor Total", "Desconto", "Data Criação", "Data Validade"};
            for (int i = 0; i < colunas.length; i++) {
                headerRow.createCell(i).setCellValue(colunas[i]);
            }

            int rowIdx = 1;
            for (OrcamentoReport o : orcamentos) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(o.getId());
                row.createCell(1).setCellValue(o.getClienteId());
                row.createCell(2).setCellValue(o.getClienteNome());
                row.createCell(3).setCellValue(o.getStatusOrcamento());
                row.createCell(4).setCellValue(o.getValorTotal() != null ? o.getValorTotal().doubleValue() : 0.0);
                row.createCell(5).setCellValue(o.getDesconto() != null ? o.getDesconto().doubleValue() : 0.0);
                row.createCell(6).setCellValue(o.getDataCriacao() != null ? o.getDataCriacao().toString() : "");
                row.createCell(7).setCellValue(o.getDataValidade() != null ? o.getDataValidade().toString() : "");
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel de orçamentos", e);
        }
    }
}