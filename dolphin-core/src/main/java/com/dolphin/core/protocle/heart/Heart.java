package com.dolphin.core.protocle.heart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Heart implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Heart.class);

    private HeartBeatable heartBeatable;

    private volatile boolean isHealth = true;

    private volatile long healthCount = 0;

    private volatile long unHealthCount = 0;

    /**
     * 心跳失败次数限制 @author keyhunter 2016年5月15日 下午2:35:22
     */
    private int unHealthLimit;

    /**
     * 心跳成功次数限制 @author keyhunter 2016年5月15日 下午2:58:06
     */
    private final int healthLimit;

    public Heart(HeartBeatable heartBeatable, int healthLimit, int unHealthLimit) {
        super();
        this.heartBeatable = heartBeatable;
        this.healthLimit = healthLimit;
        this.unHealthLimit = unHealthLimit;
    }

    public boolean isHealth() {
        if (unHealthCount < unHealthLimit) {
            return true;
        }
        return false;
    }

    @Override
    public void run() {

        synchronized (this) {
            while (true) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Heart beat checking , now health count : " + healthCount
                            + " , unHealth count : " + unHealthCount);
                }
                logger.info("Heart beat checking , now health count : " + healthCount
                        + " , unHealth count : " + unHealthCount);

                if (heartBeatable.beat()) {
                    healthCount++;
                    unHealthCount = 0;

                    if (isHealth == false && healthCount >= healthLimit) {
                        isHealth = true;
                    }
                } else {
                    unHealthCount++;
                    healthCount = 0;

                    if (isHealth == true && unHealthCount >= unHealthLimit) {
                        isHealth = false;
                    }
                }
                try {
                    wait(heartBeatable.getBeatInterval());

                } catch (InterruptedException e) {
                    logger.error("", e);
                }

            }
        }
    }

}
