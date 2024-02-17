package org.krmdemo.yaml.reconcile.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.lang.reflect.Method;
import java.util.*;

import static java.lang.String.format;
import static java.lang.System.identityHashCode;

@Slf4j
public class InvocationInfoExtension implements InvocationInterceptor,
    BeforeAllCallback, BeforeEachCallback, TestWatcher {

    private String ext(ExtensionContext extCtx) {
        return format("%s - %s", identityHashCode(extCtx), extCtx.getDisplayName());
    }

    private final List<ReflectiveInvocationContext<Method>> invocationContextStack = new LinkedList<>();
    private ExtensionContext extensionContext;

    @Override
    public void interceptBeforeAllMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extCtx) throws Throwable {
        log.debug(format("ext(%s) stack[%d]++ intercept-before-all: begin", ext(extCtx), invocationContextStack.size()));
        this.invocationContextStack.addLast(invocationContext);

        InvocationInterceptor.super.interceptBeforeAllMethod(invocation, invocationContext, extensionContext);

        log.debug(format("ext(%s) stack[%d]-- intercept-before-all: end", ext(extCtx), invocationContextStack.size()));
        this.invocationContextStack.removeLast();
    }

    @Override
    public void beforeAll(ExtensionContext extCtx) throws Exception {
        log.debug(format("ext(%s) stack[%d] .. before-all ..", ext(extCtx), invocationContextStack.size()));
        log.debug(format(".. before-all .. - lifcycle[%s], instance[%s], class[%s], method[%s]",
            extCtx.getTestInstanceLifecycle(), extCtx.getTestInstance(),
            extCtx.getTestClass(), extCtx.getTestMethod()));
    }

    @Override
    public void interceptBeforeEachMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extCtx) throws Throwable {
        log.debug(format("ext(%s) stack[%d]++ intercept-before-each: begin", ext(extCtx), invocationContextStack.size()));
        this.invocationContextStack.addLast(invocationContext);

        InvocationInterceptor.super.interceptBeforeEachMethod(invocation, invocationContext, extensionContext);

        log.debug(format("ext(%s) stack[%d]-- intercept-before-each: end", ext(extCtx), invocationContextStack.size()));
        this.invocationContextStack.removeLast();
    }

    @Override
    public void beforeEach(ExtensionContext extCtx) throws Exception {
        log.debug(format("ext(%s) stack[%d] .. before-each ..", ext(extCtx), invocationContextStack.size()));
        log.debug(format(".. before-each .. - lifecycle[%s], instance[%s], class[%s], method[%s]",
            extCtx.getTestInstanceLifecycle(), extCtx.getTestInstance(),
            extCtx.getTestClass(), extCtx.getTestMethod()));
    }

    @Override
    public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extCtx) throws Throwable {
        log.debug(format("ext(%s) stack[%d]++ intercept-test-method: begin", ext(extCtx), invocationContextStack.size()));
        this.invocationContextStack.addLast(invocationContext);

        extCtx.publishReportEntry("**************** la-la-la ******************");

        InvocationInterceptor.super.interceptTestMethod(invocation, invocationContext, extensionContext);

        log.debug(format("ext(%s) stack[%d]-- intercept-test-method: end", ext(extCtx), invocationContextStack.size()));
        this.invocationContextStack.removeLast();
    }

    @Override
    public void testDisabled(ExtensionContext extCtx, Optional<String> reason) {
        log.warn(format("ext(%s) stack[%d]++ testDisabled(%s)", ext(extCtx), invocationContextStack.size(), reason));
    }

    @Override
    public void testSuccessful(ExtensionContext extCtx) {
        log.info(format("ext(%s) stack[%d]++ testSuccessful", ext(extCtx), invocationContextStack.size()));
    }

    @Override
    public void testAborted(ExtensionContext extCtx, Throwable cause) {
        log.error(format("ext(%s) stack[%d]++ testAborted", ext(extCtx), invocationContextStack.size()), cause);
    }

    @Override
    public void testFailed(ExtensionContext extCtx, Throwable cause) {
        log.error(format("ext(%s) stack[%d]++ testFailed", ext(extCtx), invocationContextStack.size()), cause);
    }

    @Override
    public void interceptAfterEachMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extCtx) throws Throwable {
        log.debug(format("ext(%s) stack[%d]++ intercept-after-each: begin", ext(extCtx), invocationContextStack.size()));
        this.invocationContextStack.addLast(invocationContext);

        InvocationInterceptor.super.interceptAfterEachMethod(invocation, invocationContext, extensionContext);

        log.debug(format("ext(%s) stack[%d]-- intercept-after-each: end", ext(extCtx), invocationContextStack.size()));
        this.invocationContextStack.removeLast();
    }

    @Override
    public void interceptAfterAllMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extCtx) throws Throwable {
        log.debug(format("ext(%s) stack[%d]++ intercept-after-all: begin", ext(extCtx), invocationContextStack.size()));
        this.invocationContextStack.addLast(invocationContext);

        InvocationInterceptor.super.interceptAfterAllMethod(invocation, invocationContext, extensionContext);

        log.debug(format("ext(%s) stack[%d]-- intercept-after-all: end", ext(extCtx), invocationContextStack.size()));
        this.invocationContextStack.removeLast();
    }
}
