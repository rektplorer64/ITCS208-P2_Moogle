// Name: Tanawin Wichit
// Student ID: 6088221
// Section: 1

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


public class SimpleMovieSearchEngine implements BaseMovieSearchEngine{
    public Map<Integer, Movie> movies;

    /**
     * Constructor for the SearchEngine
     */
    public SimpleMovieSearchEngine(){
        movies = new HashMap<>();       /*Initiates the Map*/
    }

    /**
     * Fetches movies.csv and extracts it into the movies Map
     * @param movieFilename String value for the location of the CSV file
     * @return A Map filled with fetched Data
     */
    @Override
    public Map<Integer, Movie> loadMovies(String movieFilename){
        Map<Integer, Movie> moviesMap = new HashMap<>();    /*The Map*/

        //Reading file by using StringBuilder
        String contentBuilder = "";
        try{
            contentBuilder = new String(Files.readAllBytes(Paths.get(movieFilename)));
        }catch(IOException e){
            e.printStackTrace();
        }

        String[] lines = contentBuilder.split("\n");        /* Separating Each lines into String */

        //Pattern for CSV; For example
        //101529,"Brass Teapot, The (2012)",Comedy|Fantasy|Thriller
        //  (1) , |-----(2)-------|  |(3)|  |--------(4)----------|
        Pattern eachLinePattern = Pattern.compile("(\\d+),[\\\"?]?(.*)\\s\\((\\d+)\\)[\\\"?]?,(.+)");

        //Fields Variable for Movie Datatype
        int movieID, year;
        String movieTitle, movieGenres;

        //Pattern for the forth group of eachLinePattern; For example
        //comedy|Fantasy|Thriller
        //  (1)    (2)      (3)
        Pattern categoriesPattern = Pattern.compile("([^|]+)");
        Matcher matcher;

        for(int i = 1; i < lines.length; i++){
            matcher = eachLinePattern.matcher(lines[i]);
            if(!matcher.find()){
                continue;
            }

            movieID = Integer.parseInt(matcher.group(1));       /*Assigns group 1 into movieID field variable*/
            movieTitle = matcher.group(2);       /*Assigns group 2 into movieTitle field variable*/
            year = Integer.parseInt(matcher.group(3));      /*Assigns group 3 into year field variable*/
            movieGenres = matcher.group(4);     /*Assigns group 4 into movieGenres field variable*/
            moviesMap.put(movieID, new Movie(movieID, movieTitle, year));    /*Stores all fields into the Map (the Key is movieID, and the value is a movie object)*/

            matcher = categoriesPattern.matcher(movieGenres);       /*Sets the pattern for the Categories String*/

            if(movieGenres.equals("(no genres listed)")){   /*If there is no categories to be assign*/
                continue;
            }

            //Assigns each categories to each Movie Object by using its movieID
            //System.out.println(movieGenres);
            while(matcher.find()){
                //System.out.println("Genres = " + matcher.group());
                moviesMap.get(movieID).addTag(matcher.group());        //j is actually the movieID
            }
        }

        return moviesMap;
    }

