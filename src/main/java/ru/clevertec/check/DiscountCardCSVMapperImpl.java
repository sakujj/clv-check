package ru.clevertec.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;

public class DiscountCardCSVMapperImpl implements DiscountCardCSVMapper {

    private static final String ID_HEADER = "id";
    private static final String NUMBER_HEADER = "number";
    private static final String DISCOUNT_AMOUNT_HEADER = "discount_amount";

    private String separator = CSVReaderImpl.DEFAULT_CSV_SEPARATOR;

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public List<DiscountCard> fromCSV(String csvHeaders, List<String> csvContent) {
        List<String> headers = Arrays.asList(csvHeaders.split(separator));

        int idIndex = headers.indexOf(ID_HEADER);
        int numberIndex = headers.indexOf(NUMBER_HEADER);
        int discountAmountIndex = headers.indexOf(DISCOUNT_AMOUNT_HEADER);

        List<DiscountCard> discountCards = new ArrayList<>();
        csvContent.forEach(recordLine -> {
            List<String> recordContent = Arrays.asList(recordLine.split(separator));

            long id = Long.parseLong(recordContent.get(idIndex));
            int number = Integer.parseInt(recordContent.get(numberIndex));
            short discountAmount = Short.parseShort(recordContent.get(discountAmountIndex));

            DiscountCard parsedDiscountCard = DiscountCard.builder()
                    .id(id)
                    .number(number)
                    .amount(discountAmount)
                    .build();

            discountCards.add(parsedDiscountCard);
        });

        return discountCards;
    }

    @Override
    public SequencedMap<String, List<String>> toCSV(List<DiscountCard> content) {
        throw new UnsupportedOperationException();
    }
}
