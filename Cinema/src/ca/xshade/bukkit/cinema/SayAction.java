package ca.xshade.bukkit.cinema;

import org.bukkit.entity.Player;

public class SayAction extends Action {
	String[] lines;
	
	public SayAction(long t, String[] lines) {
		this.t = t;
		this.lines = lines;
	}
	
	public boolean doAction(Player player) {
		for (String line : lines)
			player.sendMessage(line);
		return true;
	}
	
	public String toString() {
		String out = t+";s;";
		if (lines.length > 0) {
			out += lines[0];
			for (int i = 1; i < lines.length; i++)
				out += "@"+lines[i].replaceAll("§","&");
		}
		return out;
	}
}
