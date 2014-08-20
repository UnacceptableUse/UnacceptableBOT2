package com.unacceptableuse.unacceptablebot.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import com.unacceptableuse.unacceptablebot.UnacceptableBot;

/**
 * 
 * @author Neil
 *
 */
public class CommandQuote extends Command {

	@Override
	public void performCommand(User sender, Channel channel, String message,
			String[] args, PircBotX bot) {

		int count = 1;

		for (int i = 0; i < args.length; i++) {
			if (args[i].contains("--count".toLowerCase())
					|| args[i].contains("-c".toLowerCase())) {
				try {
					count = Integer.parseInt(args[i+1]);
				} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
					bot.sendIRC().message(channel.getName(), "Something went wrong here");
					e.printStackTrace();
				}
			}
		}

		try {
			PreparedStatement ps = UnacceptableBot.getConfigHandler().sql
					.getPreparedStatement("SELECT Message FROM `teknogeek_unacceptablebot`.`"+ channel.getName()+ "` WHERE Username = '"+ args[1].replace(" ", "")+ "' ORDER BY RAND() LIMIT " + count);
			/*
			 * ps.setString(1, args.length == 3 ? args[2].startsWith("#") ?
			 * args[2] : "#" + args[2] : channel.getName()); Like, what the fuck
			 * are these even here for? ps.setString(2, args[1]);
			 */
			ResultSet rs = ps.executeQuery();
			bot.sendIRC().message(channel.getName(),
					"<" + args[1] + "> " + rs.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String[] getAliases() {
		return new String[] { "quote" };
	}

	@Override
	public int requiredArguments() {
		return 1;
	}

}
