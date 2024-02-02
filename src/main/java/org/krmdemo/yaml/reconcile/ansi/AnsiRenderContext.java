package org.krmdemo.yaml.reconcile.ansi;

/**
 * A thread-local singleton that keeps the context of ANSI- or HTML- rendering.
 */
public class AnsiRenderContext {

    private final static ThreadLocal<AnsiRenderContext> threadLocal =
        ThreadLocal.withInitial(AnsiRenderContext::new);

    private boolean siblingStylesSquash = true;

    public static AnsiRenderContext renderCtx() {
        return threadLocal.get();
    }

    public void siblingStylesSquash(boolean optimizeSiblingSpans) {
        renderCtx().siblingStylesSquash = optimizeSiblingSpans;
    }

    public boolean siblingStylesSquash() {
        return threadLocal.get().siblingStylesSquash;
    }
}
