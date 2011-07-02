package ca.xshade.bukkit.worldstacker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.WorldEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldStacker extends JavaPlugin {
	public static final String pluginName = "WorldStacker";
	
	public List<String> worldStackList = new ArrayList<String>();
	public List<World> worldStack = new ArrayList<World>();
	
	@Override
	public void onDisable() {
		worldStack.clear();
		worldStackList.clear();
		System.out.println(String.format("[%s] Version %s - Disabled", pluginName, getDescription().getVersion()));
	}

	@Override
	public void onEnable() {
		worldStack.clear();
		
		getDataFolder().mkdir();
		
		
		// Load stack list config and add all current worlds to the stack.
		worldStackList = getConfiguration().getStringList("worldstack", Arrays.asList(new String[]{getServer().getWorlds().get(0).getName()}));
		System.out.println(String.format("[%s] World stack config: %s", pluginName, Arrays.toString(worldStackList.toArray(new String[0]))));
		for (World world : getServer().getWorlds())
			addWorldToStack(world);
		
		// Register events
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.High, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.High, this);
		getServer().getPluginManager().registerEvent(Event.Type.WORLD_LOADED, worldListener, Priority.Normal, this);
		
		System.out.println(String.format("[%s] Version %s - Enabled", pluginName, getDescription().getVersion()));
		
	}
	
	public WorldListener worldListener = new WorldListener() {
		@Override
		public void onWorldLoaded(WorldEvent event) {
			addWorldToStack(event.getWorld());
		}
	};
	
	public PlayerListener playerListener = new PlayerListener() {
		public void onPlayerTeleport(PlayerMoveEvent event) {
			onPlayerMove(event);
		}
		
		public void onPlayerMove(PlayerMoveEvent event) {
			if (event.getTo().getY() < 0) {
				teleportBelow(event.getPlayer(), event.getTo());
				System.out.println(event.getPlayer().getName() + " went down.");
			} else if (event.getTo().getY() >= 128) {
				teleportAbove(event.getPlayer(), event.getTo());
				System.out.println(event.getPlayer().getName() + " went up.");
			}
		}
	};
	
	public String worldStackToString() {
		if (worldStack.size() > 0) {
			String out = worldStack.get(0).getName();
			for (int i = 1; i < worldStack.size(); i++)
				out += ", " + worldStack.get(0).getName();
			return out;
		} else 
			return "";
	}
	
	public void addWorldToStack(World world) {
		int indexWhenFull = worldStackList.indexOf(world.getName());
		if (indexWhenFull != -1) {
			if (worldStack.size() == 0) {
				worldStack.add(world);
			} else {
				if (indexWhenFull == 0) {
					worldStack.add(0, world);
				} else {
					recInsertWorld(world, indexWhenFull, indexWhenFull - 1);
				}
			}
			
			System.out.println(String.format("[%s] World stack: %s", pluginName, worldStackToString()));
		}
	}
	
	public void recInsertWorld(World world, int indexWhenFull, int searchIndex) {
		if (searchIndex >= 0) {
			String worldToSearchFor = worldStackList.get(searchIndex);
			World w = getServer().getWorld(worldToSearchFor);
			if (w != null) {
				worldStack.add(worldStack.indexOf(w)+1, world);
			} else {
				recInsertWorld(world, indexWhenFull, searchIndex - 1);
			}
		} else {
			worldStack.add(0, world);
		}
	}
	
	public void teleportBelow(Player player, Location loc) {
		int index = worldStack.indexOf(loc.getWorld());
		
		// Not found
		if (index == -1)
			return;
		
		// Bottom most world.
		if (index == 0)
			return;
		
		player.teleportTo(new Location(worldStack.get(index-1), loc.getX(), 127, loc.getZ(), loc.getPitch(), loc.getYaw()));
	}
	
	public void teleportAbove(Player player, Location loc) {
		int index = worldStack.indexOf(loc.getWorld());
		
		// Not found
		if (index == -1)
			return;
		
		// Not the top most world.
		if (index < worldStack.size()-1)
			player.teleportTo(new Location(worldStack.get(index-1), loc.getX(), 1, loc.getZ(), loc.getPitch(), loc.getYaw()));
	}
}
