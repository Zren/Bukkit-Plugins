package ca.xshade.questionmanager;

import java.util.ArrayList;
import java.util.List;

public class Question {
	// The name of who is asked the question.
	protected String target;
	
	// What the question is.
	protected String question;
	
	// What options does the target have.
	protected List<Option> options;
	
	// Will this question be deleted when session ends?
	protected boolean persistance = false;
	
	
	/**
	 * Constructor including the option to set persistence.
	 * 
	 * @param target
	 * @param question
	 * @param options
	 * @param persistance
	 */
	public Question (String target, String question, List<Option> options, boolean persistance) {
		this(target, question, options);
		this.persistance = persistance;
	}
	
	/**
	 * Constructor, assuming the question isn't persistent.
	 * 
	 * @param target
	 * @param question
	 * @param options
	 */
	public Question (String target, String question, List<Option> options) {
		this.target = target;
		this.question = question;
		this.options = new ArrayList<Option>(options);
		for (Option option : options)
			if (option.reaction instanceof QuestionTask)
				((QuestionTask) option.reaction).setQuestion(this);
	}
	
	/**
	 * Attempt to get the option specified by the command.
	 * 
	 * @param command
	 * @return the option attached to the command if it exists.
	 * @throws InvalidOptionException is called if the option doesn't exist.
	 */
	public Option getOption(String command) throws InvalidOptionException {
		for (Option option : options)
			if (option.command.toLowerCase().equals(command.toLowerCase()))
				return option;
		throw new InvalidOptionException();
	}
	
	public String getTarget() {
		return target;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public boolean hasCommand(String command) {
		for (Option option : options)
			if (option.isCommand(command))
				return true;
		return false;
	}
	
	/**
	 * Make of copy of this question, but with a new target.
	 * 
	 * @param target
	 * @return
	 */
	public Question newInstance(String target) {
		return new Question(target, question, options, persistance);
	}

	public List<Option> getOptions() {
		return options;
	}
}
