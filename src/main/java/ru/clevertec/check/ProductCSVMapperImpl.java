package ru.clevertec.check;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.stream.Collectors;

public class ProductCSVMapperImpl implements ProductCSVMapper {

    private static final String ID_HEADER = "id";
    private static final String DESCRIPTION_HEADER = "description";
    private static final String PRICE_HEADER = "price";
    private static final String QUANTITY_IN_STOCK_HEADER = "quantity_in_stock";
    private static final String WHOLESALE_PRODUCT_HEADER = "wholesale_product";

    private String separator = CSVReaderImpl.DEFAULT_CSV_SEPARATOR;

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public List<Product> fromCSV(String csvHeaders, List<String> csvContent) {

        List<String> headers = Arrays.asList(csvHeaders.split(separator));

        int idIndex = headers.indexOf(ID_HEADER);
        int descriptionIndex = headers.indexOf(DESCRIPTION_HEADER);
        int priceIndex = headers.indexOf(PRICE_HEADER);
        int quantityInStockIndex = headers.indexOf(QUANTITY_IN_STOCK_HEADER);
        int wholesaleProductIndex = headers.indexOf(WHOLESALE_PRODUCT_HEADER);

        List<Product> products = new ArrayList<>();
        csvContent.forEach(recordLine -> {
            List<String> recordContent = Arrays.asList(recordLine.split(separator));

            long id = Long.parseLong(recordContent.get(idIndex));
            String description = recordContent.get(descriptionIndex);
            BigDecimal price = new BigDecimal(recordContent.get(priceIndex));
            int quantityInStock = Integer.parseInt(recordContent.get(quantityInStockIndex));
            boolean wholesaleProduct = Boolean.parseBoolean(recordContent.get(wholesaleProductIndex));

            Product parsedProduct = Product.builder()
                    .id(id)
                    .description(description)
                    .price(price)
                    .quantityInStock(quantityInStock)
                    .wholesaleProduct(wholesaleProduct)
                    .build();

            products.add(parsedProduct);
        });

        return products;
    }

    @Override
    public SequencedMap<String, List<String>> toCSV(List<Product> content) {
        SequencedMap<String, List<String>> map = new LinkedHashMap<>();
        map.put(
                ID_HEADER + separator
                        + DESCRIPTION_HEADER + separator
                        + PRICE_HEADER + separator
                        + QUANTITY_IN_STOCK_HEADER + separator
                        + WHOLESALE_PRODUCT_HEADER,

                content.stream()
                        .map(p -> p.id() + separator
                                + p.description() + separator
                                + p.price() + separator
                                + p.quantityInStock() + separator
                                + p.wholesaleProduct())
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        return map;
    }
}
