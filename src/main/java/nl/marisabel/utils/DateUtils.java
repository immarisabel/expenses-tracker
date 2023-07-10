package nl.marisabel.utils;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DateUtils {

 private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMyyyy");
 private static final DateTimeFormatter HEADER_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

 public static YearMonth parseToYearMonth(String month) {
  if (month == null || month.isEmpty()) {
   return YearMonth.now();
  }

  return YearMonth.parse(month, MONTH_FORMATTER);
 }

 public static String formatForHeader(YearMonth yearMonth) {
  return yearMonth.format(HEADER_FORMATTER);
 }

 public static String formatForMonth(YearMonth yearMonth) {
  return yearMonth.format(MONTH_FORMATTER);
 }
}
