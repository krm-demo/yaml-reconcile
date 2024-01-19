package org.krmdemo.yaml.reconcile.test.ansi;

import java.util.*;

public class AnsiRenderable {

    public enum Horizontal {
        LEFT,

        CENTER,

        RIGHT
    };

    public enum Vertical {
        TOP,
        CENTER,
        BOTTOM
    }



    private final List<String> renderedLines;

    private Horizontal horizontal;

    private Vertical vertical;

    public static class Builder {
        private final String content;
        private Horizontal horizontal;

        private Vertical vertical;

        private

        private Builder(String content) {
            this.content = content;
        }

        public
    }
}
