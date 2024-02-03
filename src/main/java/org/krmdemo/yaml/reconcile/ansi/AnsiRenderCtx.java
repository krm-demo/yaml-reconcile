package org.krmdemo.yaml.reconcile.ansi;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import lombok.experimental.WithBy;

import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.emptyBuilder;

/**
 * A thread-local singleton that keeps the context of ANSI- or HTML- rendering.
 */
@Getter @Setter @ToString
public class AnsiRenderCtx {

    private final static ThreadLocal<AnsiRenderCtx> threadLocal =
        ThreadLocal.withInitial(AnsiRenderCtx::new);

    public static AnsiRenderCtx renderCtx() {
        return threadLocal.get();
    }

    private boolean siblingStylesSquash = true;
    private boolean linePrefixResetAll = false;
    private boolean lineSuffixResetAll = false;

    private AnsiStyle.Builder lineStyleBuilder = emptyBuilder();
}
