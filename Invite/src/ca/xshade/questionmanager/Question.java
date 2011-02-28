package ca.xshade.questionmanager;

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
	
	public Question (String target, String question, List<Option> options) {
		this.target = target;
		this.question = question;
		this.options = options;
		for (Option option : options)
			if (option.reaction instanceof QuestionTask)
				((QuestionTask) option.reaction).setQuestion(this);
	}
	
	public Question (String target, String question, List<Option> options, boolean persistance) {
		this(target, question, options);
		this.persistance = persistance;
	}
	
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
	
	public Question newInstance(String target) {
		return new Question(target, question, options, persistance);
	}
}
