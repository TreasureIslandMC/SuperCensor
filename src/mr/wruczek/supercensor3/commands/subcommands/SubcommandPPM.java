package mr.wruczek.supercensor3.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mr.wruczek.supercensor3.PPUtils.PPManager;
import mr.wruczek.supercensor3.commands.SCMainCommand;
import mr.wruczek.supercensor3.commands.SCSubcommand;
import mr.wruczek.supercensor3.data.SCPlayerDataManger;
import mr.wruczek.supercensor3.utils.SCPermissionsEnum;
import mr.wruczek.supercensor3.utils.SCUtils;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SubcommandPPM extends SCSubcommand {
	
	public SubcommandPPM() {
		SCMainCommand.registerSubcommand(this, "pp", "ppm", "penaltypoints", "penaltypointsmanager");
		SCMainCommand.registerTabCompletion(this);
	}

	@Override
	public void onCommand(CommandSender sender, String command, String[] args) {
		
		if(!SCUtils.checkForPermissions(sender, SCPermissionsEnum.PPM.toString())) {
			return;
		}
		
		if(args.length < 2) {
			sender.sendMessage(SCUtils.getCommandDescription("Commands.PPM.CommandDescription"));
			SCUtils.sendCommandUsage(sender, "ppm check &8[&6playername&8]", "Commands.PPM.Check.HelpDescription");
			SCUtils.sendCommandUsage(sender, "ppm set &8<&6playername&8> &8<&6amount&8>", "Commands.PPM.Set.HelpDescription");
			
			if(SCUtils.isTellrawSupported(sender)) {
				sender.sendMessage("\n" + SCUtils.getMessageFromMessagesFile("Commands.HoverCommandTip"));
			}
			return;
		}
		
		if(args[1].equalsIgnoreCase("check")) {
			
			String nick;
			
			if(args.length < 3) {
				nick = sender.getName();
			} else {
				nick = args[2];
			}
			
			OfflinePlayer op = Bukkit.getOfflinePlayer(nick); // TODO: Deprecated!
			int points = PPManager.getPenaltyPoints(op);
			
			sender.sendMessage(SCUtils.getMessageFromMessagesFile("Commands.PPM.Check.Response")
					.replace("%nick%", op.getName()).replace("%points%", String.valueOf(points)));
		}
		
		if(args[1].equalsIgnoreCase("set")) {
			if(args.length < 4) {
				sender.sendMessage(SCUtils.getCommandDescription("Commands.PPM.Set.HelpDescription"));
				sender.sendMessage(SCUtils.getMessageFromMessagesFile("Commands.Usage") + " "
						+ SCUtils.usageFormatter("ppm set", "playername", "amount"));
				return;
			}
			
			OfflinePlayer op = Bukkit.getOfflinePlayer(args[2]); // TODO: Deprecated!
			int newPoints = 0;
			
			try {
				newPoints = Integer.parseInt(args[3]);
			} catch (Exception e) {
				sender.sendMessage(SCUtils.getMessageFromMessagesFile("Commands.PPM.Set.OnlyNumbers"));
				return;
			}
			
			PPManager.setPenaltyPoints(op, newPoints);
			
			sender.sendMessage(SCUtils.getMessageFromMessagesFile("Commands.PPM.Set.Response")
					.replace("%nick%", op.getName()).replace("%points%", String.valueOf(newPoints)));
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> ret = new ArrayList<>();
		
		if(args.length == 2) {
			ret.add("check");
			ret.add("set");
		} else if(args.length == 3) {
			for(Player p : Bukkit.getOnlinePlayers())
				ret.add(p.getName());
		} else if(args.length == 4) {
			if(args[1].equalsIgnoreCase("set"))
				ret.add("[amount]");
		}
		
		return ret;
	}

}