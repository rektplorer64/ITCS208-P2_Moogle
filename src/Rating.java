import java.util.Map;
import java.util.Objects;

// Name: Tanawin Wichit
// Student ID: 6088221
// Section: 1

public class Rating {
	public Movie m;
	public User u;
	public double rating;	//rating can be [0, 5]
	public long timestamp;	//timestamp tells you the time this rating was recorded
	
	
	public Rating(User _u, Movie _m, double _rating, long _timestamp) {
		if(_rating > 5 || _rating < 0){
			return;
		}
		this.m = _m;
		this.u = _u;
		this.rating = _rating;
		this.timestamp = _timestamp;
	}
	
	public String toString() {
		return "[uid: " + u.getID() +" mid: "+m.getID() +" rating: "+rating+"/5 timestamp: "+timestamp+"]";
	}

	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		Rating rating1 = (Rating) o;
		return Double.compare(rating1.rating, rating) == 0 &&
				timestamp == rating1.timestamp &&
				Objects.equals(m, rating1.m) &&
				Objects.equals(u, rating1.u);
	}

	@Override
	public int hashCode(){
		return Objects.hash(m, u, rating, timestamp);
	}


}
