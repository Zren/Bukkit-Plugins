package ca.xshade.bukkit.questioner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;

import ca.xshade.questionmanager.AbstractQuestion;
import ca.xshade.questionmanager.InvalidOptionException;
import ca.xshade.questionmanager.LinkedQuestion;
import ca.xshade.questionmanager.Option;
import ca.xshade.questionmanager.Poll;
import ca.xshade.questionmanager.PollQuestion;
import ca.xshade.questionmanager.PollTask;
import ca.xshade.questionmanager.Question;
import ca.xshade.questionmanager.QuestionFormatter;
import ca.xshade.questionmanager.QuestionManager;
import ca.xshade.questionmanager.QuestionTask;
import ca.xshade.util.StringMgmt;


public class Questioner extends JavaPlugin {
	protected QuestionManager questionManager;
	protected QuestionerPlayerListener playerListener;

	private List<Option> currentOptions = new ArrayList<Option>();
	private List<String> currentTargets = new ArrayList<String>();
	private int questionsPerPage = 5;
	private String questionFormat = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "%s" + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_GREEN + "%s";
	private String optionFormat = ChatColor.GREEN + "          /%s";
	private String optionEntendedFormat = ChatColor.YELLOW + " : %s";
	private String listFooterFormat = ChatColor.DARK_GRAY + " ---- " + ChatColor.GRAY + "Page: %d/%d " + ChatColor.DARK_GRAY + "~" + ChatColor.GRAY + " Total Questions: %d";
	
