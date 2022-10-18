package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class PriceAggregatorExtra {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Callable<Double>> tasks = shopIds.stream().map((shopId) -> this.createTask(itemId, shopId)).collect(Collectors.toList());
        executor.invokeAll(tasks, 2900, TimeUnit.MILLISECONDS).stream()
                .peek(System.out::println);
        executor.shutdown();
        return Double.NaN;
    }

    private Callable<Double> createTask(long itemId, long shopId) {
        return () -> priceRetriever.getPrice(itemId, shopId);
    }
}
