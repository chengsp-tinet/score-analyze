package com.csp.app.util;

import com.alibaba.fastjson.JSONObject;
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 电子表格工具类
 *
 * @author chengsp on 2019年3月24日22:35:23
 */
public class ExcelUtil {
    /**
     * 分批导出数据,适合导出数据量特别大的情况
     * @param <T>
     */
    public static class ExportByPageHelper<T> {
        private BufferedWriter br;
        private String[] tempFields;
        private String[] tempHeaders;

        public ExportByPageHelper(BufferedWriter br) {
            this.br = br;
        }

        public void exportAsCscByPage(List<T> data, BufferedWriter br, String[] fields, String[] headers) {
            if (tempFields == null) {
                if (fields == null || fields.length == 0) {
                    tempFields = getKeysOrFieldsFromBean(data.get(0));
                } else {
                    tempFields = fields;
                }
            }
            if (tempHeaders == null) {
                exportByBufferedWriter(data, br, tempFields, tempFields);
            } else {
                exportByBufferedWriter(data, br, tempFields, null);
            }

            if (tempHeaders == null) {
                if (headers == null || headers.length == 0) {
                    tempHeaders = tempFields;
                } else {
                    tempHeaders = headers;
                }
            }
        }

        public void close() {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 字段排序
     */
    static class Com implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }
    // 字段排序比较器
    private final static Com com = new Com();

    /**
     * 统一导出方法
     * @param data
     * @param fields
     * @param headers
     * @param out
     * @param <T>
     */
    public static <T> void exportAsExcel(List<T> data, String[] fields, String[] headers, OutputStream out) {
        if (fields == null || headers == null) {
            throw new RuntimeException("fields or header can'selectPart be null.");
        }
        Workbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("excel");
        Row headerTitle = sheet.createRow(0);
        int index = 0;
        for (int i = 0; i < fields.length; i++) {
            Cell cell = headerTitle.createCell(index++ % fields.length);
            cell.setCellValue(headers[i]);
        }
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(i + 1);
            T o = data.get(i);
            for (int j = 0; j < fields.length; j++) {
                Cell cell = row.createCell(index++ % fields.length);
                Object value = getValueByNameOrKey(o, fields[j]);
                String valueStr = String.valueOf(value);
                if (value != null) {
                    try {
                        if (value instanceof String) {
                            cell.setCellValue(valueStr);
                        } else if (value instanceof Integer) {
                            cell.setCellValue(Integer.parseInt(valueStr));
                        } else if (value instanceof Long) {
                            cell.setCellValue(Long.parseLong(valueStr));
                        } else if (value instanceof Double) {
                            cell.setCellValue(Double.parseDouble(valueStr));
                        } else if (value instanceof Boolean) {
                            cell.setCellValue(Boolean.parseBoolean(valueStr));
                        } else if (value instanceof Date) {
                            cell.setCellValue(DateUtil.format((Date) value));
                        } else {
                            cell.setCellValue(valueStr);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("export VosData error", e);
                    }
                } else {
                    cell.setCellValue("");
                }
            }
        }
        try {
            wb.write(out);
        } catch (IOException e) {
            throw new RuntimeException("export data error", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
            Method method = targetObject.getClass().getMethod(getter);
            return method.invoke(targetObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 万能读取电子表格
     *
     * @param in         输入流
     * @param sheetNum
     * @param fileName   文件名
     * @param heads      表头
     * @param resultType 返回类型
     * @param <T>        返回类型
     * @return
     */
    public static <T> List<T> read(InputStream in, int sheetNum, String fileName, String heads[], Class<T> resultType) {
        Workbook wb = null;
        try {
            if (fileName == null) {
                if (POIFSFileSystem.hasPOIFSHeader(in)) {
                    wb = new HSSFWorkbook(in);
                } else {
                    wb = new XSSFWorkbook(in);
                }
            } else {
                if (fileName.endsWith(".xls")) {
                    wb = new XSSFWorkbook(in);
                } else if (fileName.endsWith(".csv")) {
                    return convertCsvToBean(in, heads, resultType);
                } else {
                    wb = new HSSFWorkbook(in);
                }
            }
            Sheet sheet = wb.getSheetAt(sheetNum);
            if (heads == null) {
                return convertSheetToBean(sheet, readRowAt(sheet, 0), resultType);
            } else {
                return convertSheetToBean(sheet, heads, resultType);
            }
        } catch (Exception e) {
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

    /**
     * 获取某一行数据
     *
     * @param sheet
     * @param rowNum
     * @return
     */
    private static String[] readRowAt(Sheet sheet, int rowNum) {
        String[] heads = new String[sheet.getLastRowNum() + 1];
        for (int i = 0; i < heads.length; i++) {
            heads[i] = String.valueOf(i);
        }
        Row row = sheet.getRow(rowNum);
        if (row != null) {
            if (row.getLastCellNum() > 0) {
                for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
                    Cell cell = row.getCell(c);
                    heads[c] = String.valueOf(getValueFromCell(cell));
                }
            }
        }
        return heads;
    }

    /**将sheet转化为bean
     * @param sheet
     * @param heads
     * @param resultType
     * @param <T>
     * @return
     */
    private static <T> List<T> convertSheetToBean(Sheet sheet, String[] heads, Class<T> resultType) {
        List<T> resultList = new LinkedList<>();
        for (int r = sheet.getFirstRowNum(); r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row != null) {
                if (row.getLastCellNum() > 0) {
                    JSONObject tableRow = new JSONObject();
                    for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
                        Cell cell = row.getCell(c);
                        Object valueFromCell = getValueFromCell(cell);
                        tableRow.put(heads[c], valueFromCell);
                    }
                    T t = tableRow.toJavaObject(resultType);
                    resultList.add(t);
                }
            }
        }
        return resultList;
    }

    /**
     * 将csv文件解析为bean
     *
     * @param in
     * @param heads
     * @param resultType
     * @param <T>
     * @return
     */
    private static <T> List<T> convertCsvToBean(InputStream in, String[] heads, Class<T> resultType) {
        List<T> resultList = new LinkedList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "GB2312"));
            String[] headsTemp;
            if (heads != null && heads.length != 0) {
                headsTemp = heads;
            } else {
                headsTemp = br.readLine().split(",");
            }
            for (int i = 0; i < headsTemp.length; i++) {
                headsTemp[i] = headsTemp[i].replaceAll("\"", "");
            }
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("\"", "");
                String fields[] = line.split(",");
                JSONObject tableRow = new JSONObject();
                for (int i = 0; i < fields.length; i++) {
                    tableRow.put(headsTemp[i], fields[i]);
                }
                T t = tableRow.toJavaObject(resultType);
                resultList.add(t);
            }
        } catch (IOException e) {
            throw new RuntimeException("读取csv文件异常:", e);
        } catch (Exception e) {
            throw new RuntimeException("解析csv文件异常:", e);
        }
        return resultList;
    }

