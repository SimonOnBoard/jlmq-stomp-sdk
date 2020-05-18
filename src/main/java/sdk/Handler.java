package sdk;

@FunctionalInterface
public interface Handler<T> {
    void handle(T message) throws Exception;
}
