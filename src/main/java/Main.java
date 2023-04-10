import database.ReactiveMongoDriver;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import model.Currency;
import model.Item;
import model.User;
import rx.Observable;

import java.util.List;
import java.util.Map;

public class Main {
    private static ReactiveMongoDriver reactiveMongoDriver;

    public static void main(String[] args) {
        reactiveMongoDriver = new ReactiveMongoDriver();
        HttpServer
                .newServer(8080)
                .start(getRequestHandler())
                .awaitShutdown();
    }

    private static RequestHandler<ByteBuf, ByteBuf> getRequestHandler() {
        return (req, resp) -> {

            String name = req.getDecodedPath();
            Map<String, List<String>> queryParameters = req.getQueryParameters();
            Observable<String> resultString = switch (name) {
                case "/createUser" -> createUser(queryParameters);
                case "/createItem" -> createItem(queryParameters);
                case "/getItems" -> getItems(queryParameters);
                default -> Observable.just("Bad request, use: createUser, createItem or getItem");
            };
            return resp.writeString(resultString);
        };
    }

    private static Observable<String> getItems(Map<String, List<String>> queryParameters) {
        if (isBadParameterList(List.of("userId"), queryParameters)) {
            return Observable.just("Bad request");
        }
        return reactiveMongoDriver.getUser(Long.parseLong(queryParameters.get("userId").get(0)))
                .switchMap(Main::filterCurrency)
                .map(item -> item.name() + ", " + item.price() + ", " + item.currency() + System.lineSeparator());
    }

    private static Observable<Item> filterCurrency(User user) {
        return reactiveMongoDriver.getItems().filter(item -> item.currency().equals(user.currency()));
    }


    private static Observable<String> createItem(Map<String, List<String>> queryParameters) {
        if (isBadParameterList(List.of("name", "price", "currency"), queryParameters)) {
            return Observable.just("Bad request");
        }
        Item item = new Item(queryParameters.get("name").get(0), Long.parseLong(queryParameters.get("price").get(0)), Currency.valueOf(queryParameters.get("currency").get(0)));
        return reactiveMongoDriver.addItem(item).map(a -> "Success!");
    }

    private static Observable<String> createUser(Map<String, List<String>> queryParameters) {
        if (isBadParameterList(List.of("name", "id", "currency"), queryParameters)) {
            return Observable.just("Bad request");
        }
        User user = new User(queryParameters.get("name").get(0), Long.parseLong(queryParameters.get("id").get(0)), Currency.valueOf(queryParameters.get("currency").get(0)));
        return reactiveMongoDriver.createUser(user).map(Enum::toString);
    }

    private static boolean isBadParameterList(List<String> requiredParameters, Map<String, List<String>> queryParameters) {
        for (String parameter : requiredParameters) {
            if (!queryParameters.containsKey(parameter) || queryParameters.get(parameter).size() != 1) {
                return true;
            }
        }
        return false;
    }
}