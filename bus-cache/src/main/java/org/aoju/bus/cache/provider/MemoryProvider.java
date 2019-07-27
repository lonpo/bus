package org.aoju.bus.cache.provider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author aoju.org
 * @version 3.0.1
 * @group 839128
 * @since JDK 1.8
 */
public class MemoryProvider implements BaseProvider {

    private ConcurrentMap<String, AtomicLong> hitMap = new ConcurrentHashMap<>();

    private ConcurrentMap<String, AtomicLong> requireMap = new ConcurrentHashMap<>();

    @Override
    public void hitIncr(String pattern, int count) {
        hitMap.computeIfAbsent(
                pattern,
                (k) -> new AtomicLong()
        ).addAndGet(count);
    }

    @Override
    public void reqIncr(String pattern, int count) {
        requireMap.computeIfAbsent(
                pattern,
                (k) -> new AtomicLong()
        ).addAndGet(count);
    }

    @Override
    public Map<String, BaseProvider.ShootingDO> getShooting() {
        Map<String, ShootingDO> result = new LinkedHashMap<>();

        AtomicLong statisticsHit = new AtomicLong(0);
        AtomicLong statisticsRequired = new AtomicLong(0);
        requireMap.forEach((pattern, count) -> {
            long hit = hitMap.computeIfAbsent(pattern, (key) -> new AtomicLong(0)).get();
            long require = count.get();

            statisticsHit.addAndGet(hit);
            statisticsRequired.addAndGet(require);

            result.put(pattern, ShootingDO.newInstance(hit, require));
        });

        result.put(summaryName(), ShootingDO.newInstance(statisticsHit.get(), statisticsRequired.get()));

        return result;
    }

    @Override
    public void reset(String pattern) {
        hitMap.remove(pattern);
        requireMap.remove(pattern);
    }

    @Override
    public void resetAll() {
        hitMap.clear();
        requireMap.clear();
    }
}