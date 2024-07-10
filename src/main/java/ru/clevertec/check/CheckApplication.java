package ru.clevertec.check;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;

public class CheckApplication {

    private static final String DEFAULT_RESULT_CSV_FILE_PATH = "./result.csv";


    private CheckApplication() {
    }

    public static void run(String[] args) {

        CSVWriter errorWriter = new CSVWriterImpl();

        SingletonContextFactory contextFactory = createCustomContextFactory();
        try (SingletonContext context = contextFactory.createOrReturnExisting()) {

            ArgsParser argsParser = context.getByClass(ArgsParser.class);
            ProductService productService = context.getByClass(ProductService.class);
            DiscountCalculator discountCalculator = context.getByClass(DiscountCalculator.class);
            CheckResponse checkResponse = buildCheckOrThrowException(
                    args,
                    argsParser,
                    productService,
                    discountCalculator);

            CheckResponseCSVMapper checkResponseCSVMapper = context.getByClass(CheckResponseCSVMapper.class);
            CSVWriter csvWriter = context.getByClass(CSVWriter.class);
            publishCheck(checkResponse, checkResponseCSVMapper, csvWriter);

        } catch (BadRequestException e) {
            publishError("BAD REQUEST", errorWriter);
            throw new RuntimeException(e);
        } catch (NotEnoughMoneyException e) {
            publishError("NOT ENOUGH MONEY", errorWriter);
            throw new RuntimeException(e);
        } catch (Exception e) {
            publishError("INTERNAL SERVER ERROR", errorWriter);
            throw new RuntimeException(e);
        }
    }

    private static void publishCheck(CheckResponse checkResponse,
                                     CheckResponseCSVMapper checkResponseCSVMapper,
                                     CSVWriter csvWriter) {

        SequencedMap<String, List<String>> csvCheckResponse = checkResponseCSVMapper.toCSV(List.of(checkResponse));

        printCsvTablesToStdout(csvCheckResponse);

        csvWriter.writeRecordTables(DEFAULT_RESULT_CSV_FILE_PATH, csvCheckResponse);
    }

    private static void publishError(String errorMessage, CSVWriter csvWriter) {

        SequencedMap<String, List<String>> csvErrorMsg = new LinkedHashMap<>();
        csvErrorMsg.put("ERROR", List.of(errorMessage));

        printCsvTablesToStdout(csvErrorMsg);

        csvWriter.writeRecordTables(DEFAULT_RESULT_CSV_FILE_PATH, csvErrorMsg);
    }

    private static void printCsvTablesToStdout(SequencedMap<String, List<String>> csvCheckResponse) {
        System.out.println();
        System.out.println("-------------CHECK-------------");
        System.out.println();
        csvCheckResponse.forEach((headers, contentRecords) -> {
            System.out.println(headers);
            contentRecords.forEach(System.out::println);
            System.out.println();
        });
        System.out.println("----------CHECK--END----------");
        System.out.println();
    }

    private static SingletonContextFactoryImpl createCustomContextFactory() {
        return new SingletonContextFactoryImpl(ctx -> {
            ArgsParser argsParser = new ArgsParserImpl();
            ctx.put(ArgsParser.class, argsParser);

            CSVReader csvReader = new CSVReaderImpl();
            ctx.put(CSVReader.class, csvReader);

            CSVWriter csvWriter = new CSVWriterImpl();
            ctx.put(CSVWriter.class, csvWriter);

            ProductCSVMapper productCSVMapper = new ProductCSVMapperImpl();
            ctx.put(ProductCSVMapper.class, productCSVMapper);

            ProductFileRepository productFileRepository = new ProductCSVFileRepository(
                    productCSVMapper, csvReader, csvWriter);
            ctx.put(ProductFileRepository.class, productFileRepository);

            DiscountCardCSVMapper discountCardCSVMapper = new DiscountCardCSVMapperImpl();
            ctx.put(DiscountCardCSVMapper.class, discountCardCSVMapper);

            DiscountCardRepository discountCardRepository = new DiscountCardCSVFileReadOnlyRepository(
                    discountCardCSVMapper, csvReader);
            ctx.put(DiscountCardRepository.class, discountCardRepository);

            ProductResponseMapper productResponseMapper = new ProductResponseMapperImpl();
            ctx.put(ProductResponseMapper.class, productResponseMapper);

            CheckResponseCSVMapper checkResponseCSVMapper = new CheckResponseCSVMapperImpl();
            ctx.put(CheckResponseCSVMapper.class, checkResponseCSVMapper);

            DiscountCalculator discountCalculator = new DiscountCalculatorImpl(discountCardRepository);
            ctx.put(DiscountCalculator.class, discountCalculator);

            ProductService productService = new ProductServiceImpl(productFileRepository, productResponseMapper);
            ctx.put(ProductService.class, productService);
        });
    }

    private static CheckResponse buildCheckOrThrowException(String[] args,
                                                            ArgsParser argsParser,
                                                            ProductService productService,
                                                            DiscountCalculator discountCalculator) {

        ArgsParser.Args parsedArgs = argsParser.parseArgs(args);

        DiscountCardRequest discountCardRequest = parsedArgs.discountCardRequest();
        DiscountCardResponse discountCardResponse = getDiscountCardResponse(
                discountCardRequest,
                discountCalculator
        );

        List<ProductRequest> productRequests = parsedArgs.productRequests();
        Map<ProductRequest, Product> requestedProductsData = productService.checkIfAvailableAndReturnGrouped(productRequests);

        List<ProductResponse> productResponses = productService
                .getProductResponsesForRequestedData(requestedProductsData, discountCardResponse);

        productService.checkIfEnoughMoneySupplied(productResponses, parsedArgs.debitCardRequest().balance());

        productService.updateProductQuantity(productRequests);

        return new CheckResponse(LocalDateTime.now(), productResponses, discountCardResponse);
    }

    private static DiscountCardResponse getDiscountCardResponse(DiscountCardRequest discountCardRequest,
                                                                DiscountCalculator discountCalculator) {
        return discountCardRequest == null ? null :
                new DiscountCardResponse(
                        discountCardRequest.number(),
                        discountCalculator.calculateDiscountPercentage(discountCardRequest)
                );
    }

}
