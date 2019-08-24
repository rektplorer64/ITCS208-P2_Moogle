# ITCS208 - Project 2 - Moogle
A student project. Mahidol University, Faculty of ICT: `ITCS208 – Object Oriented Programming`. 2017

This program is designed to work in training model in Movie/Film domain. 
This program can calculate similarity scores by providing a dataset in CSV files. When inputted and trained, the program can give out results which similar to the given parameters...
It is also be able to Classify each User, and can give out result based on the behavior of each user.

###### Source Files
- **`BaseMovieSearchEngine.java`** → Interface for the Search Engine
- **`MainController.java`** → Main Function for Command line interface
- **`MoogleTester.java`** → Test Case for the whole Program
- **`Movie.java`** → Data Model Class for Movie Entity
- **`Rating.java`** → Data Model Class for Movie Rating
- **`SimpleMovieSearchEngine.java`** → The **Core** of the Program; It contains the algorithm to fetch data and train (Calculate Similarity Score) the Model. This class also contains a method to read the trained model to give out the best suggestion for each `User`s.
- **`User.java`** → Data Model Class for User

∴ There is no Database Implementation