    /**
     * Fetches ratings.csv and extracts it into the set of each Movie Object
     * @param ratingFilename String value for the location of the CSV file
     */
    @Override
    public void loadRating(String ratingFilename){
        //Reading file by using StringBuilder
        StringBuilder contentBuilder = new StringBuilder();
        try(Stream<String> stream = Files.lines(Paths.get(ratingFilename), StandardCharsets.UTF_8)){
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }catch(IOException e){
            e.printStackTrace();
        }
        String[] line = contentBuilder.toString().split("\n");        /* Separating Each lines into String */

        //Pattern for the each line of Rating.csv; For example
        //668,108940,2.5,1391840917
        //(1)  (2)   (3)     (4)
        String eachLinePattern = "([\\d]+),([\\d]+),(.*),(\\d+)";
        Pattern pattern = Pattern.compile(eachLinePattern);

        //Fields Variable for Rating Datatype
        int userID;
        int movieId;
        double rating;
        long timestamp;

        for(int i = 1; i < line.length; i++){
            Matcher m = pattern.matcher(line[i]);
            if(m.matches()){
                userID = Integer.parseInt(m.group(1));      /*Assigns group 1 into userID field variable*/
                movieId = Integer.parseInt(m.group(2));     /*Assigns group 2 into movieId field variable*/
                if(!Movie.isAvailableInTheDatabase(movieId, movies)){       /*If movie ID is not in the map skip it*/
                    continue;
                }
                rating = Double.parseDouble(m.group(3));        /*Assigns group 3 into rating field variable*/
                timestamp = Long.parseLong(m.group(4));      /*Assigns group 4 into timestamp field variable*/
                movies.get(movieId).addRating(new User(userID), movies.get(movieId), rating, timestamp);        /*Puts every fields into the set in a Movie Object*/
            }
        }
    }

    /**
     * Fetches data from CSV files
     * @param movieFilename String value for the location of the movies CSV file
     * @param ratingFilename String value for the location of the ratings CSV file
     */
    @Override
    public void loadData(String movieFilename, String ratingFilename){
        //Loads Movies then Ratings
        movies = loadMovies(movieFilename);
        loadRating(ratingFilename);
    }

    /**
     * Returns every Movie as a Map
     * @return Map of every Movies
     */
    @Override
    public Map<Integer, Movie> getAllMovies(){
        return movies;
    }

    /**
     * Search Movies by the title
     * @param title Keyword String
     * @param exactMatch Exactly matches or not
     * @return the List of Movies which fulfills the criteria
     */
    @Override
    public List<Movie> searchByTitle(String title, boolean exactMatch){
        List<Movie> result = new ArrayList<>();
        if(exactMatch){
            for(int movieID : movies.keySet()){
                if(title.toLowerCase().equals(movies.get(movieID).getTitle().toLowerCase())){
                    result.add(movies.get(movieID));
                }
            }
        }else{
            for(int movieID : movies.keySet()){
                if(movies.get(movieID).getTitle().toLowerCase().contains(title.toLowerCase())){
                    result.add(movies.get(movieID));
                }
            }
        }
        return result;
    }

    /**
     * Search Movies by the tag
     * @param tag Keyword Tag String
     * @return the List of Movies which fulfills the criteria
     */
    @Override
    public List<Movie> searchByTag(String tag){
        List<Movie> result = new ArrayList<>();
        for(int movieID : movies.keySet()){
            if(movies.get(movieID).getTags().contains(tag)){
                result.add(movies.get(movieID));
            }
        }
        return result;
    }

    /**
     * Search Movies by using year input
     * @param year Year of released
     * @return the List of Movies which fulfills the criteria
     */
    @Override
    public List<Movie> searchByYear(int year){
        List<Movie> result = new ArrayList<>();
        for(int movieID : movies.keySet()){
            if(movies.get(movieID).getYear() == year){
                result.add(movies.get(movieID));
            }
        }
        return result;
    }

