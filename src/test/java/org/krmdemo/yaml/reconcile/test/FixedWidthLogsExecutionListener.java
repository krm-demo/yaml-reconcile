package org.krmdemo.yaml.reconcile.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.slf4j.Marker;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.System.identityHashCode;
import static java.lang.System.lineSeparator;

@Slf4j
public class FixedWidthLogsExecutionListener implements TestExecutionListener {

    private static final FixedWidthLogsExecutionListener instance = new FixedWidthLogsExecutionListener();

    public static TestExecutionListener provider() {
        log.trace(format("request for an instance - " + instance));
        return instance;
    }

    private FixedWidthLogsExecutionListener() {
        log.trace(format("creating an instance of %s(%x)", getClass().getSimpleName(), identityHashCode(this)));
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        log.trace("test started: " + testIdentifier);
        if (testIdentifier.getParentId().isEmpty()) {
            log.trace("begin of all test suites - " + testIdentifier.getUniqueId());
        }
    }

    @Override
    public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
        log.trace(format("reportingEntry(%,d) ----> %s", epochSecond(entry), entry));
    }

    private long epochSecond(ReportEntry entry) {
        return entry.getTimestamp().toEpochSecond(ZoneOffset.UTC);
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        log.trace("test finished: " + testIdentifier);
        if (testIdentifier.getParentId().isEmpty()) {
            log.trace("end of all test suites - " + testIdentifier.getUniqueId());
            log.atTrace()
                .addKeyValue("key-1", 1)
                .addKeyValue("key-2", 2)
                .addKeyValue("key-3", 3)
                .setMessage("Some mesage from {}")
                .addArgument(testIdentifier.getDisplayName())
                .log();
        }
    }
}
