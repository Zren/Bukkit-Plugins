package ca.xshade.bukkit.cinema;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Movie {
	Logger log = Logger.getLogger("Minecraft");
	CinemaPlayerListener listener = new CinemaPlayerListener();
	
	private HashMap<String, Movie> movies;
	private HashMap<String, MovieRecorder> actors;
	private ArrayList<MovieRecorder> inDevFilms;
	
	private String cinemaFolder;
	
	private HashMap<Long, Action> actions;
	private String name;
	
	public Movie(String name) {
		this.name = name;
		actions = new HashMap<Long, Action>();
	}
	
	public String getName() {
		return name;
	}
	
	public Action getAction(long n) {
		return actions.get(n);
	}
	
	public Collection<Action> getActions() {
		return actions.values();
	}
	
	public boolean addAction(Action a) {
		if (a == null)
			return false;
		
		actions.put(a.getT(), a);
		return true;	
	}
	
	public boolean load(String path) {
		try {
			BufferedReader fin = new BufferedReader(new FileReader(path));
			String line = "";
			while ((line = fin.readLine()) != null) {
				String[] tokens = line.split(";");
				try {
					long t = Long.parseLong(tokens[0]);
					
					if (tokens.length == 7 && tokens[1].equals("m")) {
						try {
							Location l = new Location(
									Cinema.server.getWorld(tokens[1]),
									Double.parseDouble(tokens[2]),
									Double.parseDouble(tokens[3]),
									Double.parseDouble(tokens[4]),
									Float.parseFloat(tokens[5]),
									Float.parseFloat(tokens[6]));
							addAction(new MoveAction(t, l));
						} catch (Exception e) {}
					} else if (tokens.length > 2 && tokens[1].equals("s")) {
						addAction(new SayAction(t, tokens[2].replaceAll("&","§").split("@")));
					} else if (tokens.length > 2 && tokens[1].equals("i")) {
						try {
							int item = Integer.parseInt(tokens[2]);
							int amount = (tokens.length == 4) ? Integer.parseInt(tokens[3]) : 1;
							addAction(new GiveAction(t, new ItemStack(item, amount)));
						} catch (Exception e) {}
					}
				} catch (Exception e) {}
			}
			fin.close();
		} catch (Exception e) {}
		
		return true;
	}
	
	public void save(String path) {
		String newLine = System.getProperty("line.separator");
		try {
			BufferedWriter fout = new BufferedWriter(new PrintWriter(path));
			// Have all the easy to edit stuff at the top
			for (Action action : actions.values()) {
				if (!(action instanceof MoveAction)) {
					fout.write(action.toString() + newLine);
				}
			}
			// Then print all the move commands.
			for (Action action : actions.values()) {
				if (action instanceof MoveAction) {
					fout.write(action.toString() + newLine);
				}
			}
			
			fout.close();
		} catch (Exception e) {}
	}
}