    /**
     * Search movies by given at least one field (title, tag, year)
     * @param title Keyword String
     * @param tag Keyword Tag String
     * @param year Year of released
     * @return the List of Movies which fulfills the criteria
     */
    @Override
    public List<Movie> advanceSearch(String title, String tag, int year){
        List<Movie> result = new ArrayList<>();     /*Initializes List*/
        boolean tagIsOk;
        boolean yearIsOk = false;
        for(int movieID : movies.keySet()){     /*For each key of movies*/
            if(title != null){      /*If title is given*/
                boolean titleIsOk = movies.get(movieID).getTitle().toLowerCase().contains(title.toLowerCase());
                if(tag != null){        /*If title and tag are given*/
                    tagIsOk = movies.get(movieID).getTags().contains(tag);      /*Check if any Movie entry has input tag as substring*/
                    if(year != -1){     /*If title, tag and year are given*/
                        yearIsOk = movies.get(movieID).getYear() == year;       /*Check if any Movie entry has exact input year*/
                        if(titleIsOk && tagIsOk && yearIsOk){
                            result.add(movies.get(movieID));        /*Add the item to result*/
                        }
                    }else{      /*If title and tag are given*/
                        if(titleIsOk && tagIsOk){
                            result.add(movies.get(movieID));
                        }
                    }
                }else{
                    if(year != -1){     /*If title and year are given*/
                        yearIsOk = movies.get(movieID).getYear() == year;       /*Check if any Movie entry has exact input year*/
                        if(titleIsOk && yearIsOk){
                            result.add(movies.get(movieID));
                        }
                    }else{      /*If only title is given*/
                        if(titleIsOk){
                            result.add(movies.get(movieID));
                        }
                    }
                }
            }else{      /*If the title is not given*/
                if(tag != null){
                    tagIsOk = movies.get(movieID).getTags().contains(tag);      /*Check if any Movie entry has input tag as substring*/
                    if(year != -1){     /*If tag and year are given*/
                        yearIsOk = movies.get(movieID).getYear() == year;       /*Check if any Movie entry has exact input year*/
                        if(tagIsOk && yearIsOk){
                            result.add(movies.get(movieID));
                        }
                    }else{      /*If tag are given*/
                        if(tagIsOk){
                            result.add(movies.get(movieID));
                        }
                    }
                }else{
                    if(year != -1){     /*If only year is given*/
                        if(yearIsOk){
                            result.add(movies.get(movieID));
                        }
                    }else{      /*If title, tag and year are not given*/
                        continue;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Sort Given List of Movies Alphabetically by their Titles
     * @param unsortedMovies List of target Movies
     * @param asc Boolean for Sorting in Ascending Order. If it is false, it means Sorting in Descending Order.
     * @return Alphabetically Sorted List
     */
    @Override
    public List<Movie> sortByTitle(List<Movie> unsortedMovies, boolean asc){
        unsortedMovies.sort(new Comparator<Movie>(){        /*Anonymous Class for custom Comparator which is required when sorting Custom Object or Class*/
            /**
             * Compares 2 Movies by their Title
             * @param c1 Movie 1
             * @param c2 Movie 2
             * @return An integer which will indicate which Movie Title is higher in Alphabetical Order
             */
            @Override
            public int compare(Movie c1, Movie c2){
                return c1.getTitle().compareToIgnoreCase(c2.getTitle());        /*Compare Movies by their Title (by using compareToIgnoreCase())*/
            }
        });
        if(!asc){       /*If the boolean parameter is not Ascending Order*/
            Collections.reverse(unsortedMovies);        /*Reverses the List*/
        }
        return unsortedMovies;
    }

    /**
     * Sort Given List of Movies Numerically by their Average Rating
     * @param unsortedMovies List of target Movies
     * @param asc Boolean for Sorting in Ascending Order. If it is false, it means Sorting in Descending Order.
     * @return Alphabetically Sorted List
     */
    @Override
    public List<Movie> sortByRating(List<Movie> unsortedMovies, boolean asc){
        for(int i = 0; i < unsortedMovies.size(); i++){
            for(int j = i + 1; j < unsortedMovies.size(); j++){
                if(asc && unsortedMovies.get(i).getMeanRating() > unsortedMovies.get(j).getMeanRating()){       /*If the boolean parameter is Ascending Order and average mean of i is greater than j*/
                    Collections.swap(unsortedMovies, i, j);     /*Swaps i and j*/
                }
                if(!asc && unsortedMovies.get(i).getMeanRating() < unsortedMovies.get(j).getMeanRating()){      /*If the boolean parameter is NOT Ascending Order and average mean of i is lower than j*/
                    Collections.swap(unsortedMovies, i, j);     /*Swaps i and j*/
                }
            }
        }
        return unsortedMovies;
    }

}
