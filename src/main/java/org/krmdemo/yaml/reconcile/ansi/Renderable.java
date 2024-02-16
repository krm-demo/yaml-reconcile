package org.krmdemo.yaml.reconcile.ansi;

import lombok.NonNull;

import static java.lang.String.format;

public interface Renderable {

    enum Type {

        CONTENT,

        ANSI_ESC_SEQ,

        ANSI_FORMAT,

        XML,

        HTML,

        SVG,

        YAML,

        JSON,

        DEBUG
    }

    /**
     * @return rendered content without any ansi-styles, but with the same layout
     */
    String content();

    /**
     * @return rendered content, which is properly decorated with ansi-sequences
     */
    default String renderAnsi() {
        throw new UnsupportedOperationException(
            "ansi-rendering is not supported for class " + getClass().getSimpleName()
        );
    }

    default String renderAnsiFormat() {
        throw new UnsupportedOperationException(
            "ansi-rendering is not supported for class " + getClass().getSimpleName()
        );
    }


    default String dump() {
        throw new UnsupportedOperationException(
            "no debug information to dump for class " + getClass().getSimpleName()
        );
    }

    default String render(@NonNull Type renderType) {
        return switch(renderType) {
            case CONTENT -> content();
            case ANSI_ESC_SEQ -> renderAnsi();
            case ANSI_FORMAT -> renderAnsiFormat();
            case DEBUG -> dump();
            default -> throw new UnsupportedOperationException(
                format("no rendering of type %s for class %s", renderType, getClass().getSimpleName()));
        };
    }
}
