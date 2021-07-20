package com.shopwise.admin.processes.export;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shopwise.common.entity.User;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class UserPdfExporter extends AbstractExporter {

    private Font getFont(String fontName, int size, Color color){
        Font font = FontFactory.getFont(fontName);
        font.setSize(size);
        font.setColor(color);
        return font;
    }

    public void export(List<User> userList, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response,"application/pdf",".pdf","_users");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document,response.getOutputStream());

        document.open();

        Font font = getFont(FontFactory.HELVETICA_BOLD, 18, Color.ORANGE);

        Paragraph paragraph = new Paragraph("ShopWise Admin | List of users", font);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(paragraph);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);
        table.setWidths(new float[] {1.2f,4.5f, 2.2f ,3.0f,3.0f,3.0f,1.5f,3.2f});

        writeTableHeaders(table);
        writeTableData(userList,table);

        document.add(table);

        document.close();
    }

    private void writeTableHeaders(PdfPTable table) {
        addHeaderCellToTable("ID",table);
        addHeaderCellToTable("E-Mail",table);
        addHeaderCellToTable("Phone Number",table);
        addHeaderCellToTable("First Name",table);
        addHeaderCellToTable("Last Name",table);
        addHeaderCellToTable("Roles",table);
        addHeaderCellToTable("Enabled",table);
        addHeaderCellToTable("Created At",table);
    }

    private void addHeaderCellToTable(String columnHeader, PdfPTable table){
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.ORANGE);
        cell.setPadding(6);

        Font font = getFont(FontFactory.HELVETICA, 11, Color.BLACK);
        cell.setPhrase(new Phrase(columnHeader, font));

        table.addCell(cell);
    }

    private void writeTableData(List<User> userList, PdfPTable table){
        for (User user:userList){

            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getEmail());
            table.addCell(user.getPhoneNumber());
            table.addCell(user.getFirstName());
            table.addCell(user.getLastName());
            table.addCell(user.getRoles().toString());
            table.addCell(String.valueOf(user.isEnabled()));
            table.addCell(user.getCreatedAt().toString());
        }
    }
}
