package ca.xshade.bukkit.reader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.xshade.bukkit.util.Colors;

public class ReaderAddCommand extends Command {
	Reader plugin;
	ConcurrentHashMap<String, String[]> readerCommands;
	
	public ReaderAddCommand(String name) {
		super("reader");
	}
	
	public ReaderAddCommand(Reader plugin, ConcurrentHashMap<String, String[]> readerCommands) {
		super("reader");
		this.plugin = plugin;
		this.readerCommands = readerCommands;
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		List<String> out = new ArrayList<String>();
		
		if (plugin.hasPermissionTo(sender, "reader.admin.add")) {
			if (args.length > 1) {
				if (readerCommands.containsKey(args[0])) {
					String line = "";
					String[] lines = readerCommands.get(args[0]);
					for (int i = 1; i < args.length; i++)
						line += args[i] + " ";
					String[] newArr = new String[lines.length+1];
					for (int i = 0; i < newArr.length-1; i++)
						newArr[i] = lines[i];
					newArr[lines.length] = line.replaceAll("&", "§");
					readerCommands.put(args[0], newArr);
					try {
						BufferedWriter fout = new BufferedWriter(new FileWriter(plugin.getDataFolder().getPath() + "/" + args[0] + ".txt", true));
						fout.write(Reader.newLine + line);
						fout.close();
						out.add("§2[Reader] §aLine added.");
					} catch (Exception e) {
						out.add("§4[Reader] §cLine appended in server memory only.");
						System.out.println("[Reader] Error writing to file. Did you set permissions properly?");
					}
				} else {
					out.add("§4[Reader] §cThat command doesn't exist.");
				}
			}
		} else {
			out.add("§4[Reader] §cYou don't have permission to add lines.");
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
