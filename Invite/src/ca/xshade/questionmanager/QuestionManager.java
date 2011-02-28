package ca.xshade.questionmanager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class QuestionManager {
	// All the active questions pending for a player.
	Map<String,LinkedList<Question>> activeQuestions = new HashMap<String,LinkedList<Question>>();
	
	public void newQuestion(Question question) {
		
	}
	
	public void appendQuestion(Question question) {
		LinkedList<Question> playersActiveQuestions = activeQuestions.get(question.target);
		if (playersActiveQuestions == null) {
			playersActiveQuestions = new LinkedList<Question>();
			activeQuestions.put(question.target.toLowerCase(), playersActiveQuestions);
		}
		playersActiveQuestions.add(question);
	}
	
	public LinkedList<Question> getQuestions(String target) throws Exception {
		LinkedList<Question> playersActiveQuestions = activeQuestions.get(target.toLowerCase());
		if (playersActiveQuestions == null)
			throw new Exception("There are no pending questions");
		return playersActiveQuestions;
	}
	
	public Question peekAtFirstQuestion(String target) throws Exception {
		LinkedList<Question> playersActiveQuestions = getQuestions(target);
		if (playersActiveQuestions.size() == 0) {
			removeAllQuestions(target);
			throw new Exception("There are no pending questions");
		}
		return playersActiveQuestions.peek();
	}
	
	public void removeAllQuestions(String target) {
		activeQuestions.remove(target.toLowerCase());
	}
	
	public Runnable answerFirstQuestion(String player, String command) throws InvalidOptionException, Exception {
		return peekAtFirstQuestion(player).getOption(command).reaction;
	}
}
