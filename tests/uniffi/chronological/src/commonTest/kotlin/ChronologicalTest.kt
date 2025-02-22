/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import chronological.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

// Windows has a system clock resolution of 100 nanoseconds.
expect val isWindows: Boolean

class ChronologicalTest {
    // Test passing timestamp and duration while returning timestamp
    @Test
    fun testAdd() {
        if (isWindows) {
            add(
                Instant.fromEpochSeconds(100, 10000),
                1.seconds + 100.nanoseconds,
            ) shouldBe Instant.fromEpochSeconds(101, 10100)
        } else {
            add(
                Instant.fromEpochSeconds(100, 100),
                1.seconds + 1.nanoseconds,
            ) shouldBe Instant.fromEpochSeconds(101, 101)
        }
    }

    // Test passing timestamp while returning duration
    @Test
    fun testDiff() {
        if (isWindows) {
            diff(
                Instant.fromEpochSeconds(101, 10100),
                Instant.fromEpochSeconds(100, 10000),
            ) shouldBe (1.seconds + 100.nanoseconds)
        } else {
            diff(
                Instant.fromEpochSeconds(101, 101),
                Instant.fromEpochSeconds(100, 100),
            ) shouldBe (1.seconds + 1.nanoseconds)
        }
    }

    // Test pre-epoch timestamps
    @Test
    fun testPreEpochTimestamps() {
        if (isWindows) {
            add(
                Instant.parse("1955-11-05T00:06:00.283000100Z"),
                1.seconds + 100.nanoseconds,
            ) shouldBe Instant.parse("1955-11-05T00:06:01.283000200Z")
        } else {
            add(
                Instant.parse("1955-11-05T00:06:00.283000001Z"),
                1.seconds + 1.nanoseconds,
            ) shouldBe Instant.parse("1955-11-05T00:06:01.283000002Z")
        }
    }

    // Test exceptions are propagated
    @Test
    fun testChronologicalException() {
        if (isWindows) {
            shouldThrow<ChronologicalException> {
                diff(Instant.fromEpochSeconds(10000), Instant.fromEpochSeconds(10100))
            }
        } else {
            shouldThrow<ChronologicalException> {
                diff(Instant.fromEpochSeconds(100), Instant.fromEpochSeconds(101))
            }
        }
    }

    // Test max Instant upper bound
    @Test
    fun testInstantUpperBound() {
        add(Instant.MAX, Duration.ZERO) shouldBe Instant.MAX

        if (isWindows) {
            // Rust uses an allowed value range for SystemTime smaller than that of Instant on Windows.
            // Check for the overflow exception.
            shouldThrow<ChronologicalException.TimeOverflow> {
                add(Instant.MAX, 1.seconds)
            }
        } else {
            // Test Instant is clamped to the upper bound, and don't check for the exception as in upstream.
            // While Java's Instant.plus throws DateTimeException for overflow, kotlinx-datetime Instant just coerces the
            // value to the upper bound.
            add(Instant.MAX, 1.seconds) shouldBe Instant.MAX
        }

        // Upstream checks for Java's exception
        // try {
        //     add(Instant.MAX, 1.seconds)
        //     throw RuntimeException("Should have thrown a DateTimeException exception!")
        // } catch (e: DateTimeException) {
        //     // It's okay!
        // }
    }

    // Test that rust timestamps behave like kotlin timestamps
    @Test
    fun test() {
        // The underlying implementation of `Clock.System` may be lower resolution than the Rust clock.
        // Sleep for 10ms between each call, which should ensure `Clock.System` ticks forward.
        runBlocking {
            val kotlinBefore = Clock.System.now()
            delay(10)
            val rustNow = now()
            delay(10)
            val kotlinAfter = Clock.System.now()
            kotlinBefore shouldBeLessThan rustNow
            kotlinAfter shouldBeGreaterThan rustNow
        }
    }

    // Test optional values work
    @Test
    fun testOptionalValues() {
        optional(Instant.MAX, Duration.ZERO) shouldBe true
        optional(null, Duration.ZERO) shouldBe false
        optional(Instant.MAX, null) shouldBe false
    }
}


// This is to mock java.time.Instant.MAX, which does not exist as a public API in kotlinx-datetime.
private val Instant.Companion.MAX: Instant
    get() = if (isWindows) {
        // Rust's SystemTime uses Windows API's FILETIME on Windows, which is a 8-byte structure holding
        // a 100-nanosecond tick value since January 1, 1601 (UTC) and has the maximum value of 2^63 - 1.
        // Therefore, the maximum allowed value of Instant by Rust's SystemTime on Windows is:
        //
        // SECONDS = (Long.MAX_VALUE - UNIX_EPOCH) / 10_000_000 = 910692730084
        // NANOSECONDS = (Long.MAX_VALUE - UNIX_EPOCH) % 10_000_000 * 100 = 477_580_700
        //
        // where UNIX_EPOCH = 116444736000000000, which is 369 = 1970 - 1601 years.
        val unixEpoch = 116444736000000000
        val seconds = (Long.MAX_VALUE - unixEpoch) / 10_000_000
        val nanoseconds = (Long.MAX_VALUE - unixEpoch) % 10_000_000 * 100
        fromEpochSeconds(seconds, nanoseconds)
    } else {
        // Since `Instant.fromEpochSeconds` clamps the given value to the platform-specific boundaries,
        // passing `Long.MAX_VALUE` is okay to get the maximum value.
        fromEpochSeconds(Long.MAX_VALUE, 999_999_999)
    }