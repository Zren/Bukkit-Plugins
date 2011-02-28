package ca.xshade.bukkit.questioner;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;

import ca.xshade.questionmanager.InvalidOptionException;
import ca.xshade.questionmanager.Option;
import ca.xshade.questionmanager.Question;
import ca.xshade.questionmanager.QuestionFormatter;
import ca.xshade.questionmanager.QuestionManager;
import ca.xshade.questionmanager.QuestionTask;

public class Questioner extends JavaPlugin {
	protected QuestionManager questionManager;
	protected QuestionerPlayerListener playerListener;

	

	public static void main(String[] args) {
		Questioner questioner = new Questioner();
		questioner.onEnable();
		
		{ // Insert first question
			List<Option> options = new ArrayList<Option>();
			options.add(new Option("yes", new QuestionTask() {
				public void run() {
					System.out.println(getQuestion().getTarget() + " recieved fries!");
				}
			}));
			options.add(new Option("no", new QuestionTask() {
				public void run() {
					System.out.println(getQuestion().getTarget() + " slapped the worker!");
				}
			}));
			Question question = new Question("You", "Would you like fries with that?", options);
			questioner.getQuestionManager().appendQuestion(question);
		}
		
		Question question = null;
		do { // Ask first question
			try {
				question = questioner.getQuestionManager().peekAtFirstQuestion("You");
			
				for (String line : QuestionFormatter.format(question))
					System.out.println(line);
				do {
					Scanner input = new Scanner(System.in);
					try {
						question.getOption(input.next()).getReaction().run();
						question = null;
					} catch (InvalidOptionException e) {
						System.out.println(e.getMessage());
					}
				} while (question != null);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		} while (question != null);
	}
	
	@Override
	public void onDisable() {
		playerListener = null;
		questionManager = null;
	}

	@Override
	public void onEnable() {
		questionManager = new QuestionManager();
		playerListener = new QuestionerPlayerListener(this, questionManager);
		
		// Bukkit Server
		if (getServer() != null) {
			getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);
		}
	}

	public QuestionManager getQuestionManager() {
		return questionManager;
	}
	
	public void sendErrorMsg(String msg) {
		System.out.println("[Questioner] Error: " + msg);
	}
}
