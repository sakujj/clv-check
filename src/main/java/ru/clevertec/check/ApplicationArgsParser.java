package ru.clevertec.check;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationArgsParser {

    private static final String DEBIT_CARD_BALANCE = "balanceDebitCard";
    private static final String DISCOUNT_CARD = "discountCard";
    private static final String SEPARATOR = "=";

    private ApplicationArgsParser() {
    }

    public record ApplicationArgs(
            Map<Long, Integer> idToQuantity,
            int discountCardNumber,
            BigDecimal debitCardBalance
    ) {
    }

    public static ApplicationArgs parse(String[] args) {
        List<String> argsList = new ArrayList<>(Arrays.asList(args));

        BigDecimal debitCardBalance = extractDebitCardBalanceAndRemoveFromArgs(argsList);
        int discountCardNumber = extractDiscountCardNumberAndRemoveFromArgs(argsList);
        Map<Long, Integer> idToQuantity = extractIdToQuantityAndRemoveFromArgs(argsList);

        if (idToQuantity.isEmpty()) {
            throw new BadRequestException("No 'id-quantity' entries were specified");
        }

        if (!argsList.isEmpty()) {
            String errorMsg = String.format("%s%s", "Unknown options are present: ", String.join(", ", argsList));
            throw new BadRequestException(errorMsg);
        }

        return new ApplicationArgs(idToQuantity, discountCardNumber, debitCardBalance);
    }

    private static BigDecimal extractDebitCardBalanceAndRemoveFromArgs(List<String> argsList) {

        String debitCardBalancePrefix = String.format("%s%s", DEBIT_CARD_BALANCE, SEPARATOR);
        List<String> debitCardBalanceArgs = StringUtils.findStringsWithPrefix(argsList, debitCardBalancePrefix);

        if (debitCardBalanceArgs.isEmpty()) {
            throw new BadRequestException("Debit card balance should be specified");
        }
        if (debitCardBalanceArgs.size() > 1) {
            throw new BadRequestException("More than one debit card balances were specified");
        }

        String debitCardBalanceArg = debitCardBalanceArgs.getFirst();
        argsList.remove(debitCardBalanceArg);

        String debitCardBalanceValue = StringUtils.removePrefix(debitCardBalanceArg, debitCardBalancePrefix)
                .orElseThrow(() -> new BadRequestException("No debit card balance value is specified"));

        String debitCardBalanceValueRegex = "-?[0-9]+(.[0-9]{1,2})?";
        return StringUtils.mapStringIfMatchesRegex(debitCardBalanceValue, debitCardBalanceValueRegex, BigDecimal::new)
                .orElseThrow(() -> new BadRequestException("Incorrect format of debit card balance option"));
    }

    private static int extractDiscountCardNumberAndRemoveFromArgs(List<String> argsList) {
        String discountCardPrefix = String.format("%s%s", DISCOUNT_CARD, SEPARATOR);
        List<String> discountCardArgs = StringUtils.findStringsWithPrefix(argsList, discountCardPrefix);

        if (discountCardArgs.isEmpty()) {
            return -1;
        }
        if (discountCardArgs.size() > 1) {
            throw new BadRequestException("More than one discount cards were specified");
        }

        String discountCardArg = discountCardArgs.getFirst();
        argsList.remove(discountCardArg);

        String discountCardValue = StringUtils.removePrefix(discountCardArg, discountCardPrefix)
                .orElseThrow(() -> new BadRequestException("No discount card value is specified"));

        String discountCardValueRegex = "[0-9]{4}";
        return StringUtils.mapStringIfMatchesRegex(discountCardValue, discountCardValueRegex, Integer::valueOf)
                .orElseThrow(() -> new BadRequestException("Incorrect format of discount card option"));
    }

    private static Map<Long, Integer> extractIdToQuantityAndRemoveFromArgs(List<String> args) {
        String idGroupName = "id";
        String quantityGroupName = "quantity";
        String separator = "-";
        String idToQuantityRegex = String.format(
                "(?<%s>[1-9]+[0-9]*)%s(?<%s>[1-9]+[0-9]*)",
                idGroupName,
                separator,
                quantityGroupName
        );

        Pattern pattern = Pattern.compile(idToQuantityRegex);

        return findIdToQuantityEntriesAndRemoveFromArgs(args, pattern, idGroupName, quantityGroupName);
    }

    private static Map<Long, Integer> findIdToQuantityEntriesAndRemoveFromArgs(List<String> args,
                                                                               Pattern pattern,
                                                                               String idGroupName,
                                                                               String quantityGroupName) {
        Map<Long, Integer> idToQuantityMap = new HashMap<>();

        final int size = args.size();
        for (int i = size - 1; i >= 0; i--) {

            String arg = args.get(i);

            Matcher matcher = pattern.matcher(arg);
            if (!matcher.matches()) {
                continue;
            }

            String foundId = matcher.group(idGroupName);
            final long id = Long.parseLong(foundId);

            String foundQuantity = matcher.group(quantityGroupName);
            final int quantity = Integer.parseInt(foundQuantity);

            idToQuantityMap.merge(id, quantity, Integer::sum);
            args.remove(i);
        }
        return idToQuantityMap;
    }
}
