package com.unacceptableuse.unacceptablebot.command;

import java.io.File;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import com.unacceptableuse.unacceptablebot.UnacceptableBot;
import com.unacceptableuse.unacceptablebot.variable.Level;

public class CommandSnapChat extends Command {

	@Override
	public void performCommand(User sender, Channel channel, String message,
			String[] args, PircBotX bot) {
		String com = args[1];
		switch (com) {
		case ("test"): {
			File f = new File("/api.pem");
			if (!f.exists()) {
				bot.sendIRC().message(channel.getName(),
						"Cannot send snaps. Missing " + f.getAbsolutePath());
			} else {
				bot.sendIRC().message(channel.getName(), "Snapchat is ok!");
			}
		}
		case ("send"): {
			try {
				String url = args[2];
				String target = args[3];
				UnacceptableBot.getSnapchat().getImage(url, target);
				bot.sendIRC().message(channel.getName(), "Snap sent.");
			} catch (Exception e) {
				bot.sendIRC().message(channel.getName(),
						"Did you enter the correct number of arguments?");
			}
		}
		case ("getUser"): {
			bot.sendIRC()
					.message(
							channel.getName(),
							"Snapchating as "
									+ UnacceptableBot.getSnapchat().getUser());
		}
		default: {
			bot.sendIRC().message(channel.getName(),
					"Unknown Snapchat API command!");
		}
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] { "sc", "snap", "snapchat" };
	}

	@Override
	public Level getAccessLevel() {
		return Level.SUPERADMIN;
	}

	@Override
	public int requiredArguments() {
		return 1;
	}

}
