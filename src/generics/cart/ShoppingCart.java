package generics.cart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShoppingCart<T extends CartItem> {

    private final Map<String, Double> couponToDiscount = Map.of(
            "Sale5", 5.0,
            "Sale8", 8.0,
            "Sale10", 10.0);

    private final List<CartEntry> contents = new ArrayList<>();
    private final List<Double> discounts = new ArrayList<>();
    private Double couponDiscount = 0.0;

    public void add(T item) {
        for (CartEntry entry : contents) {
            if (entry.item.id().equals(item.id())) {
                entry.quantity++;
                return;
            }
        }

        contents.add(new CartEntry(item, 1));
    }

    public void removeById(String id) {
        contents.removeIf(entry -> entry.item.id().equals(id));
    }

    public Double getTotal() {
        double subtotal = 0.0;

        for (CartEntry entry : contents) {
            subtotal += entry.item.price() * entry.quantity;
        }

        return subtotal * remainingMultiplier();
    }

    public List<CartEntry> getContents() {
        return contents;
    }

    public void increaseQuantity(String id) {
        for (CartEntry entry : contents) {
            if (entry.item.id().equals(id)) {
                entry.quantity++;
                return;
            }
        }
    }

    public void applyDiscountPercentage(Double discount) {
        discounts.add(discount);
    }

    public boolean applyCoupon(String coupon) {
        Double discount = couponToDiscount.get(coupon);

        if (discount == null) {
            return false;
        }

        couponDiscount = discount;
        return true;
    }

    public Double getTotalDiscount() {
        return 100.0 * (1.0 - remainingMultiplier());
    }

    public void removeLastDiscount() {
        if (!discounts.isEmpty()) {
            discounts.remove(discounts.size() - 1);
        }
    }

    public void addAll(List<T> items) {
        for (T item : items) {
            add(item);
        }
    }

    @Override
    public String toString() {
        return contents.stream()
                .map(CartEntry::toString)
                .collect(Collectors.joining(", "));
    }

    private double remainingMultiplier() {
        double multiplier = 1.0;

        for (Double discount : discounts) {
            multiplier *= (1.0 - discount / 100.0);
        }

        multiplier *= (1.0 - couponDiscount / 100.0);

        return multiplier;
    }
}