    /**
     * 写一行数据方法
     *
     * @param row
     * @param csvWriter
     * @throws IOException
     */
    private static void writeRow(List<Object> row, BufferedWriter csvWriter) {
        try {
            // 写入文件头部
            for (Object data : row) {
                StringBuffer sb = new StringBuffer();
                String rowStr = sb.append("\"").append(data).append("\",").toString();
                csvWriter.write(rowStr);
            }
            csvWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeHead(String[] row, BufferedWriter csvWriter) {
        try {
            // 写入文件头部
            for (String data : row) {
                StringBuffer sb = new StringBuffer();
                String rowStr = sb.append("\"").append(data).append("\",").toString();
                csvWriter.write(rowStr);
            }
            csvWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取bean的属性
     * @param target
     * @return
     */
    private static String[] getKeysOrFieldsFromBean(Object target) {
        if (target != null) {
            if (target instanceof Map) {
                Object[] objects = ((Map) target).keySet().toArray();
                String[] keys = new String[objects.length];
                for (int i = 0; i < objects.length; i++) {
                    keys[i] = String.valueOf(objects[i]);
                }
                Arrays.sort(keys, com);
                return keys;
            } else {
                Field[] fields = target.getClass().getDeclaredFields();
                List<String> keys = new ArrayList<>();
                for (int i = 0; i < fields.length; i++) {
                    if (!Modifier.isStatic(fields[i].getModifiers())) {
                        keys.add(fields[i].getName());
                    }
                }
                keys.sort(com);
                return keys.toArray(new String[]{});
            }
        }
        return null;
    }

    /**
     * 导出bean数据为Csv
     *
     * @param data
     * @param fields
     * @param headers
     * @param out
     * @param <T>
     */
    public static <T> void exportAsCsv(List<T> data, String[] fields, String[] headers, OutputStream out) {
        BufferedWriter br = null;
        try {
            String[] tempFields;
            if (fields == null || fields.length == 0) {
                tempFields = getKeysOrFieldsFromBean(data.get(0));
            } else {
                tempFields = fields;
            }
            String[] tempHeaders;
            if (headers != null) {
                tempHeaders = headers;
            } else {
                tempHeaders = tempFields;
            }
            br = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
            exportByBufferedWriter(data, br, tempFields, tempHeaders);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 不关闭流导出为csv
     * @param data
     * @param br
     * @param tempFields
     * @param tempHeaders
     * @param <T>
     */
    private static <T> void exportByBufferedWriter(List<T> data, BufferedWriter br, String[] tempFields, String[] tempHeaders) {
        if (tempHeaders != null) {

            writeHead(tempHeaders, br);
        }
        for (T row : data) {
            List<Object> rowData = new LinkedList<>();
            for (String field : tempFields) {
                Object value = getValueByNameOrKey(row, field);
                if (value instanceof Date) {
                    Date date = (Date) value;
                    rowData.add(DateUtil.format(date));
                } else {
                    rowData.add(value);
                }
            }
            writeRow(rowData, br);
        }
    }

    /**
     * 获取cell的值
     *
     * @param cell
     * @return
     */
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

}
