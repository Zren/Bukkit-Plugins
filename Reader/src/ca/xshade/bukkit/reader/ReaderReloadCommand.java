package ca.xshade.bukkit.reader;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.xshade.bukkit.util.Colors;

public class ReaderReloadCommand extends Command {
	Reader plugin;
	
	public ReaderReloadCommand(String name) {
		super("reader");
	}
	
	public ReaderReloadCommand(Reader plugin) {
		super("reader");
		this.plugin = plugin;
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		List<String> out = new ArrayList<String>();
		
		if (plugin.hasPermissionTo(sender, "reader.admin.reload")) {
			plugin.onDisable();
			plugin.onEnable();
		} else {
			out.add("§4[Reader] §cYou don't have permission to reload.");
		}
		
		if (!(sender instanceof Player)) {
			for (String line : out)
				sender.sendMessage(Colors.strip(line));
		} else {
			for (String line : out)
				sender.sendMessage(line);
		}
		
		return true;
	}

}
