package org.krmdemo.yaml.reconcile.ansi;

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
}