	public static void main(String[] args) {
		Questioner questioner = new Questioner();
		questioner.onEnable();
		
		{ // Insert first question
			List<Option> options = new ArrayList<Option>();
			options.add(new Option("yes maybe", new QuestionTask() {
				public void run() {
					System.out.println(((Question)getQuestion()).getTarget() + " recieved fries!");
				}
			}));
			options.add(new Option("no", new QuestionTask() {
				public void run() {
					System.out.println(((Question)getQuestion()).getTarget() + " slapped the worker!");
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
		
		{ // Insert a linked question
			List<Option> options = new ArrayList<Option>();
			options.add(new Option("yes", new QuestionTask() {
				public void run() {
					System.out.println("Chris gave you a high five!");
				}
			}));
			options.add(new Option("no", new QuestionTask() {
				public void run() {
					System.out.println("Chris slapped you!");
				}
			}));
			List<String> targets = new ArrayList<String>();
			targets.add("You");
			targets.add("Him");
			LinkedQuestion question = new LinkedQuestion(QuestionManager.getNextQuestionId(), targets, "Am I awesome?", options);
			try {
				questioner.getQuestionManager().appendLinkedQuestion(question);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		// Check his question list (should have one question)
		System.out.println("Peeking at his top question.");
		try {
			AbstractQuestion question = questioner.getQuestionManager().peekAtFirstQuestion("Him");
		
			for (String line : QuestionFormatter.format(question))
				System.out.println(line);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		// Ask questions
		System.out.println("Answer your questions.");
		boolean hasQuestion = true;
		do {
			if (!questioner.getQuestionManager().hasQuestion("You"))
				hasQuestion = false;
			else {
				try {
					AbstractQuestion question = questioner.getQuestionManager().peekAtFirstQuestion("You");
				
					for (String line : QuestionFormatter.format(question))
						System.out.println(line);
					do {
						Scanner input = new Scanner(System.in);
						try {
							question.getOption(input.nextLine()).getReaction().run();
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
		
		// Check his question list (should be empty)
		System.out.println("Peeking at his top question.");
		try {
			AbstractQuestion question = questioner.getQuestionManager().peekAtFirstQuestion("Him");
		
			for (String line : QuestionFormatter.format(question))
				System.out.println(line);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void onDisable() {
		playerListener = null;
		questionManager = null;
		System.out.println("[Questioner] v"+getDescription().getVersion()+" - Disabled");
	}

	@Override
	public void onEnable() {
		questionManager = new QuestionManager();
		playerListener = new QuestionerPlayerListener(this, questionManager);
		
		// Bukkit Server
		if (getServer() != null) {
			getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Low, this);
			System.out.println("[Questioner] v"+getDescription().getVersion()+" - Enabled");
		}
	}

	public QuestionManager getQuestionManager() {
		return questionManager;
	}
	
	public void appendQuestion(Question question) throws Exception {
		for (Option option : question.getOptions())
			if (option.getReaction() instanceof BukkitQuestionTask)
				((BukkitQuestionTask)option.getReaction()).setServer(getServer());
		getQuestionManager().appendQuestion(question);
		
		Player player = getServer().getPlayer(question.getTarget());
		if (player != null)
			for (String line : formatQuestion(question, "New Question"))
				player.sendMessage(line);
	}
	
	public void sendErrorMsg(String msg) {
		System.out.println("[Questioner] Error: " + msg);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName().toLowerCase();
		if (command.equals("question")) {
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
							sender.sendMessage("help > question opt [option]");
						}
						return true;
					} else if (args[0].equalsIgnoreCase("ask")) {
						try {
							String q = StringMgmt.join(StringMgmt.remFirstArg(args), " ");
							for (String target : currentTargets) {
								Question question = new Question(target, q, currentOptions);
								appendQuestion(question);
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
						int page = 1;
						if (args.length > 1) {
							try {
								page = Integer.parseInt(args[1]);
							} catch (NumberFormatException e) {
							}
						}
						for (String line : formatQuestionList(player.getName(), page))
							player.sendMessage(line);
						return true;
					}
				}
			}
			
			sender.sendMessage("Invalid sub command.");
			return true;
		}
		
		return false;
	}
	
	public List<String> formatQuestionList(String user, int page) {
		List<String> out = new ArrayList<String>();
		try {
			if (page < 0)
				throw new Exception("Invalid page number.");
			
			LinkedList<AbstractQuestion> activePlayerQuestions = getQuestionManager().getQuestions(user);
			int numQuestions = activePlayerQuestions.size();
			int maxPage = (int)Math.ceil(numQuestions / (double)questionsPerPage);
			if (page > maxPage) {
				throw new Exception("There are no questions on page " + page);
			} else {
				int start = (page-1)*questionsPerPage;
				for (int i = start; i < start+questionsPerPage ; i++) {
					try {
						AbstractQuestion question = activePlayerQuestions.get(i);
						out.addAll(formatQuestion(question, Integer.toString(i)));
					} catch (IndexOutOfBoundsException e) {
					}
				}
				if (maxPage > 1)
					out.add(String.format(listFooterFormat , page, maxPage, numQuestions));
			}
		} catch (Exception e) {
			out.add(ChatColor.RED + e.getMessage());
		}
		return out;
	}
	
	public List<String> formatQuestion(AbstractQuestion question, String tag) {
		List<String> out = new ArrayList<String>();
		out.add(String.format(questionFormat, tag, StringMgmt.maxLength(question.getQuestion(), 54)));
		for (Option option : question.getOptions())
			out.add(String.format(optionFormat, option.toString()) + (option.hasDescription() ? String.format(optionEntendedFormat, option.getOptionDescription()) : ""));
		return out;
	}
	
	public void loadClasses() {
		final String[] classes = new String[] {
			"ca.xshade.bukkit.questioner.BukkitQuestionTask",	
				
			"ca.xshade.questionmanager.AbstractQuestion",
			"ca.xshade.questionmanager.InvalidOptionException",
			"ca.xshade.questionmanager.LinkedQuestion",
			"ca.xshade.questionmanager.LinkedQuestionTask",
			"ca.xshade.questionmanager.Option",
			"ca.xshade.questionmanager.OptionTask",
			"ca.xshade.questionmanager.Poll",
			"ca.xshade.questionmanager.PollQuestion",
			"ca.xshade.questionmanager.PollTask",
			"ca.xshade.questionmanager.Question",
			"ca.xshade.questionmanager.QuestionFormatter",
			"ca.xshade.questionmanager.QuestionManager",
			"ca.xshade.questionmanager.QuestionTask"
		};
		
		for (String c : classes)
			try {
				Questioner.class.getClassLoader().loadClass(c);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}
}
