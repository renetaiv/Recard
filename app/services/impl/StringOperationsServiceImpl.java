package services.impl;

import services.StringOperationsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringOperationsServiceImpl implements StringOperationsService {

    @Override
    public String countDashAfterMainHeading(int headingLength, int idLength) {
        int num = headingLength - idLength;
        return "-".repeat(Math.max(0, num));
    }

    @Override
    public String countSpacesBefore(int headingLength, int valueLength) {
        int result = 0;
        StringBuilder spaces = new StringBuilder();

        int num = headingLength - valueLength;
        result = num / 2;

        spaces.append(" ".repeat(Math.max(0, result)));

        return spaces.toString();
    }

    @Override
    public String countSpacesAfter(int headingLength, int valueLength) {
        int result = 0;
        int num = headingLength - valueLength;

        StringBuilder spaces = new StringBuilder();

        if (num % 2 == 0) {
            result = num / 2;
        } else {
            result = num / 2 + 1;
        }

        spaces.append(" ".repeat(Math.max(0, result)));

        return spaces.toString();
    }

    @Override
    public String getLocalDateTimeFormattedString(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return localDateTime.format(dateTimeFormatter);
    }

    @Override
    public String makeColInfoCentred(String colValue, int headingLength) {
        return countSpacesBefore(headingLength, (colValue.length())) +
                colValue + countSpacesAfter(headingLength, colValue.length());
    }
}

