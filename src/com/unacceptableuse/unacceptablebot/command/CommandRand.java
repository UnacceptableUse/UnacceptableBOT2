package com.unacceptableuse.unacceptablebot.command;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import com.unacceptableuse.unacceptablebot.UnacceptableBot;
import com.unacceptableuse.unacceptablebot.variable.Level;

/**
 * 
 * @author Peter
 *
 */
public class CommandRand extends Command
{

	@Override
	public void performCommand(User sender, Channel channel, String message,
			String[] args, PircBotX bot)
	{
		String[] choice = message.replace("!rand","").replace("!choice","").split(",");
		String chosen = choice[UnacceptableBot.rand.nextInt(choice.length)];
		sendMessage(bot, chosen.charAt(0) == '!' ? "&REDYou can't use !rand to perform commands!" : chosen, channel);
		
	}

	public int requiredArguments()
	{
		return 2;
	}
	
	@Override
	public String[] getAliases()
	{
		
		return new String[]{"rand","random"};
	}

	@Override
	public Level getAccessLevel() {
		return Level.USER;
	}

}
