package com.albertabdullin.controlwork.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateConverter {

    public static String getStringViewOfDate(Calendar c) {
        int dayOfMonth, month = 1;
        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        month += c.get(Calendar.MONTH);
        StringBuilder sb = new StringBuilder();
        if (dayOfMonth < 10) sb.append("0");
        sb.append(dayOfMonth).append(".");
        if (month < 10) sb.append("0");
        sb.append(month).append(".");
        sb.append(c.get(Calendar.YEAR));
        return sb.toString();
    }

    public static long convertStringDateToLong(String stringDate) throws ParseException {
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(f.parse(stringDate));
        calendar.add(Calendar.DATE, 1);
        return calendar.getTimeInMillis();
    }

}
