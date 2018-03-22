// Name: Tanawin Wichit
// Student ID: 6088221
// Section: 1

import java.util.Objects;

public class User {
    public static int lastUserID;
	private int uid;
	
	public User(int _id){
		this.uid = _id;
		lastUserID = _id;
	}
	
	public int getID(){
		return uid;
	}

	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		User user = (User) o;
		return uid == user.uid;
	}

	@Override
	public int hashCode(){
		return Objects.hash(uid);
	}
}
