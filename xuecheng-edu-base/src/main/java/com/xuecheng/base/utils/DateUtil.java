package com.xuecheng.base.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Domenic
 * @Classname DateUtil
 * @Description 日期处理工具类
 * @Created by Domenic
 */
@Slf4j
public class DateUtil {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String YYYY_MM = "yyyy-MM";

    private static final Locale LOCALE = Locale.SIMPLIFIED_CHINESE;
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS, LOCALE);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD, LOCALE);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(HH_MM_SS, LOCALE);
    public static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM, LOCALE);

    /**
     * 返回一个默认格式的日期时间字符串
     * @param dateTime 日期时间
     * @return 日期时间字符串
     */
    public static String toDateTime(LocalDateTime dateTime) {
        return toDateTime(dateTime, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 返回一个指定格式的日期时间字符串
     * @param dateTime 日期时间
     * @param pattern 指定的格式
     * @return 日期时间字符串
     */
    public static String toDateTime(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern, LOCALE));
    }

    /**
     * 返回一个默认格式的日期字符串
     * @param date 日期
     * @return 日期字符串
     */
    public static String toDate(LocalDate date) {
        return toDate(date, YYYY_MM_DD);
    }

    /**
     * 返回一个指定格式的日期字符串
     * @param date 日期
     * @param pattern 指定的格式
     * @return 日期字符串
     */
    public static String toDate(LocalDate date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern(pattern, LOCALE));
    }

    /**
     * 向给定的 date 时间增加 hourToAdd 小时
     * @param date 给定的时间
     * @param hourToAdd 增加的小时数
     * @return 增加后的时间 {@link LocalDateTime}
     */
    public static LocalDateTime addExtraHour(LocalDateTime date, long hourToAdd) {
        if (date == null) {
            return null;
        }

        // 将 LocalDateTime 对象日期转换为 ZonedDateTime 对象 (使用系统默认时区)
        ZonedDateTime zonedDateTime = date.atZone(ZONE_ID);

        // 将指定的天数添加到日期
        ZonedDateTime updatedZonedDateTime = zonedDateTime.plusHours(hourToAdd);

        return updatedZonedDateTime.toLocalDateTime();
    }

    /**
     * 向给定的 date 时间增加 dayToAdd 天
     * @param date 给定的时间
     * @param dayToAdd 增加的天数
     * @return 增加后的时间 {@link LocalDateTime}
     */
    public static LocalDateTime addDaysToDate(LocalDateTime date, long dayToAdd) {
        if (date == null) {
            return null;
        }

        // 将 LocalDateTime 对象日期转换为 ZonedDateTime 对象 (使用系统默认时区)
        ZonedDateTime zonedDateTime = date.atZone(ZONE_ID);

        // 将指定的天数添加到日期
        ZonedDateTime updatedZonedDateTime = zonedDateTime.plusDays(dayToAdd);

        return updatedZonedDateTime.toLocalDateTime();
    }

    /**
     * 向给定的 date 时间增加 monthToAdd 月
     * @param date 给定的时间
     * @param monthToAdd 增加的月数
     * @return 增加后的时间 {@link LocalDateTime}
     */
    public static LocalDateTime addMonthsToDate(LocalDateTime date, long monthToAdd) {
        if (date == null) {
            return null;
        }

        // 将 LocalDateTime 对象日期转换为 ZonedDateTime 对象 (使用系统默认时区)
        ZonedDateTime zonedDateTime = date.atZone(ZONE_ID);

        // 将指定的月数添加到日期
        ZonedDateTime updatedZonedDateTime = zonedDateTime.plusMonths(monthToAdd);

        return updatedZonedDateTime.toLocalDateTime();
    }

    /**
     * 将字符串日期转换为 yyyy-MM-dd 格式的 {@link LocalDate} 对象
     * @param strDate 字符串格式的日期
     * @return yyyy-MM-dd 格式的 {@link LocalDate}
     */
    public static LocalDate parseDate(String strDate) {
        if (StringUtils.isBlank(strDate)) {
            return null;
        }

        try {
            return LocalDate.parse(strDate, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("解析字符串日期 \"{}\" 时, 发现格式错误, 正确格式为：{}", strDate, YYYY_MM_DD);
            throw e;
        }
    }

    /**
     * 将字符串日期转换为 yyyy-MM-dd HH:mm:ss 格式的 {@link LocalDate} 对象
     * @param strDateTime 字符串格式的日期
     * @return yyyy-MM-dd 格式的 {@link LocalDate}
     */
    public static LocalDateTime parseDateTime(String strDateTime) {
        if (StringUtils.isBlank(strDateTime)) {
            return null;
        }

        try {
            return LocalDateTime.parse(strDateTime, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("解析字符串日期时间 \"{}\" 时, 发现格式错误, 正确格式为：{}", strDateTime, YYYY_MM_DD_HH_MM_SS);
            throw e;
        }
    }

    /**
     * 获取日期所在月的所有天
     * @param date 指定的日期 (若为 {@code null} 则默认为当前月)
     * @return 日期列表 {@link List}
     */
    public static List<LocalDate> getAllDaysInMonth(LocalDate date) {
        // 若没有指定日期，则使用当前月份，否则使用传入的日期代表的月份
        LocalDate finalDate = date != null ? date : LocalDate.now(ZONE_ID);
        // 获取月份的总天数
        int daysInMonth = finalDate.lengthOfMonth();

        // 使用 Stream 创建一个从 1 到月份天数的范围，然后将每个日期转换为指定格式的字符串，并收集到一个列表中
        return IntStream.rangeClosed(1, daysInMonth)
                .mapToObj(day -> LocalDate.of(finalDate.getYear(), finalDate.getMonthValue(), day))
                .collect(Collectors.toList());
    }

    /**
     * 获取日期所在月的所有天，并保存为指定格式的字符串列表
     * @param month 指定的日期 (若为 {@code null} 则默认为当前月)
     * @param formatter {@link DateTimeFormatter} 日期格式
     * @return 指定格式的日期字符串列表 {@link List}
     */
    public static List<String> getAllDaysInMonthAsString(LocalDate date, DateTimeFormatter formatter) {
        // 若没有指定日期，则使用当前月份，否则使用传入的日期代表的月份
        LocalDate finalDate = date != null ? date : LocalDate.now(ZONE_ID);
        // 获取月份的总天数
        int daysInMonth = finalDate.lengthOfMonth();

        // 使用 Stream 创建一个从 1 到月份天数的范围，然后将每个日期转换为指定格式的字符串，并收集到一个列表中
        return IntStream.rangeClosed(1, daysInMonth)
                .mapToObj(day -> LocalDate.of(finalDate.getYear(), finalDate.getMonth(), day).format(formatter))
                .collect(Collectors.toList());
    }

    /**
     * 获取日期所在月的所有天，并保存为默认格式 (yyyy-MM-dd) 的字符串列表
     * @param date 指定的日期 (若为 {@code null} 则默认为当前月)
     * @return 指定格式的日期字符串列表 {@link List}
     */
    public static List<String> getAllDaysInMonthAsString(LocalDate date) {
        return getAllDaysInMonthAsString(date, DATE_FORMATTER);
    }

    /**
     * 获取日期所在月的所有天，并保存为指定格式的字符串列表
     * @param date 指定的日期 (若为 {@code null} 则默认为当前月)
     * @param pattern 字符串日期格式
     * @return 指定格式的日期字符串列表 {@link List}
     */
    public static List<String> getAllDaysInMonthAsString(LocalDate date, String pattern) {
        return getAllDaysInMonthAsString(date, DateTimeFormatter.ofPattern(pattern, LOCALE));
    }

    /**
    * 获取指定日期范围内的所有日期 (包含 start, 不包含 end)
    * @param begin 开始日期 (若为 {@code null}，则默认为当前月份的第一天)
    * @param end 结束日期 (若为 {@code null}，则默认为开始日期的下个月的第一天)
    * @return 日期列表 {@link List}
    */
    public static List<LocalDate> getAllDaysInRange(LocalDate begin, LocalDate end) {
        // 若参数为 null，则设置默认的开始和结束日期
        LocalDate beginDate = begin != null ? begin : LocalDate.now(ZONE_ID).withDayOfMonth(1);
        LocalDate endDate = end != null ? end : beginDate.plusMonths(1);

        // 使用 Stream 生成日期范围内的所有日期，并收集到一个列表中
        return Stream.iterate(beginDate, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(beginDate, endDate))
                .collect(Collectors.toList());
    }

    /**
     * 获取指定日期范围内的所有日期 (包含 start, 不包含 end)，并保存为指定格式的字符串列表
     * @param begin 开始日期 (若为 {@code null}，则默认为当前月份的第一天)
     * @param end 结束日期 (若为 {@code null}，则默认为开始日期的下个月的第一天)
     * @param formatter {@link DateTimeFormatter} 日期格式
     * @return 指定格式的日期字符串列表 {@link List}
     */
    public static List<String> getAllDaysInRangeAsString(LocalDate begin, LocalDate end, DateTimeFormatter formatter) {
        return Stream.iterate(begin, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(begin, end))
                .map(date -> date.format(formatter))
                .collect(Collectors.toList());
    }

    /**
     * 获取指定日期范围内的所有日期 (包含 start, 不包含 end)，，并保存为默认格式 (yyyy-MM-dd) 的字符串列表
     * @param begin 开始日期 (若为 {@code null}，则默认为当前月份的第一天)
     * @param end 结束日期 (若为 {@code null}，则默认为开始日期的下个月的第一天)
     * @return 默认格式的日期字符串列表 {@link List}
     */
    public static List<String> getAllDaysInRangeAsString(LocalDate begin, LocalDate end) {
        return getAllDaysInRangeAsString(begin, end, DATE_FORMATTER);
    }

    /**
     * 获取指定日期范围内的所有日期 (包含 start, 不包含 end)，并保存为指定格式的字符串列表
     * @param begin 开始日期 (若为 {@code null}，则默认为当前月份的第一天)
     * @param end 结束日期 (若为 {@code null}，则默认为开始日期的下个月的第一天)
     * @param pattern 字符串日期格式
     * @return 指定格式的日期字符串列表 {@link List}
     */
    public static List<String> getAllDaysInRangeAsString(LocalDate begin, LocalDate end, String pattern) {
        return getAllDaysInRangeAsString(begin, end, DateTimeFormatter.ofPattern(pattern, LOCALE));
    }

    /**
     * <p>
     * 格式化日期 (使用默认样式)<br/>
     * 支持 {@link LocalDate} 和 {@link LocalDateTime} 类型日期
     * </p>
     * @param time 日期类
     * @return 格式化后的日期字符串
     */
    public static String format(Object time) {
        if (time instanceof LocalDateTime) {
            DateTimeFormatter formatter = DATE_TIME_FORMATTER;
            return ((LocalDateTime) time).format(formatter);
        } else if (time instanceof LocalDate) {
            DateTimeFormatter formatter = DATE_FORMATTER;
            return ((LocalDate) time).format(formatter);
        } else {
            log.error("格式化日期时, 传入参数必须为 LocalDate 或 LocalDateTime 类型, 实际传入的类型为: {}", time.getClass().getName());
            throw new IllegalArgumentException("格式化日期时, 传入参数必须为 LocalDate 或 LocalDateTime 类型");
        }
    }

    /**
     * <p>
     * 格式化日期 (使用自定义样式)<br/>
     * 支持 {@link LocalDate} 和 {@link LocalDateTime} 类型日期
     * </p>
     * @param time 日期类
     * @param pattern 自定义格式化样式
     * @return 格式化后的日期字符串
     */
    public static String format(Object time, String pattern) {
        if (time instanceof LocalDateTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, LOCALE);
            return ((LocalDateTime) time).format(formatter);
        } else if (time instanceof LocalDate) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, LOCALE);
            return ((LocalDate) time).format(formatter);
        } else {
            log.error("格式化日期时, 传入参数必须为 LocalDate 或 LocalDateTime 类型, 实际传入的类型为: {}", time.getClass().getName());
            throw new IllegalArgumentException("格式化日期时, 传入参数必须为 LocalDate 或 LocalDateTime 类型");
        }
    }

    /**
     * 获取当前日期的格式化字符串
     * @return 表示当前日期的字符串
     */
    public static String getCurrentDateAsString() {
        return format(LocalDate.now(ZONE_ID));
    }

    /**
     * 获取当前时间的格式化字符串
     * @return 表示当前日期和时间的字符串
     */
    public static String getCurrentDateTimeAsString() {
        return format(LocalDateTime.now(ZONE_ID));
    }

    /**
     * 获取指定日期前一天的日期
     * @param specifiedDay 指定日期，格式为 "yyyy-MM-dd HH:mm:ss"
     * @return 前一天的日期
     */
    public static String getTheDayBefore(String specifiedDay) {
        return format(parseDateTime(specifiedDay).minusDays(1));
    }

    /**
     * 获取指定日期前一天的日期
     * @param specifiedDay 指定日期，格式为 "yyyy-MM-dd HH:mm:ss"
     * @return 前一天的日期
     */
    public static String getTheDayAfter(String specifiedDay) {
        return format(parseDateTime(specifiedDay).plusDays(1));
    }

    /**
     * 获取指定周的第一天
     * @param specifiedDate 指定日期，格式为 "yyyy-MM-dd"
     * @return 表示指定周第一天的 {@link LocalDate}
     */
    public static LocalDate getFirstDayOfWeek(String specifiedDate) {
        LocalDate date = parseDate(specifiedDate);
        int daysSinceMonday = date.getDayOfWeek().ordinal() - DayOfWeek.MONDAY.ordinal();
        // 将日期设置为当前周的第一天，并返回
        return date.with(DayOfWeek.MONDAY).minusDays(daysSinceMonday);
    }

    /**
     * 获取指定月份的第一天
     * @param specifiedDate 指定日期，格式为 "yyyy-MM-dd"
     * @return 表示指定月第一天的 {@link LocalDate}
     */
    public static LocalDate getFirstDayOfMonth(String specifiedDate) {
        return parseDate(specifiedDate).withDayOfMonth(1);
    }

    /**
     * 获取当前周的第一天
     * @return 表示当前周第一天的 {@link LocalDate}
     */
    public static LocalDate getFirstDayOfCurrentWeek() {
        LocalDate today = LocalDate.now(ZONE_ID);
        int daysSinceMonday = today.getDayOfWeek().ordinal() - DayOfWeek.MONDAY.ordinal();
        // 将日期设置为当前周的第一天，并返回
        return today.with(DayOfWeek.MONDAY).minusDays(daysSinceMonday);
    }

    /**
     * 返回当前月份的第一天
     * @return 表示当月第一天的 {@link LocalDate}
     */
    public static LocalDate getFirstDayOfCurrentMonth() {
        return LocalDate.now(ZONE_ID).withDayOfMonth(1);
    }

    /**
     * 获取指定周的最后一天
     * @return 表示指定周最后一天的 {@link LocalDate}
     */
    public static LocalDate getLastDayOfWeek(String specifiedDate) {
        LocalDate date = parseDate(specifiedDate);
        int daysUntilSunday = DayOfWeek.SUNDAY.ordinal() - date.getDayOfWeek().ordinal();
        // 将日期设置为当前周的最后一天，并返回
        return date.with(DayOfWeek.SUNDAY).plusDays(daysUntilSunday);
    }

    /**
     * 获取指定月份的最后一天
     * @param specifiedDate 指定日期，"yyyy-MM-dd" 格式
     * @return 表示指定月最后一天的 {@link LocalDate}
     */
    public static LocalDate getLastDayOfMonth(String specifiedDate) {
        LocalDate date = parseDate(specifiedDate);
        // 将日期设置为当前月的最后一天，并返回
        return date.withDayOfMonth(date.getMonth().maxLength());
    }

    /**
     * 获取当前周的最后一天
     * @return 表示当前周最后一天的 {@link LocalDate}
     */
    public static LocalDate getLastDayOfCurrentWeek() {
        LocalDate today = LocalDate.now(ZONE_ID);
        int daysUntilSunday = DayOfWeek.SUNDAY.ordinal() - today.getDayOfWeek().ordinal();
        // 将日期设置为当前周的最后一天，并返回
        return today.with(DayOfWeek.SUNDAY).plusDays(daysUntilSunday);
    }

    /**
     * 获取当前月份的最后一天
     * @return 表示当月最后一天的 {@link LocalDate}
     */
    public static LocalDate getLastDayOfCurrentMonth() {
        LocalDate date = LocalDate.now(ZONE_ID);
        // 将日期设置为当前月的最后一天，并返回
        return date.withDayOfMonth(date.getMonth().maxLength());
    }

    /**
     * 获取昨天开始的时间
     * @return 表示时间的字符串
     */
    public static final String getYesterdayStart() {
        return LocalDate.now(ZONE_ID).minusDays(1)
                .format(DATE_FORMATTER) + " 00:00:00";
    }

    /**
     * 获取昨天结束的时间
     * @return 表示时间的字符串
     */
    public static final String getYesterdayEnd() {
        return LocalDate.now(ZONE_ID).minusDays(1)
                .format(DATE_FORMATTER) + " 23:59:59";
    }

    /**
     * 获取今天开始的时间
     * @return 表示时间的字符串
     */
    public static final String getTodayStart() {
        return LocalDate.now(ZONE_ID).minusDays(1)
                .format(DATE_FORMATTER) + " 00:00:00";
    }

    /**
     * 获取今天结束的时间
     * @return 表示时间的字符串
     */
    public static final String getTodayEnd() {
        return LocalDate.now(ZONE_ID).minusDays(1)
                .format(DATE_FORMATTER) + " 23:59:59";
    }

    /**
     * 获取指定日期代表的周，是该月中的第几周
     * @param specifiedDate 指定日期，格式为 "yyyy-MM-dd"
     * @return 第几周
     */
    public static int getWeekIndexOfMonth(String specifiedDate) {
        LocalDate date = LocalDate.parse(specifiedDate, DATE_FORMATTER);
        TemporalField weekOfMonth = WeekFields.of(Locale.getDefault()).weekOfMonth();
        return date.get(weekOfMonth);
    }

    /**
    * 计算 当前时间 和 目标时间 之间相差的秒数
    * @param destinationTime 目标时间，格式为 "yyyy-MM-dd HH:mm:ss"
    * @return 相差的秒数
    */
    public static long getSecondsToDestinationTime(String destinationTime) {
        LocalDateTime now = LocalDateTime.now(ZONE_ID);
        LocalDateTime dest = LocalDateTime.parse(destinationTime, DATE_TIME_FORMATTER);
        return ChronoUnit.SECONDS.between(now, dest);
    }

    /**
     * 计算两个 {@link LocalDateTime} 之间相差的毫秒数
     * @param start 开始时间
     * @param end 结束时间
     * @return 相差的毫秒数
     */
    public static long getMillisecondsBetween(LocalDateTime start, LocalDateTime end) {
        ZonedDateTime zonedStart = ZonedDateTime.of(start, ZONE_ID);
        ZonedDateTime zonedEnd = ZonedDateTime.of(end, ZONE_ID);
        return zonedEnd.toInstant().toEpochMilli() - zonedStart.toInstant().toEpochMilli();
    }

}
