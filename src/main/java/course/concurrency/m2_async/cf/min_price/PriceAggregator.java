package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
//        ExecutorService executor = Executors.newCachedThreadPool();
        ExecutorService executor = Executors.newFixedThreadPool(shopIds.size());
        Optional<Double> min = shopIds.stream()
                .map(shopId -> executor.submit(() -> priceRetriever.getPrice(itemId, shopId)))
                .map(this::getValue)
                .min(Double::compareTo);

//        executor.shutdown();
        return min.get();
    }

    private Double getValue(Future<Double> f) {
        try {
            return f.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            return Double.NaN;
        }
    }
}
