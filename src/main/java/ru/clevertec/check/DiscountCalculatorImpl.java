package ru.clevertec.check;

public class DiscountCalculatorImpl implements DiscountCalculator{

    private static final byte DEFAULT_DISCOUNT_PERCENTAGE_ON_CARD = 2;

    private final DiscountCardRepository discountCardRepository;

    public DiscountCalculatorImpl(DiscountCardRepository discountCardRepository) {
        this.discountCardRepository = discountCardRepository;
    }

    @Override
    public byte calculateDiscountPercentage(DiscountCardRequest discountCardRequest) {
        if (discountCardRequest == null) {
            return 0;
        }

        return  (byte) (short) discountCardRepository.findAll().stream()
                .filter(dc -> dc.number() == discountCardRequest.number())
                .findAny()
                .map(DiscountCard::amount)
                .orElse((short)DEFAULT_DISCOUNT_PERCENTAGE_ON_CARD);
    }
}
