package com.server.oceankeeper.domain.activity.service;

import com.server.oceankeeper.domain.activity.dto.response.CrewInfoFileDto;
import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.crew.entitiy.CrewRole;
import com.server.oceankeeper.domain.crew.entitiy.Crews;
import com.server.oceankeeper.domain.crew.service.CrewService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Component
public class ExcelMaker {
    public CrewInfoFileDto makeExcelFile(List<Crews> crews) throws IOException {
        Workbook xWorkbook = new XSSFWorkbook(); //엑셀파일 생성
        Sheet xSheet = xWorkbook.createSheet("sheet1"); //시트 생성
        Row xRow = null; //행 객체 생성
        Cell xCell = null; //열 객체 생성

        initializeExcelFormat(xSheet);

        int count = 1;
        int row = 1;

        for (Crews application : crews) {
            if (application.getActivityRole().equals(CrewRole.HOST))
                continue;
            xRow = xSheet.createRow(row);
            xCell = xRow.createCell(0);
            xCell.setCellValue(count);
            xCell = xRow.createCell(1);
            xCell.setCellValue(getValue(application.getName()));
            xCell = xRow.createCell(2);
            xCell.setCellValue(getValue(application.getPhoneNumber()));
            xCell = xRow.createCell(3);
            xCell.setCellValue(getValue(application.getId1365()));
            xCell = xRow.createCell(4);
            xCell.setCellValue(getValue(application.getEmail()));
            xCell = xRow.createCell(5);
            xCell.setCellValue(getValue(application.getDayOfBirth()));

            row++;
            count++;
        }

        xRow = xSheet.getRow(xSheet.getFirstRowNum());
        Iterator<Cell> cellIterator = xRow.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            int columnIndex = cell.getColumnIndex();
            xSheet.autoSizeColumn(columnIndex);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xWorkbook.write(outputStream);


        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
        outputStream.close();
        return new CrewInfoFileDto(resource);
    }

    private void initializeExcelFormat(Sheet xSheet) {
        Row xRow;
        Cell xCell;
        xRow = xSheet.createRow(0);

        xCell = xRow.createCell(0);
        xCell.setCellValue("No.");
        xCell = xRow.createCell(1);
        xCell.setCellValue("이름");
        xCell = xRow.createCell(2);
        xCell.setCellValue("연락처");
        xCell = xRow.createCell(3);
        xCell.setCellValue("1365 아이디");
        xCell = xRow.createCell(4);
        xCell.setCellValue("이메일");
        xCell = xRow.createCell(5);
        xCell.setCellValue("생년월일");
    }

    private String getValue(String data) {
        return data == null ? "" : data;
    }
}
