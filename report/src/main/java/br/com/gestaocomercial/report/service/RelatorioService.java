package br.com.gestaocomercial.report.service;

import br.com.gestaocomercial.report.model.VendaReport;
import br.com.gestaocomercial.report.model.dto.VendaSyncDTO;
import br.com.gestaocomercial.report.repository.VendaReportRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RelatorioService {

    private final VendaReportRepository repository;

    public RelatorioService(VendaReportRepository repository) {
        this.repository = repository;
    }

    public void sincronizarVenda(VendaSyncDTO dto) {
        VendaReport relatorio = repository.findById(dto.id()).orElse(new VendaReport());

        relatorio.setId(dto.id());
        relatorio.setNomeCliente(dto.nomeCliente());
        relatorio.setValorTotal(dto.valorTotal());
        relatorio.setDataVenda(LocalDateTime.now());

        repository.save(relatorio);
    }

    public byte[] exportarVendasExcel() {
        List<VendaReport> vendas = repository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Relatório de Vendas");
            Row headerRow = sheet.createRow(0);

            String[] colunas = {"ID Venda", "Cliente", "Valor Total", "Data"};
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(colunas[i]);
            }

            int rowIdx = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (VendaReport venda : vendas) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(venda.getId());
                row.createCell(1).setCellValue(venda.getNomeCliente());
                row.createCell(2).setCellValue(venda.getValorTotal().doubleValue());
                row.createCell(3).setCellValue(venda.getDataVenda() != null ? venda.getDataVenda().format(formatter) : "");
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar arquivo Excel", e);
        }
    }

    public byte[] exportarVendasPdf() {
        List<VendaReport> vendas = repository.findAll();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Relatório Geral de Vendas", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("ID Venda");
            table.addCell("Cliente");
            table.addCell("Valor Total");
            table.addCell("Data");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (VendaReport venda : vendas) {
                table.addCell(String.valueOf(venda.getId()));
                table.addCell(venda.getNomeCliente());
                table.addCell("R$ " + venda.getValorTotal().toString());
                table.addCell(venda.getDataVenda() != null ? venda.getDataVenda().format(formatter) : "");
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }
}