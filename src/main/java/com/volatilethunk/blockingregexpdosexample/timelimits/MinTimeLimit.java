package com.volatilethunk.blockingregexpdosexample.timelimits;

import java.time.Duration;

public final class MinTimeLimit extends TimeLimit {

  public MinTimeLimit(Duration limit) {
    super(limit);
  }

  public TimeLimitBound getBound() {
    return TimeLimitBound.MIN;
  }
}
