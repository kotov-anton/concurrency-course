package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private AtomicReference<Bid> latestBidRef = new AtomicReference<>(new Bid(-1l, -1l, -1l));

    public boolean propose(Bid bid) {
        Bid latestBid;
        do {
            latestBid = latestBidRef.get();
            if (bid.getPrice() <= latestBid.getPrice()) {
                return false;
            }
        } while (latestBidRef.compareAndSet(latestBid, bid));

        notifier.sendOutdatedMessage(latestBid);
        return true;
    }

    public Bid getLatestBid() {
        return latestBidRef.get();
    }
}
