package ru.skypro.stock_of_socks.constant;

public enum TextMessageExceptionEnum {
    NOT_FOUND_COLOR("this color is not in the database"),
    NOT_FOUND_SOCKS_BY_COTTON_PART("socks with this cotton part are not in the database"),
    NO_REQUIRED_QUANTITY_IN_STOCK("written off more than the balance in the stock");

    private final String message;

    TextMessageExceptionEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
