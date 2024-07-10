package ru.clevertec.check;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;

public class CheckResponseCSVMapperImpl implements CheckResponseCSVMapper {

    private final static String SEPARATOR = ";";
    private final static String CURRENCY = "$";
    private final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");
    private final static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss");


    @Override
    public List<CheckResponse> fromCSV(String csvHeaders, List<String> csvContent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SequencedMap<String, List<String>> toCSV(List<CheckResponse> content) {

        CheckResponse checkResponse = content.getFirst();

        SequencedMap<String, List<String>> map = new LinkedHashMap<>();

        map.put(
                "Date" + SEPARATOR
                        + "Time",
                List.of(
                        dateFormatter.format(checkResponse.dateTimeIssuedOn()) + SEPARATOR
                                + timeFormatter.format(checkResponse.dateTimeIssuedOn())
                )
        );

        map.put(
                "QTY" + SEPARATOR
                        + "DESCRIPTION" + SEPARATOR
                        + "PRICE" + SEPARATOR
                        + "DISCOUNT" + SEPARATOR
                        + "TOTAL",
                checkResponse.boughtProducts()
                        .stream()
                        .sorted(Comparator.comparing(ProductResponse::description))
                        .map(pr -> pr.quantity() + SEPARATOR
                                + pr.description() + SEPARATOR
                                + pr.price() + CURRENCY + SEPARATOR
                                + pr.discount() + CURRENCY + SEPARATOR
                                + pr.total() + CURRENCY)
                        .toList()
        );

        if (checkResponse.usedDiscountCardIfAny() != null) {
            DiscountCardResponse usedDiscountCard = checkResponse.usedDiscountCardIfAny();
            map.put(
                    "DISCOUNT CARD" + SEPARATOR
                            + "DISCOUNT PERCENTAGE",
                    List.of(
                            usedDiscountCard.number() + SEPARATOR
                                    + usedDiscountCard.percentage() + "%"
                    )
            );
        }

        List<ProductResponse> boughtProducts = checkResponse.boughtProducts();

        BigDecimal totalWithoutDiscount = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (ProductResponse boughtProduct : boughtProducts) {
            totalWithoutDiscount = totalWithoutDiscount.add(boughtProduct.total());
            totalDiscount = totalDiscount.add(boughtProduct.discount());
        }

        BigDecimal totalWithDiscount = totalWithoutDiscount.subtract(totalDiscount);

        map.put(
                "TOTAL PRICE" + SEPARATOR
                        + "TOTAL DISCOUNT" + SEPARATOR
                        + "TOTAL WITH DISCOUNT",
                List.of(
                        totalWithoutDiscount.setScale(2, RoundingMode.CEILING) + CURRENCY + SEPARATOR
                                + totalDiscount.setScale(2, RoundingMode.CEILING) + CURRENCY + SEPARATOR
                                + totalWithDiscount.setScale(2, RoundingMode.CEILING) + CURRENCY
                )
        );

        return map;
    }
}
