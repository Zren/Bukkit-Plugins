package ca.xshade.bukkit.towny.election;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ca.xshade.bukkit.questioner.Questioner;
import ca.xshade.bukkit.towny.Towny;

public class TownyElection extends JavaPlugin {
	Questioner questioner;
	Towny towny;
	int nextElectionId = 0;

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnable() {
		Plugin test = getServer().getPluginManager().getPlugin("Questioner");
		if (test != null && test instanceof Questioner) {
			questioner = (Questioner)test;
		} else {
			// throw a fit
		}
		test = getServer().getPluginManager().getPlugin("Towny");
		if (test != null && test instanceof Towny) {
			towny = (Towny)test;
		} else {
			// throw a fit
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName().toLowerCase();
		if (command.equals("election")) {
			
			return true; 
		}
		return false;
	}
}
