package org.krmdemo.yaml.reconcile.ansi;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

/**
 * Rectangular size of renderable content.
 */
public interface AnsiSize {

    /**
     * @return the height of rendered rectangular area (the number of lines)
     */
    int height();

    /**
     * @return the width of rendered rectangular area (in number of char-places)
     */
    int width();

    default boolean isNotEmpty() {
        return height() > 0 && width() > 0;
    }

    default boolean isEmpty() {
        return !isNotEmpty();
    }

    static <Sz extends AnsiSize> int max(ToIntFunction<Sz> sizePart, Sz... arr) {
        return max(sizePart, stream(arr));
    }

    static <Sz extends AnsiSize> int max(ToIntFunction<Sz> sizePart, List<Sz> list) {
        return max(sizePart, list.stream());
    }

    static <Sz extends AnsiSize> int max(ToIntFunction<Sz> sizePart, Stream<Sz> sizableStream) {
        return sizableStream.mapToInt(sizePart).max().orElse(0);
    }

    static <Sz extends AnsiSize> int sum(ToIntFunction<Sz> sizePart, Sz... arr) {
        return sum(sizePart, stream(arr));
    }

    static <Sz extends AnsiSize> int sum(ToIntFunction<Sz> sizePart, List<Sz> list) {
        return sum(sizePart, list.stream());
    }

    static <Sz extends AnsiSize> int sum(ToIntFunction<Sz> sizePart, Stream<Sz> sizableStream) {
        return sizableStream.mapToInt(sizePart).sum();
    }
}
