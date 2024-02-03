package org.krmdemo.yaml.reconcile.util;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.format;

/**
 * Utility-class to work with java-streams.
 * <p/>
 * The same class, but in different package is present in 'rest-info' project.
 */
public class StreamUtils {

    /**
     * @param iter iterator over elements of type {@link T}
     * @return ordered stream of elements for {@link Iterator}
     * @param <T> type of elements
     */
    public static <T> Stream<T> stream(Iterator<T> iter) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED), false);
    }

    /**
     * @param enumeration {@link Enumeration} of type {@link T}
     * @return ordered stream of elements in {@link Enumeration}
     * @param <T> type of elements
     */
    public static <T> Stream<T> stream(Enumeration<T> enumeration) {
        return stream(enumeration.asIterator());
    }

    /**
     * The same as {@link Collectors#toMap}, but produces {@link TreeMap} and ignore duplicates.
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the key mapping function
     * @param <U> the output type of the value mapping function
     * @param keyMapper a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @return a {@code Collector} which collects elements into a {@link SortedMap}
     * whose keys are the result of applying a key mapping function to the input
     * elements, and whose values are the result of applying a value mapping
     * function to all input elements equal to the key and combining them
     * using the merge function
     */
    public static <T, K, U>
    Collector<T, ?, SortedMap<K,U>>
    toSortedMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
        return toSortedMap(keyMapper, valueMapper, StreamUtils::mergeIgnore);
    }

    /**
     * The same as {@link #toSortedMap(Function, Function)}, but allows to handle duplicates with merge-function.
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the key mapping function
     * @param <U> the output type of the value mapping function
     * @param keyMapper a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @param mergeFunction a merge function, used to resolve collisions between values
     *                      associated with the same key, as supplied to {@link Map#merge(Object, Object, BiFunction)}
     * @return a {@code Collector} which collects elements into a {@link SortedMap}
     * whose keys are the result of applying a key mapping function to the input
     * elements, and whose values are the result of applying a value mapping
     * function to all input elements equal to the key and combining them
     * using the merge function
     */
    public static <T, K, U>
    Collector<T, ?, SortedMap<K,U>>
    toSortedMap(Function<? super T, ? extends K> keyMapper,
                Function<? super T, ? extends U> valueMapper,
                BinaryOperator<U> mergeFunction) {
        return Collectors.toMap(keyMapper, valueMapper, mergeFunction, TreeMap::new);
    }

    /**
     * @param nameToObj a map of String to Object
     * @return a map of String to String (with information of type, if it's not String)
     */
    public static SortedMap<String, String> propsMap(Map<?,?> nameToObj) {
        return nameToObj.entrySet().stream().collect(
            toSortedMap(StreamUtils::propertyKey, StreamUtils::propertyValue));
    }

    public static String propertyKey(Map.Entry<?,?> entry) {
        return entry.getKey().toString();
    }

    public static String propertyValue(Map.Entry<?,?> entry) {
        if (entry.getValue() instanceof String str) {
//            if (str.length() > 75) {
//                str = "'" + str.substring(0, 75) + "'" + " ... " + (str.length() - 75) + " chars more";
//            }
            return str;
        }
        return format("%s(%s)", entry.getValue().getClass().getName(), entry.getValue());
    }

    /**
     * Merging function, that ignore duplications.
     *
     * @param x existing value
     * @param y new value
     * @return new value, implying that existing is always null
     * @param <V> type of elements
     */
    public static <V> V mergeIgnore(V x, V y) { return y; }

    /**
     * Prohibit the instantiation of utility-class
     */
    private StreamUtils() {
        throw new UnsupportedOperationException();
    }
}
