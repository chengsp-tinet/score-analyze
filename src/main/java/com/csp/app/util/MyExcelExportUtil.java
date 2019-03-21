package com.csp.app.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class MyExcelExportUtil {
    public static <T> void exportDataAsCsv(List<T> data, String[] fields, String[] header, ValueConvert[] valueConverts, OutputStream os) {
        if (fields == null || header == null) {
            throw new RuntimeException("fields or header can'selectPart be null.");
        }
        Workbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("VosCdr");
        Row headerTitle = sheet.createRow(0);
        int index = 0;
        for (int i = 0; i < fields.length; i++) {
            Cell cell = headerTitle.createCell(index++ % fields.length);
            cell.setCellValue(header[i]);
        }
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(i + 1);
            T o = data.get(i);
            for (int j = 0; j < fields.length; j++) {
                Cell cell = row.createCell(index++ % fields.length);
                Object value;
                if (valueConverts != null && valueConverts[j] != null) {
                    value = valueConverts[j].convert(getValueByNameOrKey(o, fields[j]));
                } else {
                    value = getValueByNameOrKey(o, fields[j]);
                }
                try {
                    if (value instanceof Integer) {
                        cell.setCellValue(Integer.parseInt(value.toString()));
                    } else if (value instanceof Boolean) {
                        cell.setCellValue(Boolean.parseBoolean(value.toString()));
                    } else if (value instanceof Long) {
                        cell.setCellValue(Long.parseLong(value.toString()));
                    } else if (value instanceof Double) {
                        cell.setCellValue(Double.parseDouble(value.toString()));
                    } else {
                        cell.setCellValue(value.toString());
                    }
                } catch (Exception e) {
                    throw new RuntimeException("export VosData error", e);
                }
            }
        }
        try {
            wb.write(os);
        } catch (IOException e) {
            throw new RuntimeException("export VosData error", e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static <T> void exportDataAsCsv(List<T> data, String[] fields, String[] header, OutputStream os) {
        exportDataAsCsv(data, fields, header, null, os);
    }

    /**
     * 根据属性名获取属性值
     */
    private static Object getValueByNameOrKey(Object targetObject, Object fieldOrKey) {
        try {
            if (targetObject instanceof Map) {
                Map map = (Map) targetObject;
                return map.get(fieldOrKey);
            }
            String fieldName = fieldOrKey.toString();
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);

            try {
                Method method = targetObject.getClass().getMethod(getter, new Class[]{});
                return method.invoke(targetObject, new Object[]{});
            } catch (Exception e) {
                //e.printStackTrace();
                return targetObject.getClass().getField(fieldName).get(targetObject);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return "";
        }
    }

    public static Object[][] read(InputStream in, int sheetNum, String fileName) {
        Workbook wb = null;
        try {
            if (fileName == null) {
                if (POIFSFileSystem.hasPOIFSHeader(in)) {
                    wb = new HSSFWorkbook(in);
                } else {
                    wb = new XSSFWorkbook(in);
                }
            } else {
                if ((fileName.matches("^.+\\.(?i)(xlsx)$"))) {
                    wb = new XSSFWorkbook(in);
                } else {
                    wb = new HSSFWorkbook(in);
                }
            }
            Sheet sheet = wb.getSheetAt(sheetNum);
            return getTable(sheet);
        } catch (
                Exception e) {
            throw new RuntimeException("read excel exception - ", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException("close excel exception - ", e);
                }
            }
        }

    }

    public static Object[][] read(File file, int sheetNum, String fileName) {
        try {
            return read(new FileInputStream(file), sheetNum, fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object[][] getTable(Sheet sheet) {
        Object[][] list = new Object[sheet.getLastRowNum() + 1][];
        for (int r = sheet.getFirstRowNum(); r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                list[r] = null;
            } else {
                if (row.getLastCellNum() <= 0) {
                    continue;
                }
                list[r] = new Object[row.getLastCellNum()];
                for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
                    Cell cell = row.getCell(c);
                    list[r][c] = getValueFromCell(cell);
                }
            }
        }
        return list;
    }

    private static final Object getValueFromCell(Cell cell) {
        if (cell == null) {
            return StringUtils.EMPTY;
        }
        Object value = null;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue();
                } else {
                    value = cell.getNumericCellValue();
                }
                break;
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                double numericValue = cell.getNumericCellValue();
                if (HSSFDateUtil.isValidExcelDate(numericValue)) {
                    value = cell.getDateCellValue();
                } else {
                    value = String.valueOf(numericValue);
                }
                break;
            case Cell.CELL_TYPE_BLANK:
                value = StringUtils.EMPTY;
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_ERROR:
                value = String.valueOf(cell.getErrorCellValue());
                break;
            default:
                value = StringUtils.EMPTY;
                break;
        }
        return value;
    }

    public static void main(String[] args) {
        /*Object[][] read = read(new File("D:\\dowmload\\5400053-201812.xls"), 0);
        System.out.println(JSON.toJSON(read));*/
    }
}
