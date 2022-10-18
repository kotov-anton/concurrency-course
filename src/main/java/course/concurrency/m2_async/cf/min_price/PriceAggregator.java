package course.concurrency.m2_async.cf.min_price;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        try {
            ExecutorService executor = Executors.newCachedThreadPool();
            List<Callable<Double>> tasks = shopIds.stream().map((shopId) -> this.createTask(itemId, shopId)).collect(Collectors.toList());
            Optional<Double> min = executor.invokeAll(tasks, 2900, TimeUnit.MILLISECONDS)
                    .stream()
                    .filter(f -> !f.isCancelled())
                    .map(this::getValue)
                    .min(Double::compareTo);

            return min.orElse(Double.NaN);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Double getValue(Future<Double> f) {
        try {
            return f.get();
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    private Callable<Double> createTask(long itemId, long shopId) {
        return () -> priceRetriever.getPrice(itemId, shopId);
    }
}
