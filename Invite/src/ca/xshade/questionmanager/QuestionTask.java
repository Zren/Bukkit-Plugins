package ca.xshade.questionmanager;

public abstract class QuestionTask extends OptionTask {
	protected Question question;
	
	public Question getQuestion() {
		return question;
	}

	void setQuestion(Question question) {
		this.question = question;
	}
	
	public abstract void run();
}
