package br.com.gestaocomercial.report.service;

import br.com.gestaocomercial.report.model.*;
import br.com.gestaocomercial.report.model.dto.*;
import br.com.gestaocomercial.report.repository.*;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public Page<VendaReport> relatorioVendasFiltrado(LocalDate dataInicio, LocalDate dataFim, String statusPagamento, Pageable pageable) {
        Specification<VendaReport> spec = (root, query, cb) -> cb.conjunction();

        if (dataInicio != null && dataFim != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("dataCriacao"), dataInicio, dataFim));
        }
        if (statusPagamento != null && !statusPagamento.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("statusPagamento"), statusPagamento));
        }

        return vendaRep.findAll(spec, pageable);
    }

    public Page<OrcamentoReport> relatorioOrcamentoFiltrado(String status, LocalDate dataCriacao, LocalDate dataValidade, Pageable pageable) {
        Specification<OrcamentoReport> spec = ((root, query, cb) -> cb.conjunction());

        if (dataCriacao != null && dataValidade != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("dataCriacao"), dataCriacao, dataValidade));
        }

        if (status != null && !status.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("statusOrcamento"), status));
        }
        return orcamentoRep.findAll(spec, pageable);
    }

    public Page<ClienteReport> relatorioClientesFiltrado(String statusCliente, String cidade, Pageable pageable) {
        Specification<ClienteReport> spec = ((root, query, cb) -> cb.conjunction());

        if (statusCliente != null && !statusCliente.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("statusCliente"), statusCliente));
        }
        if (cidade != null && !cidade.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("cidade"), cidade));
        }

        return clienteRep.findAll(spec, pageable);
    }

    private PdfPCell criarCelulaCentralizada(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto != null ? texto : "", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); // Centraliza horizontalmente
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   // Centraliza verticalmente
        cell.setPadding(6f); // Espaçamento interno para respirar
        return cell;
    }

    private CellStyle criarEstiloBaseExcel(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    public byte[] exportarVendasPdf(List<VendaReport> vendas) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1f, 2f, 1f});

            try {
                Image logo = Image.getInstance(getClass().getResource("/logo.png"));
                logo.scaleToFit(80, 80);
                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(logoCell);
            } catch (Exception e) {
                PdfPCell emptyCell = new PdfPCell(new Phrase("LOGO AQUI"));
                emptyCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(emptyCell);
            }

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            PdfPCell titleCell = new PdfPCell(new Phrase("Relatório Gerencial de Vendas", titleFont));
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(titleCell);

            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            String dataGeracao = "Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            PdfPCell dateCell = new PdfPCell(new Phrase(dataGeracao, dateFont));
            dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            dateCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(dateCell);

            document.add(headerTable);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 1.5f, 2f, 2f, 1.5f, 1.5f});

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            String[] headers = {"ID Venda", "ID Cliente", "Status Pagamento", "Valor Final", "Data", "Nota"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(20, 33, 61));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(8f);
                table.addCell(cell);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            BigDecimal totalGeral = BigDecimal.ZERO;
            Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            for (VendaReport venda : vendas) {
                table.addCell(criarCelulaCentralizada(String.valueOf(venda.getId()), rowFont));
                table.addCell(criarCelulaCentralizada(String.valueOf(venda.getClienteId()), rowFont));
                table.addCell(criarCelulaCentralizada(venda.getStatusPagamento(), rowFont));

                String valorStr = venda.getValorFinal() != null ? String.format("R$ %.2f", venda.getValorFinal()) : "R$ 0,00";
                table.addCell(criarCelulaCentralizada(valorStr, rowFont));

                String dataStr = venda.getDataCriacao() != null ? venda.getDataCriacao().format(formatter) : "";
                table.addCell(criarCelulaCentralizada(dataStr, rowFont));

                String notaStr = venda.getNotaAvaliacao() != null ? String.valueOf(venda.getNotaAvaliacao()) : "-";
                table.addCell(criarCelulaCentralizada(notaStr, rowFont));

                String statusStr = venda.getStatusPagamento() != null ? venda.getStatusPagamento().trim() : "";
                if (venda.getValorFinal() != null &&
                        ("APROVADO".equalsIgnoreCase(statusStr) || "PAGO".equalsIgnoreCase(statusStr) || "CONCLUIDO".equalsIgnoreCase(statusStr))) {
                    totalGeral = totalGeral.add(venda.getValorFinal());
                }
            }

            String textoTotal = String.format("TOTAL (Aprovadas): R$ %.2f", totalGeral);
            PdfPCell totalCell = new PdfPCell(new Phrase(textoTotal, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            totalCell.setColspan(6);
            totalCell.setHorizontalAlignment(Element.ALIGN_CENTER); // Centralizado conforme solicitado
            totalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            totalCell.setBackgroundColor(new Color(236, 240, 241));
            totalCell.setPadding(8f);
            table.addCell(totalCell);

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }

    public byte[] exportarVendasExcel(List<VendaReport> vendas) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Relatório de Vendas");
            sheet.createFreezePane(0, 1);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle centerStyle = criarEstiloBaseExcel(workbook);

            CellStyle currencyStyle = criarEstiloBaseExcel(workbook);
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("R$ #,##0.00"));

            CellStyle dateStyle = criarEstiloBaseExcel(workbook);
            dateStyle.setDataFormat(format.getFormat("dd/mm/yyyy"));

            Row headerRow = sheet.createRow(0);
            String[] colunas = {"ID Venda", "ID Orçamento", "ID Cliente", "Status Pagamento", "Valor Final", "Data Criação", "Nota"};
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(colunas[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (VendaReport venda : vendas) {
                Row row = sheet.createRow(rowIdx++);

                Cell c0 = row.createCell(0);
                c0.setCellValue(venda.getId());
                c0.setCellStyle(centerStyle);

                Cell c1 = row.createCell(1);
                c1.setCellValue(venda.getOrcamentoId());
                c1.setCellStyle(centerStyle);

                Cell c2 = row.createCell(2);
                c2.setCellValue(venda.getClienteId());
                c2.setCellStyle(centerStyle);

                Cell c3 = row.createCell(3);
                c3.setCellValue(venda.getStatusPagamento());
                c3.setCellStyle(centerStyle);

                Cell valorCell = row.createCell(4);
                valorCell.setCellValue(venda.getValorFinal() != null ? venda.getValorFinal().doubleValue() : 0.0);
                valorCell.setCellStyle(currencyStyle);

                Cell dataCell = row.createCell(5);
                if (venda.getDataCriacao() != null) {
                    dataCell.setCellValue(venda.getDataCriacao());
                    dataCell.setCellStyle(dateStyle);
                } else {
                    dataCell.setCellValue("");
                    dataCell.setCellStyle(centerStyle);
                }

                Cell c6 = row.createCell(6);
                c6.setCellValue(venda.getNotaAvaliacao() != null ? venda.getNotaAvaliacao() : 0.0);
                c6.setCellStyle(centerStyle);
            }

            for (int i = 0; i < colunas.length; i++) sheet.autoSizeColumn(i);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar arquivo Excel", e);
        }
    }


    public byte[] exportarClientesPdf(List<ClienteReport> clientes) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1f, 2f, 1f});

            try {
                Image logo = Image.getInstance(getClass().getResource("/logo.png"));
                logo.scaleToFit(80, 80);
                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(logoCell);
            } catch (Exception e) {
                PdfPCell emptyCell = new PdfPCell(new Phrase("LOGO AQUI"));
                emptyCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(emptyCell);
            }

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            PdfPCell titleCell = new PdfPCell(new Phrase("Relatório Gerencial de Clientes", titleFont));
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(titleCell);

            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            String dataGeracao = "Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            PdfPCell dateCell = new PdfPCell(new Phrase(dataGeracao, dateFont));
            dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            dateCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(dateCell);

            document.add(headerTable);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            String[] headers = {"ID", "Nome", "Tipo", "Status", "Cidade/UF", "Email"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(20, 33, 61));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(8f);
                table.addCell(cell);
            }

            Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            for (ClienteReport c : clientes) {
                table.addCell(criarCelulaCentralizada(String.valueOf(c.getId()), rowFont));
                table.addCell(criarCelulaCentralizada(c.getNome(), rowFont));
                table.addCell(criarCelulaCentralizada(c.getTipo(), rowFont));
                table.addCell(criarCelulaCentralizada(c.getStatusCliente(), rowFont));

                String localizacao = (c.getCidade() != null ? c.getCidade() : "") + " / " + (c.getEstado() != null ? c.getEstado() : "");
                table.addCell(criarCelulaCentralizada(localizacao, rowFont));

                table.addCell(criarCelulaCentralizada(c.getEmail(), rowFont));
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
            sheet.createFreezePane(0, 1);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle centerStyle = criarEstiloBaseExcel(workbook);

            Row headerRow = sheet.createRow(0);
            String[] colunas = {"ID", "Nome", "Tipo", "Status", "Cidade", "Estado", "Email", "Data Cadastro"};
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(colunas[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (ClienteReport c : clientes) {
                Row row = sheet.createRow(rowIdx++);

                Cell c0 = row.createCell(0);
                c0.setCellValue(c.getId());
                c0.setCellStyle(centerStyle);

                Cell c1 = row.createCell(1);
                c1.setCellValue(c.getNome());
                c1.setCellStyle(centerStyle);

                Cell c2 = row.createCell(2);
                c2.setCellValue(c.getTipo());
                c2.setCellStyle(centerStyle);

                Cell c3 = row.createCell(3);
                c3.setCellValue(c.getStatusCliente());
                c3.setCellStyle(centerStyle);

                Cell c4 = row.createCell(4);
                c4.setCellValue(c.getCidade());
                c4.setCellStyle(centerStyle);

                Cell c5 = row.createCell(5);
                c5.setCellValue(c.getEstado());
                c5.setCellStyle(centerStyle);

                Cell c6 = row.createCell(6);
                c6.setCellValue(c.getEmail());
                c6.setCellStyle(centerStyle);

                Cell c7 = row.createCell(7);
                c7.setCellValue(c.getDataCadastro() != null ? c.getDataCadastro().toString() : "");
                c7.setCellStyle(centerStyle);
            }

            for (int i = 0; i < colunas.length; i++) sheet.autoSizeColumn(i);

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

            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1f, 2f, 1f});

            try {
                Image logo = Image.getInstance(getClass().getResource("/logo.png"));
                logo.scaleToFit(80, 80);
                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(logoCell);
            } catch (Exception e) {
                PdfPCell emptyCell = new PdfPCell(new Phrase("LOGO AQUI"));
                emptyCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(emptyCell);
            }

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            PdfPCell titleCell = new PdfPCell(new Phrase("Relatório Gerencial de Orçamentos", titleFont));
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(titleCell);

            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            String dataGeracao = "Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            PdfPCell dateCell = new PdfPCell(new Phrase(dataGeracao, dateFont));
            dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            dateCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(dateCell);

            document.add(headerTable);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 2f, 1.5f, 2f, 1.5f, 2f});

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            String[] headers = {"ID", "Cliente", "Status", "Valor Total", "Desconto", "Data Criação"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(20, 33, 61));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(8f);
                table.addCell(cell);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            BigDecimal totalAprovado = BigDecimal.ZERO;
            Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            for (OrcamentoReport o : orcamentos) {
                table.addCell(criarCelulaCentralizada(String.valueOf(o.getId()), rowFont));
                table.addCell(criarCelulaCentralizada(o.getClienteNome(), rowFont));
                table.addCell(criarCelulaCentralizada(o.getStatusOrcamento(), rowFont));

                String valorStr = o.getValorTotal() != null ? String.format("R$ %.2f", o.getValorTotal()) : "R$ 0,00";
                table.addCell(criarCelulaCentralizada(valorStr, rowFont));

                String descStr = o.getDesconto() != null ? String.format("R$ %.2f", o.getDesconto()) : "R$ 0,00";
                table.addCell(criarCelulaCentralizada(descStr, rowFont));

                String dataStr = o.getDataCriacao() != null ? o.getDataCriacao().format(formatter) : "";
                table.addCell(criarCelulaCentralizada(dataStr, rowFont));

                String statusStr = o.getStatusOrcamento() != null ? o.getStatusOrcamento().trim() : "";
                if (o.getValorTotal() != null && "APROVADO".equalsIgnoreCase(statusStr)) {
                    totalAprovado = totalAprovado.add(o.getValorTotal());
                }
            }

            String textoTotal = String.format("TOTAL (Aprovados): R$ %.2f", totalAprovado);
            PdfPCell totalCell = new PdfPCell(new Phrase(textoTotal, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            totalCell.setColspan(6);
            totalCell.setHorizontalAlignment(Element.ALIGN_CENTER); // Centralizado conforme solicitado
            totalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            totalCell.setBackgroundColor(new Color(236, 240, 241));
            totalCell.setPadding(8f);
            table.addCell(totalCell);

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
            sheet.createFreezePane(0, 1);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle centerStyle = criarEstiloBaseExcel(workbook);

            CellStyle currencyStyle = criarEstiloBaseExcel(workbook);
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("R$ #,##0.00"));

            CellStyle dateStyle = criarEstiloBaseExcel(workbook);
            dateStyle.setDataFormat(format.getFormat("dd/mm/yyyy"));

            Row headerRow = sheet.createRow(0);
            String[] colunas = {"ID", "ID Cliente", "Nome Cliente", "Status", "Valor Total", "Desconto", "Data Criação", "Data Validade"};
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(colunas[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (OrcamentoReport o : orcamentos) {
                Row row = sheet.createRow(rowIdx++);

                Cell c0 = row.createCell(0);
                c0.setCellValue(o.getId());
                c0.setCellStyle(centerStyle);

                Cell c1 = row.createCell(1);
                c1.setCellValue(o.getClienteId());
                c1.setCellStyle(centerStyle);

                Cell c2 = row.createCell(2);
                c2.setCellValue(o.getClienteNome());
                c2.setCellStyle(centerStyle);

                Cell c3 = row.createCell(3);
                c3.setCellValue(o.getStatusOrcamento());
                c3.setCellStyle(centerStyle);

                Cell valorCell = row.createCell(4);
                valorCell.setCellValue(o.getValorTotal() != null ? o.getValorTotal().doubleValue() : 0.0);
                valorCell.setCellStyle(currencyStyle);

                Cell descontoCell = row.createCell(5);
                descontoCell.setCellValue(o.getDesconto() != null ? o.getDesconto().doubleValue() : 0.0);
                descontoCell.setCellStyle(currencyStyle);

                Cell dataCriacaoCell = row.createCell(6);
                if (o.getDataCriacao() != null) {
                    dataCriacaoCell.setCellValue(o.getDataCriacao());
                    dataCriacaoCell.setCellStyle(dateStyle);
                } else {
                    dataCriacaoCell.setCellValue("");
                    dataCriacaoCell.setCellStyle(centerStyle);
                }

                Cell dataValidadeCell = row.createCell(7);
                if (o.getDataValidade() != null) {
                    dataValidadeCell.setCellValue(o.getDataValidade());
                    dataValidadeCell.setCellStyle(dateStyle);
                } else {
                    dataValidadeCell.setCellValue("");
                    dataValidadeCell.setCellStyle(centerStyle);
                }
            }

            for (int i = 0; i < colunas.length; i++) sheet.autoSizeColumn(i);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel de orçamentos", e);
        }
    }
}