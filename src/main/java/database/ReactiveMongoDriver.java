package database;

import com.mongodb.client.model.Filters;
import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.Success;
import model.Item;
import model.User;
import org.bson.Document;
import rx.Observable;

/**
 * @author akirakozov
 */
public class ReactiveMongoDriver {

    private static final MongoClient client = createMongoClient();

    private static final String DATABASE_NAME = "test_db";
    private static final String USER_COLLECTION = "user";
    private static final String ITEM_COLLECTION = "item";

    public Observable<Success> createUser(User user) {
        return client.getDatabase(DATABASE_NAME).getCollection(USER_COLLECTION).insertOne(MongoConverter.toDocument(user));
    }

    public Observable<User> getUser(Long id) {
        Observable<Document> document = client.getDatabase(DATABASE_NAME).getCollection(USER_COLLECTION).find(Filters.eq(MongoConverter.USER_ID_FIELD, id)).first();
        return document.map(MongoConverter::fromDocumentUser);
    }

    public Observable<User> getUsers() {
        Observable<Document> documents = client.getDatabase(DATABASE_NAME).getCollection(USER_COLLECTION).find().toObservable();
        return documents.map(MongoConverter::fromDocumentUser);
    }

    public Observable<Success> addItem(Item item) {
        return client.getDatabase(DATABASE_NAME).getCollection(ITEM_COLLECTION).insertOne(MongoConverter.toDocument(item));
    }

    public Observable<Item> getItems() {
        Observable<Document> documents = client.getDatabase(DATABASE_NAME).getCollection(ITEM_COLLECTION).find().toObservable();
        return documents.map(MongoConverter::fromDocumentItem);
    }

    private static MongoClient createMongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }
}