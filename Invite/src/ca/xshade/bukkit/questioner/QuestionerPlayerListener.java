package ca.xshade.bukkit.questioner;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

import ca.xshade.questionmanager.InvalidOptionException;
import ca.xshade.questionmanager.QuestionManager;

public class QuestionerPlayerListener extends PlayerListener {
	Questioner plugin;
	private QuestionManager questionManager;
	
	public QuestionerPlayerListener(Questioner plugin, QuestionManager questionManager) {
		this.plugin = plugin;
		this.questionManager = questionManager;
	}
	
	@Override
	public void onPlayerCommandPreprocess(PlayerChatEvent event) {
		if (event.isCancelled())
			return;
		
		String command = event.getMessage().substring(1);
		System.out.println(command);
		Player player = event.getPlayer();
		
		try {
			Runnable reaction = questionManager.answerFirstQuestion(player.getName(), command);
			System.out.println("REACTING!");
			int id = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, reaction);
			if (id == -1)
				plugin.sendErrorMsg("Could not schedule reaction to " + player.getName() + "'s question.");
			event.setCancelled(true);
		} catch (InvalidOptionException e) {
		} catch (Exception e) {
		}
	}
}
