package ca.xshade.bukkit.cinema;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import redecouverte.npcspawner.BasicHumanNpc;
import redecouverte.npcspawner.BasicHumanNpcList;
import redecouverte.npcspawner.NpcSpawner;

import com.echo28.bukkit.vanish.Vanish;

public class Cinema extends JavaPlugin {
	public BasicHumanNpcList HumanNPCList;
	protected static Server server = null;
	
	protected CinemaPlayerListener playerListener = new CinemaPlayerListener(); 
	
	protected static HashMap<String, Movie> movies;
	protected static HashMap<String, MovieRecorder> actors;
	protected static ArrayList<MovieRecorder> inDevFilms;
	
	
	@Override
	public void onDisable() {
		movies = null;
		actors = null;
		inDevFilms = null;
	}

	@Override
	public void onEnable() {
		server = getServer();
		this.HumanNPCList = new BasicHumanNpcList();

		
		movies = new HashMap<String, Movie>();
		actors = new HashMap<String, MovieRecorder>();
		inDevFilms = new ArrayList<MovieRecorder>();
		
		load();
		
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ANIMATION, playerListener, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player)sender;
			String cmd = command.getName().toLowerCase();
			Location l = player.getLocation();
			
			if (cmd.equals("detach")) {
				Plugin test = getServer().getPluginManager().getPlugin("Vanish");
				if (test == null)
					return false;
				if (!test.isEnabled())
					return false;
				Vanish vanish = (Vanish)test;
				
				BasicHumanNpc hnpc = NpcSpawner.SpawnBasicHumanNpc(player.getName(), player.getName(), player.getWorld(), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
				this.HumanNPCList.put(player.getName(), hnpc);
				
				vanish.vanish(player);
			} else if (cmd.equals("cinema")) {
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("play")  && args.length == 2) {
						Movie movie = movies.get(args[1]);
						if (movie != null) {
							watchMovie(player, movie);
							player.sendMessage("Arena location set.");
							return true;
						} else {
							player.sendMessage("No movie with that name.");
						}
					} else if (args[0].equalsIgnoreCase("record")  && args.length == 2) {
						if (!actors.containsKey(player.getName())) {
							Movie movie = new Movie(args[1]);
							MovieRecorder movieRecorder = new MovieRecorder(movie, getDataFolder().getPath()+"/"+args[1]+".txt", player.getName());
							actors.put(player.getName(), movieRecorder);
							inDevFilms.add(movieRecorder);
							player.sendMessage("You started directing a new movie.");
						} else {
							player.sendMessage("Your already filming a movie.");
						}
						return true;
					} else if (args[0].equalsIgnoreCase("list")) {
						player.sendMessage("--- Movies ---");
						for (String movie : movies.keySet())
							player.sendMessage(movie);
						return true;
					}
				}
				
				// Howto use command
				player.sendMessage("  §3/cinema §brecord [movie] : Start recording a new movie");
				player.sendMessage("      §3Swing");
				player.sendMessage("          §bObsidian : Action! / Cut (auto save on cut)");
				player.sendMessage("          §bBookcase : Insert empty text annotation");
				player.sendMessage("          §bChest : Insert empty item gift");
				player.sendMessage("  §3/cinema §bplay [movie] : Playback a movie");
				player.sendMessage("  §3/cinema §blist : Display a list of movies");
				
				return true;
			}
		}
		
		return false;
	}

	public void vanishVanish(Vanish vanish, Player player) {
		if (vanish.invisible.contains(player))
		{
			vanish.reappear(player);
			return;
		}
		vanish.invisible.add(player);
		Player[] playerList = getServer().getOnlinePlayers();
		for (Player p : playerList)
		{
			if (vanish.getDistance(player, p) <= vanish.RANGE && !p.equals(player))
			{
				vanishInvisible(player, p);
			}
		}
	}
	
	public void vanishReappear(Vanish vanish, Player player) {
		if (vanish.invisible.contains(player))
		{
			vanish.invisible.remove(player);
			// make someone really disappear if there's any doubt, should remove
			// cloning
			vanish.updateInvisibleForAll();
			Player[] playerList = getServer().getOnlinePlayers();
			for (Player p : playerList)
			{
				if (vanish.getDistance(player, p) < vanish.RANGE && !p.equals(player))
				{
					vanishUninvisible(player, p);
				}
			}
		}
	}
	
	private void vanishInvisible(Player p1, Player p2) {
		CraftPlayer hide = (CraftPlayer) p1;
		CraftPlayer hideFrom = (CraftPlayer) p2;
		hideFrom.getHandle().a.b(new Packet29DestroyEntity(hide.getEntityId()));
	}
	
	private void vanishUninvisible(Player p1, Player p2) {
		CraftPlayer unHide = (CraftPlayer) p1;
		CraftPlayer unHideFrom = (CraftPlayer) p2;
		unHideFrom.getHandle().a.b(new Packet20NamedEntitySpawn(unHide.getHandle()));
	}
	
	public void load() {
		File dir = getDataFolder();
		try {
			if (!(dir.exists() && dir.isDirectory()))
				dir.mkdir();
		} catch(Exception e) {
			System.out.println("[Cinema] Error creating default file and folder.");
		}
		
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
		};
		String[] children = dir.list(filter);
		if (children != null) {
			for (String filmFilename : children) {
				String filmName = filmFilename.substring(0, filmFilename.length()-4);
				if (filmName.split(" ").length > 1)
					continue;
				
				Movie movie = new Movie(filmName);
				movie.load(getDataFolder().getPath() + "/" + filmFilename);
				movies.put(movie.getName(), movie);
			}
		}
	}
	
	public static void deleteInDevFilm(MovieRecorder film) {
		actors.remove(film.getActor());
		inDevFilms.remove(film);
	}
	
	public static void watchMovie(Player player, Movie movie) {
		Timer timer = new Timer();
		
		for (Action action : movie.getActions())
			timer.schedule(new ActionTask(action, player), action.getT());
	}
}
