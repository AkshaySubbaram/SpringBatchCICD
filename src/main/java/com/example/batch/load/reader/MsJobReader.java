package com.example.batch.load.reader;

import com.example.batch.load.entity.MsJobs;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@Component
@StepScope
public class MsJobReader implements ItemReader<MsJobs> {

    private static final Logger logger = LoggerFactory.getLogger(MsJobReader.class);

    private Iterator<Row> rowIterator;

    private Workbook workbook;

    private InputStream inputStream;

    private int currentRow = 0;

    private final Resource resource;

    public MsJobReader(@Value("${input.file.msJob}") Resource resource) throws IOException {
        this.resource = resource;
        logger.info("MsJob File exists: {}", resource.exists());
        logger.info("MsJob File is readable: {}", resource.isReadable());
        logger.info("MsJob Absolute path: {}", resource.getFile().getAbsolutePath());
    }

    @Override
    public MsJobs read() throws Exception {
        if (rowIterator == null) {
            initialize();
            currentRow = 0;
        }

        if (!rowIterator.hasNext()) {
            logger.info("NO MORE ROWS - Total rows processed: {}", currentRow);
            closeResources();
            return null;
        }

        Row row = rowIterator.next();
        currentRow++;

        logger.info("READING ROW MsJob {}: {}", currentRow, rowToString(row));

        if (currentRow == 1) {
            logger.info("SKIPPING HEADER ROW");
            return read();
        }

        return mapRowToSkill(row);
    }

    private MsJobs mapRowToSkill(Row row) {
        MsJobs jobs = new MsJobs();

        jobs.setJobid(getIntValue(row.getCell(0)));
        jobs.setJobgrade(getStringValue(row.getCell(1)));
        jobs.setJobtext(getStringValue(row.getCell(2)));
        jobs.setJoboverview(getStringValue(row.getCell(3)));
        jobs.setLastupdated(getStringValue(row.getCell(4)));

        return jobs;
    }

    private String rowToString(Row row) {
        StringBuilder sb = new StringBuilder();
        for (Cell cell : row) {
            sb.append("| ");
            switch (cell.getCellType()) {
                case STRING: sb.append(cell.getStringCellValue()); break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        sb.append(cell.getDateCellValue());
                    } else {
                        sb.append(cell.getNumericCellValue());
                    }
                    break;
                case BOOLEAN: sb.append(cell.getBooleanCellValue()); break;
                case BLANK: sb.append("[BLANK]"); break;
                default: sb.append(cell.toString());
            }
            sb.append(" ");
        }
        return sb.toString();
    }

    private void initialize() throws Exception {
        try {
            logger.info("File URI: {}", resource.getURI());
            logger.info("File size: {} bytes", resource.contentLength());

            inputStream = new BufferedInputStream(resource.getInputStream());
            workbook = WorkbookFactory.create(inputStream);

            logger.info("Number of sheets: {}", workbook.getNumberOfSheets());

            Sheet sheet = workbook.getSheetAt(0);

            logger.info("Sheet name: '{}'", sheet.getSheetName());
            logger.info("Physical rows: {}", sheet.getPhysicalNumberOfRows());

            rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) {
                Row firstRow = rowIterator.next();
                logger.info("Header row cell count: {}", firstRow.getPhysicalNumberOfCells());
            }

        } catch (Exception e) {
            logger.error("Failed to initialize Excel reader", e);
            throw e;
        }
    }

    private String getStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            String value = switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue().trim();
                case NUMERIC -> cell.getNumericCellValue() == (int) cell.getNumericCellValue()
                        ? String.valueOf((int) cell.getNumericCellValue())
                        : String.valueOf(cell.getNumericCellValue());
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                case FORMULA -> {
                    try {
                        yield cell.getCellFormula();
                    } catch (Exception e) {
                        logger.warn("Error reading formula cell: {}", e.getMessage());
                        yield null;
                    }
                }
                case BLANK -> null;
                default -> {
                    logger.warn("Unsupported cell type: {}", cell.getCellType());
                    yield null;
                }
            };
            logger.trace("Cell value: {}", value);
            return value;
        } catch (Exception e) {
            logger.warn("Error reading cell value: {}", e.getMessage());
            return null;
        }
    }

    private Integer getIntValue(Cell cell) {
        if (cell == null) {
            logger.trace("Cell is null");
            return null;
        }

        try {
            Integer value = switch (cell.getCellType()) {
                case NUMERIC -> (int) cell.getNumericCellValue();
                case STRING -> {
                    try {
                        yield Integer.parseInt(cell.getStringCellValue().trim());
                    } catch (NumberFormatException e) {
                        logger.warn("Not a valid integer: {}", cell.getStringCellValue());
                        yield null;
                    }
                }
                default -> null;
            };

            logger.trace("Cell int value: {}", value);
            return value;

        } catch (Exception e) {
            logger.warn("Error reading integer cell: {}", e.getMessage());
            return null;
        }
    }

    private void closeResources() {
        try {
            if (workbook != null) {
                workbook.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            logger.info("Closed Excel resources");
        } catch (Exception e) {
            logger.error("Error closing resources", e);
        }
    }

}
