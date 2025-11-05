
import type {  TableColumn } from "../types";
import dayjs from "dayjs";
import { useColumnSlots } from "../hooks/useColumnSlots";

const { getSlotName } = useColumnSlots();

const formatNumber = (value: any): string => {
  const num = Number(value);
  return isNaN(num) ? "0" : num.toLocaleString();
};

const formatBoolean = (value: any): string => {
  return value ? "✓" : "✗";
};

const formatCurrency = (value: any): string => {
  const num = Number(value);
  return isNaN(num) ? "$0.00" : `$${num.toFixed(2)}`;
};

const formatDate = (value: any): string => {
  if (!value) return "";
  const date = new Date(value);
  return isNaN(date.getTime())
    ? String(value)
    : `${date.getMonth() + 1}/${date.getDate()}/${date.getFullYear()}`;
};

const formatDateTime = (value: any): string => {
  if (!value) return "";
  try {
    // 使用dayjs处理带时区的时间格式，转换为当前系统时区
    const dateTime = dayjs(value);
    if (!dateTime.isValid()) {
      return String(value);
    }
    // 格式化为中文本地化时间格式
    return dateTime.format('YYYY-MM-DD HH:mm:ss');
  } catch (error) {
    console.error('DateTime formatting error:', error);
    return String(value);
  }
};

const formatString = (value: any): string => {
  return value != null ? String(value) : "";
};

// 暂存
const getColumnFormatter = (column: TableColumn) => {
  return (row: Record<string, any>, col: TableColumn, cellValue: any) => {
    const value = cellValue ?? row[col.prop];

    // Use custom formatter if provided
    if (col.formatter) {
      try {
        return col.formatter(value, row, col);
      } catch (error) {
        console.error("Formatting error:", error);
        return String(value || "");
      }
    }

    // Use built-in formatters based on type
    switch (col.type) {
      case "number":
        return formatNumber(value);
      case "boolean":
        return formatBoolean(value);
      case "currency":
        return formatCurrency(value);
      case "date":
        return formatDate(value);
      case "dateTime":
        return formatDateTime(value);
      default:
        return formatString(value);
    }
  };
};

const getCellValue = (row: Record<string, any>, column: TableColumn): string => {
  const value = row[column.prop];

  if (value === undefined || value === null) {
    return "-";
  }

  // Use custom formatter if provided
  if (column.formatter) {
    try {
      return column.formatter(value, row, column);
    } catch (error) {
      console.error("Formatting error:", error);
      return String(value || "");
    }
  }

  // Use built-in formatters based on type
  switch (column.type) {
    case "number":
      return formatNumber(value);
    case "boolean":
      return formatBoolean(value);
    case "currency":
      return formatCurrency(value);
    case "date":
      return formatDate(value);
    case "dateTime":
      return formatDateTime(value);
    default:
      return formatString(value);
  }
};

/**
 * 检查列是否使用自定义槽
 * @param column 列配置
 * @returns 槽名称或 null
 */
export const getColumnSlotName = (column: TableColumn): string | null => {
  return getSlotName(column);
}

/**
 * 获取单元格的格式化值（用于槽作用域参数）
 * @param row 行数据
 * @param column 列配置
 * @returns 格式化后的值
 */
export const getFormattedCellValue = (row: Record<string, any>, column: TableColumn): string => {
  return getCellValue(row, column);
}

/**
 * 获取单元格的原始值（用于槽作用域参数）
 * @param row 行数据
 * @param column 列配置
 * @returns 原始值
 */
export const getRawCellValue = (row: Record<string, any>, column: TableColumn): any => {
  return row[column.prop];
}

export {
  getColumnFormatter,
  getCellValue
}