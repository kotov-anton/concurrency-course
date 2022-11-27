package course.concurrency.m3_shared.collections;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RestaurantService {

    private Map<String, Restaurant> restaurantMap = new ConcurrentHashMap<>() {{
        put("A", new Restaurant("A"));
        put("B", new Restaurant("B"));
        put("C", new Restaurant("C"));
    }};

    private Map<String, AtomicInteger> stat = new ConcurrentHashMap<>();

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return restaurantMap.get(restaurantName);
    }

    public void addToStat(String restaurantName) {
        final AtomicInteger counter = stat.putIfAbsent(restaurantName, new AtomicInteger(1));
        if (counter != null) {
            counter.incrementAndGet();
        }
    }

    public Set<String> printStat() {
        return stat.entrySet().stream()
                .map(e -> e.getKey() + " - " + e.getValue().get())
                .collect(Collectors.toSet());
    }

}
