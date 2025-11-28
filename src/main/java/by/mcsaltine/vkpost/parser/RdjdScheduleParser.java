// src/main/java/by/mcsaltine/vkpost/parser/RdjdScheduleParser.java
package by.mcsaltine.vkpost.parser;

import by.mcsaltine.vkpost.model.ScheduleLesson;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RdjdScheduleParser {

    public static List<ScheduleLesson> parse(InputStream inputStream) throws Exception {
        List<ScheduleLesson> lessons = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        String currentDay = null;
        List<CabinetInfo> cabinets = new ArrayList<>();

        for (Row row : sheet) {
            if (row.getRowNum() < 8) continue;

            String firstCell = getMergedCellValue(sheet, row.getRowNum(), 0);
            if (firstCell != null && isDayOfWeek(firstCell.trim())) {
                currentDay = firstCell.trim();
                cabinets.clear();
                continue;
            }

            if (currentDay != null && row.getCell(0) != null && row.getCell(0).toString().contains("Кабинет №")) {
                cabinets.clear();
                for (int col = 0; col <= 10; col += 2) {
                    Cell cell = row.getCell(col);
                    if (cell != null) {
                        String text = cell.toString().trim();
                        if (text.contains("Кабинет №")) {
                            String cabinet = extractCabinet(text);
                            String teacher = extractTeacher(text);
                            cabinets.add(new CabinetInfo(cabinet, teacher, col));
                        }
                    }
                }
                continue;
            }

            if (currentDay != null && !cabinets.isEmpty()) {
                Cell timeCell = row.getCell(0);
                if (timeCell != null) {
                    String timeStr = timeCell.toString().trim();
                    if (isTimeFormat(timeStr)) {
                        for (CabinetInfo cab : cabinets) {
                            Cell groupCell = row.getCell(cab.columnIndex + 1);
                            String group = groupCell != null ? groupCell.toString().trim() : "";
                            if (!group.isEmpty() && !group.equals("0")) {
                                lessons.add(new ScheduleLesson(currentDay, cab.cabinet, cab.teacher, timeStr, group));
                            }
                        }
                    }
                }
            }
        }
        workbook.close();
        return lessons;
    }

    private static boolean isDayOfWeek(String s) {
        return List.of("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота").contains(s);
    }

    private static boolean isTimeFormat(String s) {
        return s.matches("\\d{1,2}\\.\\d{2}-\\d{1,2}\\.\\d{2}");
    }

    private static String extractCabinet(String text) {
        var m = java.util.regex.Pattern.compile("№\\s*(\\d+)").matcher(text);
        return m.find() ? "Кабинет №" + m.group(1) : "Неизвестно";
    }

    private static String extractTeacher(String text) {
        var parts = text.split("педагог|Инструктор");
        if (parts.length > 1) {
            String t = parts[1].trim();
            return t.isEmpty() ? "Не указан" : t;
        }
        return "Не указан";
    }

    private static String getMergedCellValue(Sheet sheet, int row, int col) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            if (region.isInRange(row, col)) {
                Row r = sheet.getRow(region.getFirstRow());
                if (r != null) {
                    Cell c = r.getCell(region.getFirstColumn());
                    if (c != null) return c.toString();
                }
            }
        }
        Cell cell = sheet.getRow(row).getCell(col);
        return cell != null ? cell.toString() : null;
    }

    private record CabinetInfo(String cabinet, String teacher, int columnIndex) {}
}