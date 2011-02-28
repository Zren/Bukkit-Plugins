package ca.xshade.questionmanager;

public class Option {
	// The command used to choose this option.
	protected String command;
	
	// What is displayed when asked the question.
	// A null value will instead display what is in the variable command.
	protected String fullOption;
	
	// The reaction caused when the player chooses this option
	protected Runnable reaction;
	
	public Option (String command, Runnable reaction) {
		this.command = command;
		this.reaction = reaction;
		if (reaction instanceof OptionTask)
			((OptionTask) reaction).setOption(this);
	}
	
	public Option (String command, Runnable reaction, String fullOption) {
		this(command, reaction);
		this.fullOption = fullOption;
	}
	
	public String getOptionString() {
		if (fullOption == null)
			return command;
		else
			return fullOption;
	}

	public boolean isCommand(String command) {
		return this.command.toLowerCase().equals(command.toLowerCase());
	}
	
	public Runnable getReaction() {
		return reaction;
	}
}
