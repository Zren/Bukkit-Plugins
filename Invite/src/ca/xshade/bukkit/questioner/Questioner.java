package ca.xshade.bukkit.questioner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;

import ca.xshade.questionmanager.InvalidOptionException;
import ca.xshade.questionmanager.Option;
import ca.xshade.questionmanager.Poll;
import ca.xshade.questionmanager.PollQuestion;
import ca.xshade.questionmanager.PollTask;
import ca.xshade.questionmanager.Question;
import ca.xshade.questionmanager.QuestionFormatter;
import ca.xshade.questionmanager.QuestionManager;
import ca.xshade.questionmanager.QuestionTask;

import com.shade.util.StringMgmt;

public class Questioner extends JavaPlugin {
	protected QuestionManager questionManager;
	protected QuestionerPlayerListener playerListener;

	private List<Option> currentOptions = new ArrayList<Option>();
	private List<String> currentTargets = new ArrayList<String>();

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
			try {
				questioner.getQuestionManager().appendQuestion(question);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		
		
		{ // Insert a poll
			List<Option> options = new ArrayList<Option>();
			options.add(new Option("rock", new PollTask()));
			options.add(new Option("metal", new PollTask()));
			Question question = new Question("", "What's better?", options);
			List<String> voters = Arrays.asList(new String[]{"You"});
			for (String voter : voters) {
				Poll poll = new Poll(
						voters,
						question) {
					public void end() {
						System.out.println("End of poll. Displaying results:");
						
						System.out.println("Voters choice:");
						HashMap<String,Option> results = getVoters();
						for (String voter : results.keySet())
							System.out.println("    " + voter + ": " + results.get(voter).getOptionString());
						
						System.out.println("Votes:");
						Map<Option,Integer> votes = getVotes();
						for (Option option : votes.keySet())
							System.out.println("    " + option.getOptionString() + ": " + votes.get(option));
						
					}
				};
				try {
					questioner.getQuestionManager().appendQuestion(new PollQuestion(poll, voter, question, poll.isPersistant()));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
		
		
		
		boolean hasQuestion = true;
		do { // Ask first question
			if (!questioner.getQuestionManager().hasQuestion("You"))
				hasQuestion = false;
			else {
				try {
					Question question = questioner.getQuestionManager().peekAtFirstQuestion("You");
				
					for (String line : QuestionFormatter.format(question))
						System.out.println(line);
					do {
						Scanner input = new Scanner(System.in);
						try {
							question.getOption(input.next()).getReaction().run();
							questioner.getQuestionManager().removeFirstQuestion("You");
							question = null;
						} catch (InvalidOptionException e) {
							System.out.println(e.getMessage());
						}
					} while (question != null);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					hasQuestion = false;
				}
			}
		} while (hasQuestion);
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
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName().toLowerCase();
		if (command.equals("que")) {
			if (args.length > 0) {
				if (sender.isOp()) {
					if (args[0].equalsIgnoreCase("target")) {
						for (int i = 1; i < args.length; i++)
							currentTargets.add(args[i]);
						sender.sendMessage("NumTargets: " + currentTargets.size());
						return true;
					} else if (args[0].equalsIgnoreCase("opt")) {
						if (args.length > 1) {
							currentOptions.add(new Option(args[1], new QuestionTask() {
								public void run() {
									System.out.println("You chose " + getOption().getOptionString() + "!");
								}
							}));
							sender.sendMessage("NumOptions: " + currentOptions.size());
						} else {
							sender.sendMessage("help > que opt [option]");
						}
						return true;
					} else if (args[0].equalsIgnoreCase("ask")) {
						try {
							String q = StringMgmt.join(StringMgmt.remFirstArg(args), " ");
							for (String target : currentTargets) {
								Question question = new Question(target, q, currentOptions);
								getQuestionManager().appendQuestion(question);
								Player player = getServer().getPlayer(target);
								if (player != null)
									player.sendMessage(q);
							}
							currentOptions.clear();
							currentTargets.clear();
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("list")) {
					if (sender instanceof Player) {
						Player player = (Player)sender;
						try {
							LinkedList<Question> activePlayerQuestions = getQuestionManager().getQuestions(player.getName());
							for (Question question : activePlayerQuestions) {
								player.sendMessage(question.getQuestion());
								for (Option option : question.getOptions())
									player.sendMessage("    " + option.getOptionString());
							}
						} catch (Exception e) {
							player.sendMessage(e.getMessage());
						}
						return true;
					}
				} 
			} else {
				sender.sendMessage("Invalid sub command.");
				return true;
			}
		}
		
		return false;
	}
}
