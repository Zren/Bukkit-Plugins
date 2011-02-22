import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;


public class Parties extends JavaPlugin {
	private HashMap<String, Party> playerParty;
	private List<Party> party;
	private static final Party IGNORE_PARTY_REQUESTS = new Party("");
	private final String msgPrefix = "§3[§bParty§3]§f ";
	
    public Parties(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
    }

    public void onEnable() {
    	registerEvents();
    	
    	playerParty = new HashMap<String, Party>();
		party = new ArrayList<Party>();
		
		System.out.println("[Party] Mod Enabled - Version: Octorok");
    }
    
    public void onDisable() {
    	playerParty = null;
		party = null;
        System.out.println("[Party] Mod Disabled");
    }
    
    private void registerEvents() {
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGEDBY_ENTITY, entityListener, Priority.Normal, this);
    }
    
    private PlayerListener playerListener = new PlayerListener() {
    	public void onPlayerCommand(PlayerChatEvent event) {
    		if (event.isCancelled())
            	return;
            
            String[] split = event.getMessage().split(" ");
            Player player = event.getPlayer();
            
			if (split[0].equalsIgnoreCase("/party")) {
				String[] newSplit = new String[split.length-1];
				System.arraycopy(split, 1, newSplit, 0, split.length-1);
				parsePartyCommand(player, newSplit);
				event.setCancelled(true);
			} else if (split[0].equalsIgnoreCase("/pc")) {
				if (split.length > 1) {
					Party team = playerParty.get(player.getName());
					if (team != null && team != IGNORE_PARTY_REQUESTS) {
						sendPartyMessage(team, player.getName() + ": " + event.getMessage().substring(4));
					}
				} else {
					player.sendMessage(msgPrefix + "Message had no content.");
				}
				event.setCancelled(true);
			}
			
		}
    };
	
	private EntityListener entityListener = new EntityListener() {
		@Override
		
    	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    		if (event.isCancelled())
            	return;
    		System.out.println("[Parties] 0");
			Entity attacker = event.getDamager();
			Entity defender = event.getEntity();
			if (attacker instanceof Player && defender instanceof Player) {
				System.out.println("[Parties] 1");
				Player a = (Player)attacker;
				Party ap = playerParty.get(a.getName());
				if (ap != null && ap != IGNORE_PARTY_REQUESTS) {
					System.out.println("[Parties] 2");
					Player b = (Player)defender;
					Party bp = playerParty.get(b.getName());
					if (bp == ap) {
						System.out.println("[Parties] 3");
						event.setCancelled(true);
					}
				}
			}
			
		}
    };
	
	public void parsePartyCommand(Player player, String[] split) {
		if (split.length == 1) {
			if (split[0].equalsIgnoreCase("new")) {
				partyNew(player);
				return;
			}
			else if (split[0].equalsIgnoreCase("disband")) {
				partyDisband(player);
				return;
			}
			else if (split[0].equalsIgnoreCase("leave")) {
				partyLeave(player);
				return;
			} else if (split[0].equalsIgnoreCase("list")) {
				partyList(player);
				return;
			}
		} else if (split.length >= 2) {
			if (split[0].equalsIgnoreCase("add")) {
				String[] names = new String[split.length-1];
				System.arraycopy(split, 1, names, 0, split.length-1);
				partyAdd(player, names);
				return;
			} else if (split[0].equalsIgnoreCase("kick")) {
				String[] names = new String[split.length-1];
				System.arraycopy(split, 1, names, 0, split.length-1);
				partyKick(player, names);
				return;
			} else if (split[0].equalsIgnoreCase("toggle") && split[1].equalsIgnoreCase("invite")) {
				partyToggleInvite(player);
				return;
			}
		}
		
		// HELP
		showHelp(player);
	}
	
	public void partyNew(Player player) {
		if (playerParty.get(player.getName()) == null) {
			Party newParty = new Party(player.getName());
			party.add(newParty);
			playerParty.put(player.getName(), newParty);
			player.sendMessage(msgPrefix + "You started a new party!");
		} else {
			player.sendMessage(msgPrefix + "You already belong to a party.");
		}
	}
	
	public void partyDisband(Player player) {
		Party team = playerParty.get(player.getName());
		if (team != null) {
			if (team.leader.equals(player.getName())) {
				disbandParty(team);
				player.sendMessage(msgPrefix + "Team disbanded.");
			} else {
				player.sendMessage(msgPrefix + "Your not the team leader.");
			}
		} else {
			player.sendMessage(msgPrefix + "You don't belong to a party.");
		}
	}
	
	public void partyLeave(Player player) {
		Party team = playerParty.get(player.getName());
		if (team != null) {
			int index = team.members.indexOf(player.getName());
			if (index != -1) {
				team.members.remove(index);
				playerParty.remove(player.getName());
			}
			if (team.leader.equals(player.getName())) {
				if (team.size() == 0) {
					disbandParty(team);
					player.sendMessage(msgPrefix + "Team disbanded.");
					return;
				} else {
					team.leader = team.members.get(0);
				}
			}
			player.sendMessage(msgPrefix + "Left the team.");
			sendPartyMessage(team, player.getName() + " left the party.");
		} else {
			player.sendMessage(msgPrefix + "You don't belong to a party.");
		}
	}
	
	public void partyList(Player player) {
		Party team = playerParty.get(player.getName());
		if (team != null) {
			player.sendMessage(msgPrefix + "§6_____[ §eParty List §6]_____");
			for (String member : team.members) {
				Player p = getServer().getPlayer(member);
				if (p == null) continue;
				// No Colors/Groups yet
				//player.sendMessage(msgPrefix + "§f["+p.getColor().substring(0,2)+group+"§f] "+p.getName() + (p.getName().equals(team.leader) ? " §6[§eLeader§6]" : ""));
				player.sendMessage(msgPrefix + "§f"+p.getName() + (p.getName().equals(team.leader) ? " §6[§eLeader§6]" : ""));
			}
		} else {
			player.sendMessage(msgPrefix + "You don't belong to a party.");
		}
	}
	
	public void partyAdd(Player player, String[] names) {
		Party team = playerParty.get(player.getName());
		if (team != null) {
			if (team.leader.equals(player.getName())) {
				ArrayList<String> invited = new ArrayList<String>();
				for (String name : names) {
					List<Player> matches = getServer().matchPlayer(name);
					for (Player p : matches) { 
						if (p != null) {
							if (inviteToParty(p, team))
								invited.add(p.getName());
						}
					}
				}
				if (invited.size() > 0) {
					String msg = player.getName() + " invited ";
					for (String newMember : invited)
						msg += newMember+", ";
					sendPartyMessage(team, msg + "to the party.");
				} else {
					player.sendMessage(msgPrefix + "Non of those names were valid.");
				}
			} else {
				player.sendMessage(msgPrefix + "Your not the team leader.");
			}
		} else {
			player.sendMessage(msgPrefix + "You don't belong to a party.");
		}
	}
	
	public void partyKick(Player player, String[] names) {
		Party team = playerParty.get(player.getName());
		if (team != null) {
			if (team.leader.equals(player.getName())) {
				ArrayList<String> kicked = new ArrayList<String>();
				for (String name : names) {
					List<Player> matches = getServer().matchPlayer(name);
					for (Player p : matches) { 
						if (p.getName().equals(player.getName()))
							continue;
						if (p != null) {
							if (removeFromParty(p, team)) {
								kicked.add(p.getName());
								player.sendMessage(msgPrefix + player.getName()+" kicked you from the party.");
							}
						}
					}
				}
				if (kicked.size() > 0) {
					String msg = player.getName() + " kicked ";
					for (String kickedMember : kicked)
						msg += kickedMember+", ";
					sendPartyMessage(team, msg+"from the party.");
				} else {
					player.sendMessage(msgPrefix + "Non of those names were valid.");
				}
			} else {
				player.sendMessage(msgPrefix + "Your not the team leader.");
			}
		} else {
			player.sendMessage(msgPrefix + "You don't belong to a party.");
		}
	}
	
	public void partyToggleInvite(Player player) {
		Object pParty = playerParty.get(player.getName());
		if (pParty == null) {
			playerParty.put(player.getName(), IGNORE_PARTY_REQUESTS);
			player.sendMessage(msgPrefix + "Ignoring all party invites.");
		} else if (pParty == IGNORE_PARTY_REQUESTS) {
			playerParty.remove(player.getName());
			player.sendMessage(msgPrefix + "Accepting all party invites.");
		} else {
			player.sendMessage(msgPrefix + "Your already in a party.");
		}
	}
	
	public void showHelp(Player player) {
		player.sendMessage(msgPrefix + "§6_____[ §e/party §6]_____");
		player.sendMessage(msgPrefix + "All Players: leave, toggle invite, new, list");
		player.sendMessage(msgPrefix + "Party Leader: disband, add [p]..[p], kick [p]..[p]");
	}
	
	public void sendPartyMessage(Party party, String msg) {
		for (String member : party.members) {
			Player player = getServer().getPlayer(member);
			if (player != null)
				player.sendMessage(msgPrefix +  " " + msg);
		}
	}
	
	public void disbandParty(Party team) {
		for (String member : team.members)
			playerParty.remove(member);
		party.remove(party.indexOf(team));
	}
	
	public boolean inviteToParty(Player player, Party team) {
		Party pParty = playerParty.get(player.getName());
							
		if (pParty != null && pParty != IGNORE_PARTY_REQUESTS) {
			return false;
		} else {
			if (!team.members.contains(player.getName())) {
				team.members.add(player.getName());
				playerParty.put(player.getName(), team);
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean removeFromParty(Player player, Party team) {
		if (team.members.contains(player.getName())) {
			team.members.remove(player.getName());
			playerParty.remove(player.getName());
			return true;
		} else {
			return false;
		}
	}
}

class Party {
	ArrayList<String> members;
	String leader;
	
	public Party(String leader) {
		this.leader = leader;
		this.members = new ArrayList<String>();
		this.members.add(leader);
	}
	
	public int size() {
		return members.size();
	}
}
