package ru.clevertec.check;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.stream.Collectors;

public class ProductCSVFileRepository implements ProductFileRepository {

    private static final String DEFAULT_PRODUCTS_CSV_PATH = "./src/main/resources/products.csv";

    private final ProductCSVMapper productCSVMapper;
    private final CSVReader csvReader;
    private final CSVWriter csvWriter;

    private List<Product> availableProducts;

    private String productsCSVFilePath = DEFAULT_PRODUCTS_CSV_PATH;

    public ProductCSVFileRepository(ProductCSVMapper productCSVMapper,
                                    CSVReader csvReader,
                                    CSVWriter csvWriter) {
        this.productCSVMapper = productCSVMapper;
        this.csvReader = csvReader;
        this.csvWriter = csvWriter;
    }

    public String getProductsCSVFilePath() {
        return productsCSVFilePath;
    }

    public void setProductsCSVFilePath(String productsCSVFilePath) {
        this.productsCSVFilePath = productsCSVFilePath;
        loadProductsFromFile(productsCSVFilePath);
    }

    private void loadProductsIfNull() {
        if (availableProducts == null) {
            loadProductsFromFile(productsCSVFilePath);
        }
    }

    @Override
    public List<Product> findAll() {
        loadProductsIfNull();
        return new ArrayList<>(availableProducts);
    }

    @Override
    public Page<Product> find(PageRequest pageRequest) {
        loadProductsIfNull();

        int pageNumber = pageRequest.pageNumber();
        int pageSize = pageRequest.pageSize();

        ArrayList<Product> pageData = availableProducts.stream()
                .skip((long) pageNumber * pageSize)
                .limit(pageRequest.pageSize())
                .collect(Collectors.toCollection(ArrayList<Product>::new));

        return new Page<>(pageSize, pageNumber, pageData);
    }

    @Override
    public Optional<Product> findById(Long id) {
        loadProductsIfNull();

        return availableProducts.stream()
                .filter(p -> id.equals(p.id()))
                .findAny();
    }

    @Override
    public void update(Product product) {
        update(product, true);
    }

    @Override
    public void update(Product product, boolean doFlush) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateQuantityHavingId(Long id, int updateAmount) {
        updateQuantityHavingId(id, updateAmount, true);
    }

    @Override
    public void updateQuantityHavingId(Long id, int updateAmount, boolean doFlush) {
        loadProductsIfNull();

        for (int i = 0; i < availableProducts.size(); i++) {
            Product product = availableProducts.get(i);

            if (!id.equals(product.id())) {
                continue;
            }

            int newQuantity = product.quantityInStock() + updateAmount;
            availableProducts.set(i,
                    Product.builder()
                            .quantityInStock(newQuantity)
                            .id(product.id())
                            .wholesaleProduct(product.wholesaleProduct())
                            .description(product.description())
                            .price(product.price())
                            .build());
            if (doFlush) {
                flush();
            }
            return;
        }
    }

    @Override
    public Long add(Product product) {
        return add(product, true);
    }

    @Override
    public void removeById(Long aLong) {
        removeById(aLong, true);
    }


    @Override
    public Long add(Product product, boolean doFlush) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeById(Long id, boolean doFlush) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void flush() {
        if (availableProducts == null) {
            return;
        }

        Map<String, List<String>> csvProducts = productCSVMapper.toCSV(availableProducts);
        SequencedMap<String, List<String>> csvProductsWrapper = new LinkedHashMap<>();
        Entry<String, List<String>> table = csvProducts.entrySet().iterator().next();
        csvProductsWrapper.put(table.getKey(), table.getValue());

        csvWriter.writeRecordTables(productsCSVFilePath, csvProductsWrapper);
    }

    private void loadProductsFromFile(String csvFilePath) {
        SequencedMap<String, List<String>> csvProductsMap = csvReader.readRecords(csvFilePath);
        if (csvProductsMap.size() != 1) {
            throw new BadRequestException("File should contain only the products table");
        }
        Entry<String, List<String>> csvProducts = csvProductsMap.firstEntry();
        this.availableProducts = productCSVMapper.fromCSV(csvProducts.getKey(), csvProducts.getValue());
    }
}
