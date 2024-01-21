package org.krmdemo.yaml.reconcile.test;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.fail;


@Slf4j
@ExtendWith(InvocationInfoExtension.class)
public class InvocationInfoTest {

    @Nested
    class NestedTest {
        @Test
        void testNested() {
            log.debug("!!! this is nested !!!");
        }
    }

    @BeforeAll
    static void beforeAll() {
        log.debug("====== before-all ======");
    }

    @BeforeEach
    void beforeEach() {
        log.debug("----- before-each -----");
    }

    @Test
    void testOne() {
        log.debug("!!! this is test-one !!!");
    }

    @Test
    void testTwo() {
        log.debug("!!! this is test-two !!!");
        if (log.isTraceEnabled()) {
            fail("oops !!!");
        }
    }

    @AfterEach
    void afterEach() {
        log.debug("----- after-each ------");
    }

    @AfterAll
    static void afterAll() {
        log.debug("====== after-all =======");

    }
}
