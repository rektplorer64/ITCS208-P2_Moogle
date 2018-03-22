// Name: Tanawin Wichit
// Student ID: 6088221
// Section: 1

import java.util.*;

public class Movie{

    //Field Variables
    private int mid;        /*An integer for Movie ID*/
    private String title;       /*A String for Movie Title*/
    private int year;       /*An integer for Movie's year of release*/
    private Set<String> tags;       /*A Set for Movie's tags*/
    private Map<Integer, Rating> ratings;    //mapping userID -> rating /*A Map for Ratings of the Movie (Integer of UserID is key, Rating is value)*/
    private Double avgRating;       /*A Double for Average Rating*/
    //additional

    /**
     * The constructor for Movie class
     * @param _mid Movie ID
     * @param _title Movie Title
     * @param _year Movie's year of release
     */
    public Movie(int _mid, String _title, int _year){
        this.mid = _mid;
        this.title = _title;
        this.year = _year;

        tags = new HashSet<>();     /*Initializes the tags set*/
        ratings = new HashMap<>();      /*Initializes the Rating map*/
    }

    /**
     * Returns Movie ID as an integer
     * @return Movie ID
     */
    public int getID(){
        return mid;
    }

    /**
     * Returns Movie Title as a String
     * @return Movie Title
     */
    public String getTitle(){
        return title;
    }

    /**
     * Returns Movie tags as a Set
     * @return Movie tags
     */
    public Set<String> getTags(){
        return tags;
    }

    /**
     * Adds a tag to the tags Set by giving a String
     * @param tag A tag to add
     */
    public void addTag(String tag){
        tags.add(tag);
    }

    /**
     * Returns Movie's Year of release as an integer
     * @return Movie's Year of release
     */
    public int getYear(){
        return year;
    }

    /**
     * Returns field variables as a String
     * @return field variables String
     */
    public String toString(){
        avgRating = calMeanRating();
        return "[mid: " + mid + ":" + title + " (" + year + ") " + tags + "] -> avg rating: " + avgRating;
    }

    /**
     * Calculates the average Rating
     * @return average Rating
     */
    public double calMeanRating(){
        double sum = 0;
        //System.out.println("rating size " + ratings.size());
        for (Rating f : ratings.values()) {     /*For each Ratings in the Map*/
            sum += f.rating;    /*Adds up Rating score*/
        }
        avgRating = sum / ratings.size();       /*Divides sum with size*/
        return avgRating;
    }

    /**
     * Calculates the average Rating and return it
     * @return average Rating
     */
    public Double getMeanRating(){
        avgRating = calMeanRating();
        return avgRating;
    }

    /**
     * Adds a Rating to the Rating Map
     * @param user User who rated the movie
     * @param movie Target Movie
     * @param rating Score for the Rating
     * @param timestamp The time of when the User rated the Movie
     */
    public void addRating(User user, Movie movie, double rating, long timestamp){
        if(rating == 0){        /*If given rating has score equals to ZERO, exit the method*/
            return;
        }
        ratings.put(user.getID(), new Rating(user, movie, rating, timestamp));
    }

    /**
     * Returns the Rating Map
     * @return Rating Map
     */
    public Map<Integer, Rating> getRating(){
        return ratings;
    }

    /**
     * Check whether the given movieID is in the given moviesMap
     * @param movieID Target for Checking
     * @param moviesMap Movies Map
     * @return Whether the given movieID is in the given moviesMap
     */
    public static boolean isAvailableInTheDatabase(int movieID, Map<Integer, Movie> moviesMap){
        return moviesMap.get(movieID) != null && movieID == moviesMap.get(movieID).mid;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        Movie movie = (Movie) o;
        return mid == movie.mid &&
                year == movie.year &&
                Objects.equals(title, movie.title) &&
                Objects.equals(tags, movie.tags) &&
                Objects.equals(ratings, movie.ratings) &&
                Objects.equals(avgRating, movie.avgRating);
    }

    @Override
    public int hashCode(){
        return Objects.hash(mid, title, year, tags, ratings, avgRating);
    }
}
