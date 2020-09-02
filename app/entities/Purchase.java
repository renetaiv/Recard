package entities;

import java.util.concurrent.atomic.AtomicInteger;

public class Purchase extends BaseEntity {

    private User buyer;

    private int credits;

    public Purchase(User buyer,int credits) {
        super();
        this.setCredits(credits);
        this.setBuyer(buyer);
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
