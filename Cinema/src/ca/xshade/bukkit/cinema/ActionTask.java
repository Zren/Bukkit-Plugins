package ca.xshade.bukkit.cinema;

import java.util.TimerTask;

import org.bukkit.entity.Player;

public class ActionTask extends TimerTask {
	private Action action;
	private Player player;
	
	public ActionTask(Action action, Player player) {
		this.player = player;
		this.action = action;
	}
	
	public void run() {
		action.doAction(player);
	}
}
