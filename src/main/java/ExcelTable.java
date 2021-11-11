import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelTable extends XSSFSheet {

    private static final File file =
            new File("/home/danny/covid");
    private static int rowCounter = 0;

    public static void fillTable(Study study) {
        System.out.println("fillTable is Started, rowCounter is " + rowCounter);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        //заполняю строку с id
        Row idRow = sheet.createRow(rowCounter);
        idRow.createCell(0).setCellValue("ID");
        idRow.createCell(1).setCellValue(study.getId());

        //заполняю строку с is_healthy
        Row IsHealthyRow = sheet.createRow(++rowCounter);
        IsHealthyRow.createCell(0).setCellValue("Is Healthy");
        IsHealthyRow.createCell(1).setCellValue(study.getIsHealthy());

        //заполняю строку с prob
        Row probRow = sheet.createRow(++rowCounter);
        probRow.createCell(0).setCellValue("Prob");
        probRow.createCell(1).setCellValue(study.getProb());

        //заполняю строку с status
        Row statusRow = sheet.createRow(++rowCounter);
        statusRow.createCell(0).setCellValue("Status");
        statusRow.createCell(1).setCellValue(study.getStatus());

        //заполняю строку с status
        Row statusTextRow = sheet.createRow(++rowCounter);
        statusTextRow.createCell(0).setCellValue("Status");
        statusTextRow.createCell(1).setCellValue(study.getStatusText());
        try {
            FileOutputStream outFile = new FileOutputStream(file);
            workbook.write(outFile);
            System.out.println("Created file: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        rowCounter += 2;
        System.out.println(rowCounter + "\n" + "fillTable is Done");
    }
}
