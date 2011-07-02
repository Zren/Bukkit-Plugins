package ca.xshade.bukkit.cinema;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

class CinemaPlayerListener extends PlayerListener {
	@Override
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		if (event.getAnimationType() == PlayerAnimationType.ARM_SWING) {
			MovieRecorder film = Cinema.actors.get(player.getName());
			if (film != null) {
				if (film.hasActor(player.getName())) {
					ItemStack hand = player.getItemInHand();
					if (hand.getType() == Material.OBSIDIAN) {
						if (!film.isRecording()) {
							film.start();
							player.sendMessage("Started recording");
						} else {
							film.finish();
							Movie movie = film.getMovie();
							Cinema.movies.put(movie.getName(), movie);
							Cinema.deleteInDevFilm(film);
							player.sendMessage("Finished recording");
						}
					} else if (hand.getType() == Material.BOOKSHELF) {
						film.addAction(new SayAction(film.getRecordingTime(), new String[]{}));
					} else if (hand.getType() == Material.CHEST) {
						film.addAction(new GiveAction(film.getRecordingTime(), new ItemStack(1)));
					}
				}				
			}
		}
	}
	
	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		MovieRecorder film = Cinema.actors.get(player.getName());
		if (film != null && film.isRecording()) {
			Movie movie = film.getMovie();
			movie.addAction(new MoveAction(film.getRecordingTime(), player.getLocation()));
		}
	}
}
