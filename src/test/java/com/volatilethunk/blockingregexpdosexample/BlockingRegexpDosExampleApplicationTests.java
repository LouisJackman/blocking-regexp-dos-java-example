package com.volatilethunk.blockingregexpdosexample;

import static junit.framework.TestCase.assertTrue;

import com.volatilethunk.blockingregexpdosexample.timelimits.MaxTimeLimit;
import com.volatilethunk.blockingregexpdosexample.timelimits.MinTimeLimit;
import com.volatilethunk.blockingregexpdosexample.timelimits.TimeLimit;
import java.time.Duration;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.StopWatch;

/**
 * These tests are, by their very nature, very hardware specific. Skynet circa 2029 probably will be
 * able to complete the exponential regexp in under 25 seconds, for example.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BlockingRegexpDosExampleApplication.class)
@AutoConfigureWebTestClient
public class BlockingRegexpDosExampleApplicationTests {

  // This might need tweaking as hardware improves.
  private static final int NEFARIOUS_INPUT_REPETITIONS = 42;

  private static final String NEFARIOUS_INPUT = "a".repeat(NEFARIOUS_INPUT_REPETITIONS) + '!';

  // First, the innocent regular expressions.

  private static final String SMALL_LINEAR_REGEXP = "[a-z]*";

  private static final String LARGE_LINEAR_REGEXP = "[a-z](?:[A-Z]|[0-9])*";

  // Now for the not-so-innocent one.

  /**
   * The classic ReDos example is simpler than this, but modern Java versions actually do a good job
   * at getting though power-2 complexity in regexps these days, so to demonstrate the problem
   * power-3 or greater is used.
   *
   * <p>Since power-3 or worse regexps should be quite rare, this vulnerability is therefore most
   * prevalent in services that allow user-definable regexps for, say, custom field validation or
   * the like.
   */
  private static final String EVIL_REGEXP = "^((([a-z])+.)+([A-Z]+)([a-z])+)+$";

  @Inject private WebTestClient webTestClient;

  private void testRegexp(String regexp, TimeLimit limit) {
    var stopWatch = new StopWatch();
    stopWatch.start();

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/")
                    .queryParam("regexp", regexp)
                    .queryParam("name", NEFARIOUS_INPUT)
                    .build())
        .exchange()
        .expectStatus()
        .value(
            ignored -> {
              stopWatch.stop();

              var timeTaken = Duration.ofMillis(stopWatch.getTotalTimeMillis());
              var comparison = timeTaken.compareTo(limit.getLimit());

              final boolean withinBound;
              switch (limit.getBound()) {
                case MIN:
                  withinBound = 0 <= comparison;
                  break;
                case MAX:
                  withinBound = comparison <= 0;
                  break;
                default:
                  throw new UnsupportedOperationException();
              }
              assertTrue(withinBound);
            });
  }

  @Test
  public void smallRegexpDoesNotBlock() {
    testRegexp(SMALL_LINEAR_REGEXP, new MaxTimeLimit(Duration.ofSeconds(1)));
  }

  @Test
  public void largeLinearRegexpDoesNotBlock() {
    testRegexp(LARGE_LINEAR_REGEXP, new MaxTimeLimit(Duration.ofSeconds(1)));
  }

  @Test
  public void largeExponentialRegexpDoesBlock() {
    testRegexp(EVIL_REGEXP, new MinTimeLimit(Duration.ofSeconds(20)));
  }
}
