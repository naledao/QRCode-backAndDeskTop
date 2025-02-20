package xqq.kangnasi.xyz.webviewdemo.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

public class XlsxUtil {
    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return ""; // 如果单元格为空，返回空字符串
        }

        // 根据单元格的类型处理不同的值
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue(); // 字符串类型

            case NUMERIC:
                // 如果是日期格式，进行日期转换
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // 返回日期的字符串形式
                }
                return String.valueOf(cell.getNumericCellValue()); // 数值类型

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue()); // 布尔类型

            case FORMULA:
                try {
                    // 如果公式结果是数值
                    return String.valueOf(cell.getNumericCellValue());
                } catch (IllegalStateException e) {
                    // 如果公式结果是字符串
                    return cell.getStringCellValue();
                }

            case BLANK:
                return ""; // 空白单元格

            case ERROR:
                return ""; // 错误单元格的标识

            default:
                return ""; // 其他未知类型
        }
    }
}
