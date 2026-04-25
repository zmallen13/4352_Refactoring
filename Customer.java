import java.util.ArrayList;
import java.util.List;

final class RentalDiscountUtil {
    private RentalDiscountUtil() {
    }

    public static double applyOneDollarOffIfOverFive(double chargeBeforeCoupon) {
        if (chargeBeforeCoupon > 5.0) {
            return chargeBeforeCoupon - 1.0;
        }
        return chargeBeforeCoupon;
    }
}

// ==================== Price (abstract) ====================
abstract class Price {
    public abstract int getPriceCode();
    public abstract double getCharge(int daysRented);

    public int getFrequentRenterPoints(int daysRented) {
        return 1;
    }
}

// ==================== RegularPrice ====================
class RegularPrice extends Price {
    public int getPriceCode() {
        return Movie.REGULAR;
    }

    public double getCharge(int daysRented) {
        double amount = 2;
        if (daysRented > 2) {
            amount += (daysRented - 2) * 1.5;
        }
        return amount;
    }
}

// ==================== NewReleasePrice ====================
class NewReleasePrice extends Price {
    public int getPriceCode() {
        return Movie.NEW_RELEASE;
    }

    public double getCharge(int daysRented) {
        return daysRented * 3;
    }

    @Override
    public int getFrequentRenterPoints(int daysRented) {
        return (daysRented > 1) ? 2 : 1;
    }
}

// ==================== ChildrensPrice ====================
class ChildrensPrice extends Price {
    public int getPriceCode() {
        return Movie.CHILDRENS;
    }

    public double getCharge(int daysRented) {
        double amount = 1.5;
        if (daysRented > 3) {
            amount += (daysRented - 3) * 1.5;
        }
        return amount;
    }
}

// ==================== Movie ====================
class Movie {
    public static final int CHILDRENS   = 2;
    public static final int REGULAR     = 0;
    public static final int NEW_RELEASE = 1;

    private String title;
    private Price  price;

    public Movie(String title, int priceCode) {
        this.title = title;
        setPriceCode(priceCode);
    }

    public int getPriceCode() {
        return price.getPriceCode();
    }

    public void setPriceCode(int arg) {
        switch (arg) {
            case REGULAR:     this.price = new RegularPrice();     break;
            case NEW_RELEASE: this.price = new NewReleasePrice();  break;
            case CHILDRENS:   this.price = new ChildrensPrice();   break;
            default: throw new IllegalArgumentException("Incorrect price code");
        }
    }

    public String getTitle() {
        return title;
    }

    public double getCharge(int daysRented) {
        return price.getCharge(daysRented);
    }

    public int getFrequentRenterPoints(int daysRented) {
        return price.getFrequentRenterPoints(daysRented);
    }
}

// ==================== Rental ====================
class Rental {
    private Movie movie;
    private int   daysRented;
    private boolean oneDollarOffIfOverFiveCoupon;

    public Rental(Movie movie, int daysRented) {
        this(movie, daysRented, false);
    }

    public Rental(Movie movie, int daysRented, boolean oneDollarOffIfOverFiveCoupon) {
        this.movie = movie;
        this.daysRented = daysRented;
        this.oneDollarOffIfOverFiveCoupon = oneDollarOffIfOverFiveCoupon;
    }

    public int getDaysRented() {
        return daysRented;
    }

    public Movie getMovie() {
        return movie;
    }

    public double getCharge() {
        double charge = movie.getCharge(daysRented);
        if (oneDollarOffIfOverFiveCoupon) {
            charge = RentalDiscountUtil.applyOneDollarOffIfOverFive(charge);
        }
        return charge;
    }

    public int getFrequentRenterPoints() {
        return movie.getFrequentRenterPoints(daysRented);
    }
}

// Decorator base class - wraps a Rental and lets us modify getCharge()
abstract class RentalCoupon extends Rental {
    protected Rental rental;

    public RentalCoupon(Rental rental) {
        super(rental.getMovie(), rental.getDaysRented());
        this.rental = rental;
    }

    @Override
    public abstract double getCharge();

    @Override
    public int getFrequentRenterPoints() {
        return rental.getFrequentRenterPoints();
    }
}

// 50% off coupon - just cuts the charge in half
class HalfOffCoupon extends RentalCoupon {
    public HalfOffCoupon(Rental rental) {
        super(rental);
    }

    @Override
    public double getCharge() {
        return rental.getCharge() * 0.5;
    }
}

class FreeMovie extends RentalCoupon {
    public FreeMovie(Rental rental) {
        super(rental);
    }

    @Override
        public double getCharge() {
            return 0;
        }
}

// ==================== Customer ====================
public class Customer {
    private String       name;
    private List<Rental> rentals;
    private int customerPoints;

    public Customer(String name) {
        this.name    = name;
        this.rentals = new ArrayList<>();
    }

    public void addRental(Rental rental) {
        if(customerPoints >= 10) {
            rental = new FreeMovie(rental);
            customerPoints = customerPoints - 10;
        }
        rentals.add(rental);
        customerPoints = customerPoints + rental.getFrequentRenterPoints(); 
    }

    public String getName() {
        return name;
    }

    private double getTotalCharge() {
        double total = 0;
        for (Rental r : rentals) {
            total += r.getCharge();
        }
        return total;
    }

    private int getTotalFrequentRenterPoints() {
        int points = 0;
        for (Rental r : rentals) {
            points += r.getFrequentRenterPoints();
        }
        return points;
    }

    private String getRentalLines() {
        StringBuilder lines = new StringBuilder();
        for (Rental r : rentals) {
            lines.append("\t")
                 .append(r.getMovie().getTitle())
                 .append("\t")
                 .append(r.getCharge())
                 .append("\n");
        }
        return lines.toString();
    }

    public String statement() {
        String result = "Rental Record for " + getName() + "\n";
        result += getRentalLines();
        result += "Amount owed is " + getTotalCharge() + "\n";
        result += "You earned " + getTotalFrequentRenterPoints() + " frequent renter points";
        return result;
    }

    public String xmlStatement() {
        StringBuilder xml = new StringBuilder();
        xml.append("<statement>\n");
        xml.append("  <name>").append(getName()).append("</name>\n");
        for (Rental r : rentals) {
            xml.append("  <rental>\n");
            xml.append("    <movie>").append(r.getMovie().getTitle()).append("</movie>\n");
            xml.append("    <charge>").append(r.getCharge()).append("</charge>\n");
            xml.append("  </rental>\n");
        }
        xml.append("  <totalAmount>").append(getTotalCharge()).append("</totalAmount>\n");
        xml.append("  <frequentRenterPoints>").append(getTotalFrequentRenterPoints()).append("</frequentRenterPoints>\n");
        xml.append("</statement>");
        return xml.toString();
    }

    public static void main(String[] args) {
        Customer customer = new Customer("John Smith");
        customer.addRental(new Rental(new Movie("Independence Day",  Movie.REGULAR),     3));
        customer.addRental(new Rental(new Movie("Encanto",           Movie.CHILDRENS),   4));
        customer.addRental(new Rental(new Movie("Top Gun: Maverick", Movie.NEW_RELEASE), 2));

        System.out.println(customer.statement());
        System.out.println("--- XML ---");
        System.out.println(customer.xmlStatement());
    }
}
