package org.krmdemo.yaml.reconcile.test.echo;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Stream.of;

public class EchoFmt implements Function<String[], String> {

    public static void main(String... args) throws IllegalArgumentException {
        if (args.length < 1) {
            throw new IllegalArgumentException(
                "format-ARG with the rest of ARGs as parameters to 'String.formatted(...)' are expected");
        }
        EchoFmt echoFmt = new EchoFmt();
        System.out.println("--- before apply ---");
        System.out.println(echoFmt.apply(args));
        System.out.println("--- after apply ----");
    }

    @Override
    public String apply(String... args) {
        return args[0].formatted(Arrays.stream(args).map(this::obj).toArray());
    }

    private Object obj(String str) {
        return streamFunc()
            .flatMap(f -> tryObj(f, str).stream())
            .findFirst().orElse(str);
    }

    private Optional<Object> tryObj(Function<String,Object> valueOf, String str) {
        try {
            return Optional.of(valueOf.apply(str));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private Stream<Function<String,Object>> streamFunc() {
        return of(
            this::tryInt,
            this::tryBoolean,
            this::tryDouble,
            this::trySelf
        );
    }

    private Integer tryInt(String str) {
        return Integer.valueOf(str);
    }

    private Double tryDouble(String str) {
        return Double.valueOf(str);
    }

    private Boolean tryBoolean(String str) {
        return Boolean.valueOf(str);
    }

    private String trySelf(String str) {
        return "%s".formatted(str);
    }
}
