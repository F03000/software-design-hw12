package database;

import model.Currency;
import model.Item;
import model.User;
import org.bson.Document;

public class MongoConverter {
    protected static final String USER_ID_FIELD = "user_id";
    private static final String USER_NAME_FIELD = "user_name";
    private static final String USER_CURRENCY_FIELD = "user_currency";
    private static final String ITEM_NAME_FIELD = "item_name";
    private static final String ITEM_PRICE_FIELD = "item_price";
    private static final String ITEM_CURRENCY_FIELD = "item_currency";

    public static Document toDocument(User user) {
        return new Document(USER_ID_FIELD, user.id()).append(USER_NAME_FIELD, user.name()).append(USER_CURRENCY_FIELD, user.currency().toString());
    }

    public static User fromDocumentUser(Document document) {
        String name = document.getString(USER_NAME_FIELD);
        Long id = document.getLong(USER_ID_FIELD);
        Currency currency = Currency.valueOf(document.getString(USER_CURRENCY_FIELD));
        return new User(name, id, currency);
    }

    public static Document toDocument(Item item) {
        return new Document(ITEM_NAME_FIELD, item.name()).append(ITEM_PRICE_FIELD, item.price()).append(ITEM_CURRENCY_FIELD, item.currency().toString());
    }

    public static Item fromDocumentItem(Document document) {
        String name = document.getString(ITEM_NAME_FIELD);
        Long price = document.getLong(ITEM_PRICE_FIELD);
        Currency currency = Currency.valueOf(document.getString(ITEM_CURRENCY_FIELD));
        return new Item(name, price, currency);
    }
}
