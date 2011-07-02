package ca.xshade.bukkit.cinema;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.entity.Player;

public class MovieRecorder {
	private Movie movie;
	private String filepath, actor;
	private ArrayList<String> actors;
	private long startedRecording;
	private Timer timer;
	private boolean recording;
	private int moveInterval;
	
	public MovieRecorder(Movie movie, String filepath, String actor) {
		this.movie = movie;
		this.filepath = filepath;
		this.actor = actor;
		this.timer = new Timer();
		this.recording = false;
		this.moveInterval = 50;
	}
	
	public boolean isRecording() {
		return recording;
	}
	
	public void setRecording(boolean recording) {
		this.recording = recording;
	}
	
	public boolean hasActor(String name) {
		return name.equals(actor);
	}
	
	public ArrayList<String> getActors() {
		return actors;
	}
	
	public void addActor(String actor) {
		this.actors.add(actor);
	}
	
	public String getActor() {
		return actor;
	}
	
	public void setMoveInterval(int moveInterval) {
		this.moveInterval = moveInterval;
	}
	
	public void addAction(Action action) {
		movie.addAction(action);
	}
	
	public long getRecordingTime() {
		return System.currentTimeMillis() - startedRecording;
	}
	
	public void start() {
		recording = true;
		startedRecording = System.currentTimeMillis();
		timer.schedule(new RecordingTicker(), 0, moveInterval);
	}
	
	public void finish() {
		timer.cancel();
		movie.save(filepath);
	}
	
	public Movie getMovie() {
		return movie;
	}
	
	private class RecordingTicker extends TimerTask {
		public void run() {
			Player player = Cinema.server.getPlayer(actor);
			if (player != null)
				movie.addAction(new MoveAction(getRecordingTime(), player.getLocation()));
			else
				finish();
		}
	}
}


