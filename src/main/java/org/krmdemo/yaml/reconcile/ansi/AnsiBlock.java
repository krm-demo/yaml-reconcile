package org.krmdemo.yaml.reconcile.ansi;


import java.util.stream.Stream;

/**
 * This class represents a renderable rectangular block of screen. In enriches the wrapped {@link AnsiText}
 * with horizontal alignment and padding with left and right indentations.
 */
public class AnsiBlock {

    public interface Indentable {
        AnsiText leftIndent(int lineNum);
    }

    /**
     * @param line the source line to extract the sub-line from
     * @param contentPosFrom the start-position of in source line content (inclusive)
     * @param contentPosTo the stop-position of in source line  content (inclusive)
     * @return a sub-line with content of source line in the range <code>[contentPosFrom;contentPosTo)</code>.
     */
    public static Stream<AnsiSpan> subLine(AnsiText.Line line, int contentPosFrom, int contentPosTo) {
        return Stream.empty();
    }
}
