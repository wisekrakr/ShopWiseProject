package com.shopwise.admin.processes.export;

import com.shopwise.common.entity.User;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class UserExcelExporter extends AbstractExporter {

    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public UserExcelExporter() {
        workbook = new XSSFWorkbook();
    }

    /**
     * Create a new cell in a excel spread sheet
     * @param row XSSFRow
     * @param columnIndex number index of this column
     * @param value value of the data (String, Integer or Boolean)
     * @param style CellStyle
     */
    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style){

        XSSFCell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);

        if(value instanceof Integer){
            cell.setCellValue((Integer)value);
        }else if(value instanceof Boolean){
            cell.setCellValue((Boolean)value);
        }else{
            cell.setCellValue((String)value);

        }
        cell.setCellStyle(style);
    }

    private XSSFCellStyle getCellStyle(boolean isBold, double fontHeight){
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(isBold);
        font.setFontHeight(fontHeight);
        cellStyle.setFont(font);

        return cellStyle;
    }

    private void writeHeaderLine(){
        sheet = workbook.createSheet("Users");
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle cellStyle = getCellStyle(true, 16);

        createCell(row,0,"ID", cellStyle);
        createCell(row,1,"E-Mail", cellStyle);
        createCell(row,2,"Phone Number", cellStyle);
        createCell(row,3,"First Name", cellStyle);
        createCell(row,4,"Last Name", cellStyle);
        createCell(row,5,"Roles", cellStyle);
        createCell(row,6,"Enabled", cellStyle);
        createCell(row,7,"Created At", cellStyle);
    }



    private void writeDataLines(List<User> userList) {
        int rowIndex = 1;
        XSSFCellStyle cellStyle = getCellStyle(false, 14);

        for (User user: userList){
            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIndex = 0;

            createCell(row, columnIndex++, user.getId(), cellStyle);
            createCell(row, columnIndex++, user.getEmail(), cellStyle);
            createCell(row, columnIndex++, user.getPhoneNumber(), cellStyle);
            createCell(row, columnIndex++, user.getFirstName(), cellStyle);
            createCell(row, columnIndex++, user.getLastName(), cellStyle);
            createCell(row, columnIndex++, user.getRoles().toString(), cellStyle);
            createCell(row, columnIndex++, user.isEnabled(), cellStyle);
            createCell(row, columnIndex++, user.getCreatedAt(), cellStyle);
        }
    }

    public void export(List<User> userList, HttpServletResponse response) throws IOException{
        super.setResponseHeader(response,"application/octet-stream",".xlsx","_users");

        writeHeaderLine();
        writeDataLines(userList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

}
