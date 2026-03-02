public class Rental {
    private Movie rentMovie;                //Changed _movie to rentMovie and _daysRented to rentDays
    private int   rentDays;
    
    public Rental(Movie movie, int daysRented) {
        rentMovie      = movie;
        rentDays = daysRented;
    }
    
    public int getDaysRented() {
        return rentDays;
    }
    
    public Movie getMovie() {
        return rentMovie;
    }
}
