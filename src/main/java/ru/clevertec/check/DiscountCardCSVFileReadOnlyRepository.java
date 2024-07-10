package ru.clevertec.check;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.stream.Collectors;

public class DiscountCardCSVFileReadOnlyRepository implements DiscountCardRepository {

    private static final String DEFAULT_PRODUCTS_CSV_PATH = "./src/main/resources/discountCards.csv";

    private final DiscountCardCSVMapper discountCardCSVMapper;
    private final CSVReader csvReader;

    private String discountCardCSVFilePath = DEFAULT_PRODUCTS_CSV_PATH;

    private List<DiscountCard> availableDiscountCards = null;

    public DiscountCardCSVFileReadOnlyRepository(DiscountCardCSVMapper discountCardCSVMapper, CSVReader csvReader) {
        this.discountCardCSVMapper = discountCardCSVMapper;
        this.csvReader = csvReader;
    }

    public String getDiscountCardCSVFilePath() {
        return discountCardCSVFilePath;
    }

    public void setDiscountCardCSVFilePath(String discountCardCSVFilePath) {
        this.discountCardCSVFilePath = discountCardCSVFilePath;
        loadDiscountCardsFromFile(discountCardCSVFilePath);
    }

    @Override
    public List<DiscountCard> findAll() {
        loadDiscountCardsIfNull();
        return new ArrayList<>(availableDiscountCards);
    }

    @Override
    public Page<DiscountCard> find(PageRequest pageRequest) {
        loadDiscountCardsIfNull();

        int pageNumber = pageRequest.pageNumber();
        int pageSize = pageRequest.pageSize();

        ArrayList<DiscountCard> pageData = availableDiscountCards.stream()
                .skip((long) pageNumber * pageSize)
                .limit(pageRequest.pageSize())
                .collect(Collectors.toCollection(ArrayList<DiscountCard>::new));

        return new Page<>(pageSize, pageNumber, pageData);
    }

    @Override
    public Optional<DiscountCard> findById(Long id) {
        loadDiscountCardsIfNull();

        return availableDiscountCards.stream()
                .filter(p -> id.equals(p.id()))
                .findAny();
    }

    @Override
    public void update(DiscountCard product) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long add(DiscountCard product) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeById(Long aLong) {
        throw new UnsupportedOperationException();
    }

    private void loadDiscountCardsIfNull() {
        if (availableDiscountCards == null) {
            loadDiscountCardsFromFile(discountCardCSVFilePath);
        }
    }

    private void loadDiscountCardsFromFile(String csvFilePath) {
        SequencedMap<String, List<String>> csvDiscountCardsMap = csvReader.readRecords(csvFilePath);
        if (csvDiscountCardsMap.size() != 1) {
            throw new BadRequestException("File should contain only the discount cards table");
        }
        Map.Entry<String, List<String>> csvDiscountCards = csvDiscountCardsMap.firstEntry();
        this.availableDiscountCards = discountCardCSVMapper.fromCSV(csvDiscountCards.getKey(), csvDiscountCards.getValue());
    }
}
