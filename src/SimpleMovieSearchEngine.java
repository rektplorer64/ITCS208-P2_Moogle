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
        //  (1) , |-----(2)--------------|  |--------(4)----------|
        Pattern eachLinePattern = Pattern.compile("(\\d+),[\\\"]?(.+)[\\\"]?[\\s]?,(.+)");

        //Fields Variable for Movie Datatype
        int movieID, year = 0;
        String movieTitleAndYear, movieGenres;
        String movieTitle = null;

        //Pattern for the forth group of eachLinePattern; For example
        //comedy|Fantasy|Thriller
        //  (1)    (2)      (3)
        Pattern categoriesPattern = Pattern.compile("([^|]+)");

        //Pattern for the second group of eachLinePattern; For example
        //Twelve Monkeys (a.k.a. 12 Monkeys) (1995)
        //|------------(1)-----------------|  |(2)|
        //Four Rooms (1995)
        //|--(1)---|  |(2)|
        Pattern yearPattern = Pattern.compile("(.+) \\((\\d{4})\\)|(.+)");
        Matcher generalMatcher;
        Matcher yearMatcher;

        for(int i = 1; i < lines.length; i++){
            generalMatcher = eachLinePattern.matcher(lines[i]);

            if(!generalMatcher.find()){
                continue;
            }

            movieID = Integer.parseInt(generalMatcher.group(1));       /*Assigns group 1 into movieID field variable*/
            movieTitleAndYear = generalMatcher.group(2);       /*Assigns group 2 into movieTitleAndYear field variable*/

            yearMatcher = yearPattern.matcher(movieTitleAndYear);
            if(yearMatcher.find()){
                if(yearMatcher.group(1) == null || yearMatcher.group(2) == null){       /*If it has no year specified, get only its title*/
                    movieTitle = yearMatcher.group(3);
                    year = 0;
                }else{      /*If it has both title and year, get them all*/
                    movieTitle = yearMatcher.group(1);
                    year = Integer.parseInt(yearMatcher.group(2));      /*Assigns group 3 into year field variable*/
                }

            }

            movieGenres = generalMatcher.group(3);     /*Assigns group 3 into movieGenres field variable*/
            moviesMap.put(movieID, new Movie(movieID, movieTitle, year));    /*Stores all fields into the Map (the Key is movieID, and the value is a movie object)*/

            if(movieGenres.equals("(no genres listed)")){   /*If there is no categories to be assigned*/
                continue;
            }

            generalMatcher = categoriesPattern.matcher(movieGenres);       /*Sets the pattern for the Categories String*/
            //Assigns each categories to each Movie Object by using its movieID
            //System.out.println(movieGenres);
            while(generalMatcher.find()){
                //System.out.println("Genres = " + generalMatcher.group());
                moviesMap.get(movieID).addTag(generalMatcher.group());        //j is actually the movieID
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
     * @param tag Keyword String
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

    @Override
    public List<Movie> advanceSearch(String title, String tag, int year){
        List<Movie> result = new ArrayList<>();
        boolean tagIsOk;
        boolean yearIsOk = false;
        for(int movieID : movies.keySet()){
            if(title != null){
                boolean titleIsOk = movies.get(movieID).getTitle().toLowerCase().contains(title.toLowerCase());
                if(tag != null){
                    tagIsOk = movies.get(movieID).getTags().contains(tag);
                    if(year != -1){
                        yearIsOk = movies.get(movieID).getYear() == year;
                        if(titleIsOk && tagIsOk && yearIsOk){
                            result.add(movies.get(movieID));
                        }
                    }else{
                        if(titleIsOk && tagIsOk){
                            result.add(movies.get(movieID));
                        }
                    }
                }else{
                    if(year != -1){
                        yearIsOk = movies.get(movieID).getYear() == year;
                        if(titleIsOk && yearIsOk){
                            result.add(movies.get(movieID));
                        }
                    }else{
                        if(titleIsOk){
                            result.add(movies.get(movieID));
                        }
                    }
                }
            }else{
                if(tag != null){
                    tagIsOk = movies.get(movieID).getTags().contains(tag);
                    if(year != -1){
                        yearIsOk = movies.get(movieID).getYear() == year;
                        if(tagIsOk && yearIsOk){
                            result.add(movies.get(movieID));
                        }
                    }else{
                        if(tagIsOk){
                            result.add(movies.get(movieID));
                        }
                    }
                }else{
                    if(year != -1){
                        if(yearIsOk){
                            result.add(movies.get(movieID));
                        }
                    }else{
                        continue;
                    }
                }
            }
        }
        return result;
    }

    /**
     *
     * @param unsortedMovies
     * @param asc
     * @return
     */
    @Override
    public List<Movie> sortByTitle(List<Movie> unsortedMovies, boolean asc){
        unsortedMovies.sort(new Comparator<Movie>(){
            @Override
            public int compare(Movie c1, Movie c2){
                return c1.getTitle().compareToIgnoreCase(c2.getTitle());
            }
        });
        if(!asc){
            Collections.reverse(unsortedMovies);
        }
        return unsortedMovies;
    }

    /**
     *
     * @param unsortedMovies
     * @param asc
     * @return
     */
    @Override
    public List<Movie> sortByRating(List<Movie> unsortedMovies, boolean asc){
        for(int i = 0; i < unsortedMovies.size(); i++){
            for(int j = i + 1; j < unsortedMovies.size(); j++){
                if(asc && unsortedMovies.get(i).getMeanRating() > unsortedMovies.get(j).getMeanRating()){
                    Collections.swap(unsortedMovies, i, j);
                }
                if(!asc && unsortedMovies.get(i).getMeanRating() < unsortedMovies.get(j).getMeanRating()){
                    Collections.swap(unsortedMovies, i, j);
                }
            }
        }
        return unsortedMovies;
    }

}
