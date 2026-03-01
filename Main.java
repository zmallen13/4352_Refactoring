public class Main {

    public static void main(String[] args) {
        Movie regularMovie = new Movie("The Godfather", Movie.REGULAR);
        Movie newReleaseMovie = new Movie("Independence Day", Movie.NEW_RELEASE);
        Movie childrensMovie = new Movie("Toy Story", Movie.CHILDRENS);

        Customer customer = new Customer("John Smith");
        customer.addRental(new Rental(regularMovie, 3));
        customer.addRental(new Rental(newReleaseMovie, 2));
        customer.addRental(new Rental(childrensMovie, 4));

        System.out.println(customer.statement());
        System.out.println();
        System.out.println(customer.statementXml());
    }
}

