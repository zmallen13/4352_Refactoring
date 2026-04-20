import java.util.ArrayList;
import java.util.List;

abstract class Price {
    public abstract int getPriceCode();
    public abstract double getCharge(int daysRented);
    public int getFrequentRenterPoints(int daysRented) {
        return 1;
    }
}

class RegularPrice extends Price {
    public int getPriceCode() { return Movie.REGULAR; }
    public double getCharge(int daysRented) {
        double amount = 2;
        if (daysRented > 2) amount += (daysRented - 2) * 1.5;
        return amount;
    }
}

class NewReleasePrice extends Price {
    public int getPriceCode() { return Movie.NEW_RELEASE; }
    public double getCharge(int daysRented) {
        return daysRented * 3;
    }
    @Override
    public int getFrequentRenterPoints(int daysRented) {
        return (daysRented > 1) ? 2 : 1;
    }
}

class ChildrensPrice extends Price {
    public int getPriceCode() { return Movie.CHILDRENS; }
    public double getCharge(int daysRented) {
        double amount = 1.5;
        if (daysRented > 3) amount += (daysRented - 3) * 1.5;
        return amount;
    }
}

class Movie {
    public static final int CHILDRENS   = 2;
    public static final int REGULAR     = 0;
    public static final int NEW_RELEASE = 1;

    private String title;
    private Price price;

    public Movie(String title, int priceCode) {
        this.title = title;
        setPriceCode(priceCode);
    }

    public int getPriceCode() { return price.getPriceCode(); }

    public void setPriceCode(int arg) {
        switch (arg) {
            case REGULAR:     this.price = new RegularPrice();    break;
            case NEW_RELEASE: this.price = new NewReleasePrice(); break;
            case CHILDRENS:   this.price = new ChildrensPrice();  break;
            default: throw new IllegalArgumentException("Incorrect price code");
        }
    }

    public String getTitle() { return title; }
    public double getCharge(int daysRented) { return price.getCharge(daysRented); }
    public int getFrequentRenterPoints(int days) { return price.getFrequentRenterPoints(days); }
}

class Rental {
    private Movie movie;
    private int daysRented;

    public Rental(Movie movie, int daysRented) {
        this.movie = movie;
        this.daysRented = daysRented;
    }

    public int getDaysRented() { return daysRented; }
    public Movie getMovie() { return movie; }
    public double getCharge() { return movie.getCharge(daysRented); }
    public int getFrequentRenterPoints() { return movie.getFrequentRenterPoints(daysRented); }
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

class FreeMovie extends RentalCoupon{        //Coupon for a free movie
    public FreeMovie(Rental rental){
        super(rental);
    }

    @Override
        public double getCharge(){        //Chrage returns 0 because movie is free
            return 0;
        }
}

public class Customer {
    private String name;
    private Int customerPoints;        //Keeps track of how many points a customer has
    private List<Rental> rentals = new ArrayList<>();

    public Customer(String name) {
        this.name = name;
    }

    public void addRental(Rental rental) {
        if(customerPoints >= 10){                //If the customer has 10 or more points they get a free movie
            rental = new FreeMovie(rental);
            customerPoints = customerPoints - 10;        //Subtracts 10 points from the customer total points
        }
        rentals.add(rental);
        customerPoints = customerPoints + rental.getFrequentRenterPoints();        //adds points to a customer when they rent a movie
    }

    public String getName() {
        return name;
    }

    public String statement() {
        double totalAmount = 0;
        int frequentRenterPoints = 0;
        String result = "Rental Record for " + getName() + "\n";

        for (Rental r : rentals) {
            frequentRenterPoints += r.getFrequentRenterPoints();
            result += "\t" + r.getMovie().getTitle() + "\t" + r.getCharge() + "\n";
            totalAmount += r.getCharge();
        }

        result += "Amount owed is " + totalAmount + "\n";
        result += "You earned " + frequentRenterPoints + " frequent renter points";
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
        double total = 0;
        int points = 0;
        for (Rental r : rentals) {
            total += r.getCharge();
            points += r.getFrequentRenterPoints();
        }
        xml.append("  <totalAmount>").append(total).append("</totalAmount>\n");
        xml.append("  <frequentRenterPoints>").append(points).append("</frequentRenterPoints>\n");
        xml.append("</statement>");
        return xml.toString();
    }

    public static void main(String[] args) {
        Movie regular    = new Movie("The Godfather",    Movie.REGULAR);
        Movie newRelease = new Movie("Independence Day", Movie.NEW_RELEASE);
        Movie childrens  = new Movie("Toy Story",        Movie.CHILDRENS);

        Customer customer = new Customer("John Smith");
        customer.addRental(new Rental(regular, 3));
        customer.addRental(new HalfOffCoupon(new Rental(newRelease, 2))); // 50% off
        customer.addRental(new Rental(childrens, 4));

        System.out.println(customer.statement());
        System.out.println("\n--- XML ---\n");
        System.out.println(customer.xmlStatement());
    }
}
