import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelTable extends XSSFSheet {

    private static final File file =
            new File(Env.excelFilePath);
    private static int rowCounter = 0;

    public static void fillTable(List<Study> studies) {
        System.out.println("fillTable is Started, rowCounter is " +
                rowCounter + "\n" + "study to be filled is - " + studies);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        for (Study study : studies) {

            //заполняю строку SeriesInstanceUID
            Row seriesIdRow = sheet.createRow(rowCounter);
            seriesIdRow.createCell(0).setCellValue("SeriesInstanceUID");
            seriesIdRow.createCell(1).setCellValue(study.getSeriesInstanceUID());

            //заполняю строку с id
            Row idRow = sheet.createRow(++rowCounter);
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

            //заполняю строку с status Text
            Row statusTextRow = sheet.createRow(++rowCounter);
            statusTextRow.createCell(0).setCellValue("Status Text");
            statusTextRow.createCell(1).setCellValue(study.getStatusText());
            rowCounter += 2;
            System.out.println("One study is filled" + "\n" + "rowCounter is - " + rowCounter);
        }
        try {
            FileOutputStream outFile = new FileOutputStream(file);
            workbook.write(outFile);
            System.out.println("Created file: " + file.getAbsolutePath());
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
