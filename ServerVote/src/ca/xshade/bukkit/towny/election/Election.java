package ca.xshade.bukkit.towny.election;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: Limit one nominee per person.

public class Election {
	// Who called the election
	String instigator;
	
	// The nominees chosen for the election
	List<String> nominees = new ArrayList<String>();
	
	// Who voted for who
	Map<String,String> votes = new HashMap<String,String>();
	
	public Election (String instigator, List<String> voters) {
		for (String voter : voters)
			votes.put(voter, null);
	}
	
	public void voteFor(String voter, String vote) {
		votes.put(voter, vote);
	}
	
	public void nominate(String voter, String nominee) throws Exception {
		if (voter.equalsIgnoreCase(nominee))
			throw new Exception("You cannot nominate for yourself.");
		else if (nominees.contains(nominee))
			throw new Exception(nominee + " has already been nominated.");
		else
			nominees.add(nominee);
	}
	
	public boolean checkEnd() {
		for (String vote : votes.values())
			if (vote == null)
				return false;
		return true;
	}
}
