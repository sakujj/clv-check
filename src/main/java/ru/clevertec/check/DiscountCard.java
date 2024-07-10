package ru.clevertec.check;

public record DiscountCard(
        long id,
        int number,
        short amount
) {

    public static DiscountCardBuilder builder() {
        return new DiscountCardBuilder();
    }

    public static class DiscountCardBuilder {

        long id;
        int number;
        short amount;

        private DiscountCardBuilder(){}

        public DiscountCardBuilder id(long id) {
            this.id = id;
            return this;
        }

        public DiscountCardBuilder number(int number) {
            this.number = number;
            return this;
        }

        public DiscountCardBuilder amount(short amount) {
            this.amount = amount;
            return this;
        }

        public DiscountCard build() {
            return new DiscountCard(id, number, amount);
        }
    }
}
