package ca.xshade.bukkit.questioner;

import org.bukkit.Server;

import ca.xshade.questionmanager.QuestionTask;

public abstract class BukkitQuestionTask extends QuestionTask {
	protected Server server;
	
	public Server getServer() {
		return server;
	}
	
	public void setServer(Server server) {
		this.server = server;
	}
	
	public abstract void run();
}
