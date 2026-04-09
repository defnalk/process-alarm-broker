package com.defneertugrul.alarmer.suppression;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "alarm.suppression")
public class SuppressionConfig {
    private int windowSeconds = 30;
    private int maxGroupSize = 10;

    public int getWindowSeconds() { return windowSeconds; }
    public void setWindowSeconds(int windowSeconds) { this.windowSeconds = windowSeconds; }

    public int getMaxGroupSize() { return maxGroupSize; }
    public void setMaxGroupSize(int maxGroupSize) { this.maxGroupSize = maxGroupSize; }
}
