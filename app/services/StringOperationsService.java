package services;

import java.time.LocalDateTime;

public interface StringOperationsService {

    String countDashAfterMainHeading(int headingLength, int idLength);

    String countSpacesBefore(int headingLength, int valueLength);

    String countSpacesAfter(int headingLength, int valueLength);

    String getLocalDateTimeFormattedString(LocalDateTime localDateTime);

    String makeColInfoCentred(String colValue, int headingLength);
}
