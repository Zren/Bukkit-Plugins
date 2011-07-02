package ca.xshade.bukkit.cinema;

import org.bukkit.entity.Player;

public abstract class Action implements Comparable {
	protected long t;
	abstract boolean doAction(Player player);
	
	public long getT() {
		return t;
	}
	
	public String toString() {
		return t+";a";
	}
	
    public int compareTo(Object o) {
		if (o instanceof Action)
			return (int)(this.t - ((Action)o).t);
		else
			return -1;
    }
}
