package com.volatilethunk.blockingregexpdosexample.timelimits;

import java.time.Duration;

public final class MaxTimeLimit extends TimeLimit {

  public MaxTimeLimit(Duration limit) {
    super(limit);
  }

  public TimeLimitBound getBound() {
    return TimeLimitBound.MAX;
  }
}
