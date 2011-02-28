package ca.xshade.questionmanager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class QuestionManager {
	// All the active questions pending for a player.
	Map<String,LinkedList<Question>> activeQuestions = new HashMap<String,LinkedList<Question>>();
	
	public void newQuestion(Question question) {
		
	}
	
	public void appendQuestion(Question question) throws Exception {
		if (question.options.size() == 0)
			throw new Exception("Question has no options.");
		
		LinkedList<Question> playersActiveQuestions = activeQuestions.get(question.target);
		if (playersActiveQuestions == null) {
			playersActiveQuestions = new LinkedList<Question>();
			activeQuestions.put(question.target.toLowerCase(), playersActiveQuestions);
		}
		playersActiveQuestions.add(question);
		activeQuestions.put(question.target, playersActiveQuestions);
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
	
	public Runnable answerFirstQuestion(String target, String command) throws InvalidOptionException, Exception {
		return peekAtFirstQuestion(target).getOption(command).reaction;
	}
	
	public void removeFirstQuestion(String target) throws Exception {
		LinkedList<Question> playersActiveQuestions = getQuestions(target);
		if (playersActiveQuestions.size() == 0) {
			removeAllQuestions(target);
			throw new Exception("There are no pending questions");
		}
		playersActiveQuestions.removeFirst();
	}
	
	public boolean hasQuestion(String target) {
		try {
			LinkedList<Question> playersActiveQuestions = getQuestions(target);
			if (playersActiveQuestions.size() == 0) {
				removeAllQuestions(target);
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
