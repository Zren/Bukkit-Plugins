package ca.xshade.bukkit.reader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijikokun.bukkit.Permissions.Permissions;


public class Reader extends JavaPlugin {
	public final static String newLine = System.getProperty("line.separator");
	public ConcurrentHashMap<String, String[]> readerCommands = new ConcurrentHashMap<String, String[]>();
	public final int linesAtATime = 12;
	protected Permissions permissions = null;
	
    public void onDisable() {
    	readerCommands.clear();
        System.out.println("[Reader] Mod Disabled");
    }

    public void onEnable() {
    	registerEvents();
    	try {
			if (!(getDataFolder().exists() && getDataFolder().isDirectory()))
				getDataFolder().mkdir();
		} catch(Exception e) {
			System.out.println("[Reader] Error creating data folder.");
		}
    	
    	FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
		};
		String[] children = getDataFolder().list(filter);
		if (children != null) {
			for (String commandFilename : children) {
				String commandName = commandFilename.substring(0, commandFilename.length()-4);
				if (commandName.split(" ").length > 1)
					continue;
				
				ArrayList<String> lines = new ArrayList<String>();
				try {
					BufferedReader fin = new BufferedReader(new FileReader(getDataFolder().getPath() + "/" + commandFilename));
					String line;
					while ((line = fin.readLine()) != null)
						lines.add(line.replaceAll("&", "§"));
					fin.close();
				} catch (Exception e) {}
				
				
				if (lines.size() > 0) {
					String[] arr = new String[lines.size()];
					for (int i = 0; i < lines.size(); i++)
						arr[i] = lines.get(i);
					readerCommands.put(commandName, arr);
				}
			}
		}
		
		System.out.println("[Reader] Mod Enabled - Version: 1.3");
		System.out.println("[Reader] Added: "+readerCommands.keySet().toString());
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    	String command = cmd.getName().toLowerCase();
    	if (command.equals("readeradd")) {
    		return (new ReaderAddCommand(this, readerCommands)).execute(sender, commandLabel, args);
    	} else if (command.equals("readerreload"))
    		return (new ReaderReloadCommand(this)).execute(sender, commandLabel, args);
    	return false;
    }
    
    public boolean hasPermissionTo(CommandSender sender, String node) {
    	if (sender.isOp())
			return true;
    	
    	if (!(sender instanceof Player))
    		return true;
    	
    	Player player = (Player)sender;
    	
    	if (permissions == null) {
    		Plugin test = getServer().getPluginManager().getPlugin("Permissions");
    		if(permissions == null)
    			if(test != null)
    				permissions = ((Permissions)test);
    	}
    	
		if (permissions != null)
			return Permissions.Security.permission(player, node);
		else
			return true;
    }
    
    private final PlayerListener playerListener = new PlayerListener() {
    	public void onPlayerCommandPreprocess(PlayerChatEvent event) {
    		if (event.isCancelled())
            	return;
            
            String[] split = event.getMessage().split(" ");
            Player player = event.getPlayer();
            
            String[] lines;
			int startLine = 0, page = 0, pages = 0;
            
            for (String commandName : readerCommands.keySet()) {
				if (split[0].equalsIgnoreCase("/" + commandName)) {
					if (!hasPermissionTo(player, "reader.command." + commandName)) {
						player.sendMessage("§4[Reader] §cYou don't have permission to use this command.");
						event.setCancelled(true);
						return;
					}
						
					lines = readerCommands.get(commandName);
					
					if (split.length == 2) {
						try {
							page = Integer.parseInt(split[1]);
							if (page > 0) {
								startLine = (page-1)*linesAtATime;
							} else {
								page = 1; startLine = 0;
							}
						} catch (Exception e) {}
					} else {
						page = 1; startLine = 0;
					}
					pages = (int) Math.ceil((double) lines.length / (double) linesAtATime);
					
					if (page > 1 || pages > 1) {
						if (page > pages) {
							player.sendMessage("§4[Reader] §cFile is only §4"+pages+" §cpages long.");
							event.setCancelled(true);
							return;
						}
						player.sendMessage("§6[ §f"+commandName+" §6] [§ePage: "+page+"/"+pages+"§6]");
					}
					for (int i = startLine; i < ((startLine+linesAtATime > lines.length) ? lines.length : startLine+linesAtATime); i++)
						player.sendMessage(lines[i]);
					event.setCancelled(true);
					return;
				}
			}
		}
    };
}
