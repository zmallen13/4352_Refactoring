public final class RentalCoupon {

    private RentalCoupon() {
    }

    public static double applyOneDollarOffIfOverFive(double chargeBeforeCoupon) {
        if (chargeBeforeCoupon > 5.0) {
            return chargeBeforeCoupon - 1.0;
        }
        return chargeBeforeCoupon;
    }
}
