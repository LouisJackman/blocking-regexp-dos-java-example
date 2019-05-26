package com.volatilethunk.blockingregexpdosexample.timelimits;

import java.time.Duration;

public abstract class TimeLimit {

  private final Duration limit;

  public abstract TimeLimitBound getBound();

  TimeLimit(Duration limit) {
    this.limit = limit;
  }

  public Duration getLimit() {
    return limit;
  }
}
