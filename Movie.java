public class Movie {

    public static final int CHILDRENS   = 2;
    public static final int REGULAR     = 0;
    public static final int NEW_RELEASE = 1;
    
    private String movieTitle;                        //Changed _title to movieTitle and _pricecode to movieType
    private int    movieType;
    
    public Movie(String title, int priceCode) {
        movieTitle = title;
        movieType = priceCode;
    }
    
    public int getPriceCode() {
        return movieType;
    }
    
    public void setPriceCode(int arg) {
        movieType = arg;
    }
    
    public String getTitle() {
        return movieTitle;
    }
}
