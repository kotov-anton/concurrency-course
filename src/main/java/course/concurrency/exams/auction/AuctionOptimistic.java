package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private AtomicReference<Bid> latestBidRef = new AtomicReference<>();

    public boolean propose(Bid bid) {
        boolean successful = false;
        while (!successful) {
            final Bid latestBit = latestBidRef.get();
            if (latestBit == null || bid.getPrice() > latestBit.getPrice()) {
                successful = latestBidRef.compareAndSet(latestBit, bid);
                if (successful) {
                    notifier.sendOutdatedMessage(latestBit);
                }
            } else {
                break;
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBidRef.get();
    }
}
