package ca.xshade.bukkit.debugger;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitDebugger extends JavaPlugin {
	DebuggerBlockListener blockListener = new DebuggerBlockListener();
	protected static final String newLine = System.getProperty("line.separator");
	private static BufferedWriter logger;
	
	@Override
	public void onDisable() {
		try {
			logger.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onEnable() {
		if (!getDataFolder().exists() && !getDataFolder().isDirectory())
			getDataFolder().mkdir();
		try {
			logger = new BufferedWriter(new PrintWriter(getDataFolder() + "/log.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("[Debugger] ERROR! Could not creat log file.");
		}
		
		registerEvents();
		System.out.println("[Debugger] Enabled - Logging all events.");
	}

	public void registerEvents() {
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Lowest, this);
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BURN, blockListener, Priority.Lowest, this);
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_CANBUILD, blockListener, Priority.Lowest, this);
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Lowest, this);
	}
	
	public static void log(Event.Type type, String[] keys, Object[] values) {
		if (keys.length != values.length)
			return;
		
		if (logger == null)
			System.out.println("[Debugger] ERROR! Could not log event.");
		
		try {
			for (int i = 0; i < keys.length; i++)
				logger.write(System.currentTimeMillis()
						+ " [" + type + "] "
						+ (keys[i] != null ? keys[i] : "Null") + " = "
						+ (values[i] != null ? values[i].toString() : "Null")
						+ newLine);
			logger.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
