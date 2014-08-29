package com.unacceptableuse.unacceptablebot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;

import javax.net.ssl.HttpsURLConnection;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import com.google.gson.JsonObject;
import com.unacceptableuse.unacceptablebot.handler.CommandHandler;
import com.unacceptableuse.unacceptablebot.handler.ConfigHandler;
import com.unacceptableuse.unacceptablebot.handler.SnapchatHandler;
import com.unacceptableuse.unacceptablebot.threading.SnapchatThread;

@SuppressWarnings("rawtypes")
public class UnacceptableBot extends ListenerAdapter {

	private static CommandHandler handler = new CommandHandler();
	private static ConfigHandler config = new ConfigHandler();
	private static SnapchatHandler snapchat = new SnapchatHandler();
	public static Random rand = new Random();
	public static ArrayList<String> channels = null;
	private int messageCount = 0;
	private ArrayList<String> sexQuotes = new ArrayList<String>();

	/**
	 * Starts the init process of everything
	 * 
	 * @author UnacceptableUse
	 */
	public UnacceptableBot() {
		handler.init();
		try {
			snapchat.init();
			Timer timer = new Timer();
			timer.schedule(new SnapchatThread(), 0, (30 * 1000));
		} catch (Exception e) {
			e.printStackTrace();
		}
		config.init();
		channels = new ArrayList<String>();
		config.increment("stat:startups");
		config.setLong("startupTime", new Date().getTime());

		loadSexQuotes();

	}

	@Override
	public void onMessage(final MessageEvent event) throws Exception {
		recordMessage(event);
<<<<<<< Updated upstream
		if (event.getMessage().charAt(0) == '!') {
=======
		doReddit(event.getMessage(), event.getChannel().getName(),
				event.getUser(), event.getBot());
		if (event.getMessage().startsWith("!")) {
>>>>>>> Stashed changes
			// String channel = event.getChannel().getName();
			handler.processMessage(event);
		} else if (event.getChannel().getName().equals("#doge-coin")) {
			if (event.getUser().getNick().equals("DogeWallet")
					&& event.getMessage().contains(
							"sent " + getConfigHandler().getString("botName"))) {
				event.getBot().sendIRC().message("DogeWallet", ".balance");
			}
		}

		if (event.getChannel().getName().equals("##Ocelotworks")) {
			if (event.getMessage().equals("new topic plz"))
				messageCount = 100;
			if (event.getMessage().equals("reload those sweet sex phrases bro")) {
				sexQuotes.clear();
				loadSexQuotes();
			}
			messageCount++;
			if (messageCount > 100) {
				event.getBot()
						.sendRaw()
						.rawLine(
								"TOPIC ##Ocelotworks "
										+ sexQuotes.get(rand.nextInt(sexQuotes
												.size())));
				messageCount = 0;
			}
		}
	}

	@Override
	public void onPrivateMessage(final PrivateMessageEvent event)
			throws Exception {
		// if(event.getMessage().startsWith("!"))
		// {
		// String user = event.getUser().getNick();
		// handler.processMessage(event);
		// }

		if (event.getUser().getNick().equals("DogeWallet")) {
			if (event.getMessage().contains("Active")) {
				int activeUsers = Integer.parseInt(event.getMessage().split(
						": ")[1]);
				if (activeUsers == 0) {
					event.getBot()
							.sendIRC()
							.message("#doge-coin",
									">> There are no active users, so soak cannot happen :( <<");
				} else {
					long amtToSoak = (getConfigHandler().getLong(
							"dogeWalletBalance") - (getConfigHandler()
							.getInteger("faucetReserve") + getConfigHandler()
							.getInteger("profitReserve")))
							/ activeUsers;
					event.getBot().sendIRC()
							.message("#doge-coin", ".soak " + amtToSoak);
					getConfigHandler().increment("stat:totalSoaked",
							(int) amtToSoak);
				}
			} else {
				long balance = Long.parseLong(event.getMessage().split(".")[0]);
				final int tipOver = getConfigHandler().getInteger(
						"faucetReserve")
						+ getConfigHandler().getInteger("profitReserve")
						+ getConfigHandler().getInteger("soakThreshold");
				getConfigHandler().setLong("dogeWalletBalance", balance);

				if (balance > tipOver) {
					event.getBot().sendIRC().message("DogeWallet", ".active");
				} else {
					event.getBot()
							.sendIRC()
							.message(
									"#doge-coin",
									">> Only " + (tipOver - balance)
											+ " Doge needed to soak! <<");
				}

			}
		}
	}

	@Override
	public void onInvite(final InviteEvent event) {
		log("INFO", "INVITE",
				event.getUser() + " invited bot to " + event.getChannel());
		event.getBot().sendIRC().joinChannel(event.getChannel());
	}

	@Override
	public void onJoin(final JoinEvent event) {
		if (event.getUser().equals(event.getBot().getUserBot())) {
			channels.add(event.getChannel().getName());
			log("INFO", "JOIN", "Joined channel "
					+ event.getChannel().getName());
		}

	}

	/**
	 * Record the message to the database
	 * 
	 * @author Neil
	 * **/
	private void recordMessage(final MessageEvent event) throws SQLException {
		String message = event.getMessage();
		Channel channel = event.getChannel();
		User sender = event.getUser();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int date = calendar.get(Calendar.DATE);
		int month = calendar.get(Calendar.MONTH);
		final String[] months = new String[] { "Jan", "Feb", "Mar", "Apr",
				"May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
				"WHAT ARE YOU, A FUCKING WIZARD?!" };
		String stringMonth = months[month];
		String dateTime = date + " " + stringMonth + ", " + hours + ":"
				+ minutes;
		config.createChannelTable(channel.getName());
		config.setLog(dateTime, sender.getNick(), message, channel.getName());
	}

	private void doReddit(String message, String channel, User sender,
			PircBotX bot) {
		// TODO: fancy regex for this
		try {
			if (message.contains("/r/") && !message.contains("reddit.com")
					&& config.getUserLevel(sender) > 0) {
				String subreddit = message.split("/r/")[1].split(" ")[0];
				InputStream is = getUrlContents("http://api.reddit.com/r/"
						+ subreddit.replace(",", "").replace(".", "")
						+ "/about");
				com.google.gson.JsonParser parser = new com.google.gson.JsonParser();

				String subredditDesc = parser.parse(new InputStreamReader(is))
						.getAsJsonObject().get("data").getAsJsonObject()
						.get("public_description").getAsString();
				bot.sendIRC().message(
						channel,
						Colors.BOLD + "http://reddit.com/r/" + subreddit
								+ " - " + subredditDesc);
			}

			if (message.contains("/u/") && !message.contains("reddit.com")
					&& config.getUserLevel(sender) > 0) {
				String user = message.split("/u/")[1].split(" ")[0];
				InputStream is = getUrlContents("http://api.reddit.com/u/"
						+ user.replace(",", "").replace(".", "") + "/about");
				com.google.gson.JsonParser parser = new com.google.gson.JsonParser();

				JsonObject jo = parser.parse(new InputStreamReader(is))
						.getAsJsonObject().get("data").getAsJsonObject();

				int linkKarma = jo.get("link_karma").getAsInt();
				int commentKarma = jo.get("comment_karma").getAsInt();
				bot.sendIRC().message(
						channel,
						Colors.NORMAL + Colors.BOLD + "http://reddit.com/u/"
								+ user + " - " + linkKarma + " Link Karma. "
								+ commentKarma + " Comment Karma.");
			}
		} catch (Exception e) {
		}
	}

	public static void log(String level, String origin, String message) {
		try {
			getConfigHandler().createChannelTable("SYSTEM");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		getConfigHandler().setLog(new Date().toString(), origin,
				"[" + level + "]" + " " + message, "SYSTEM");
	}

	public static InputStream getUrlContents(String surl) {
		URL url;

		try {
			url = new URL(surl);
			URLConnection conn = url.openConnection();
			InputStream is = conn.getInputStream();

			return is;
		} catch (Exception e) {
			log("ERROR", "getUrlContents", "Unable to connect to " + surl
					+ ": " + e.toString());
			e.printStackTrace();
		}
		return null;
	}

	public static InputStream getHTTPSUrlContents(String surl) {
		URL url;

		try {
			url = new URL(surl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			InputStream is = conn.getInputStream();

			return is;

		} catch (Exception e) {
			log("ERROR", "getHTTPSUrlContents", "Unable to connect to " + surl
					+ ": " + e.toString());
			e.printStackTrace();
		}
		return null;
	}

	private void loadSexQuotes() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					"sexquotes.txt")));
			String line = "missingno";
			while ((line = br.readLine()) != null) {
				sexQuotes.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static CommandHandler getCommandHandler() {
		return handler;
	}

	public static ConfigHandler getConfigHandler() {
		return config;
	}

	public static ArrayList<String> getChannels() {
		return channels;
	}

	public static SnapchatHandler getSnapchat() {
		return snapchat;
	}

	public static void setSnapchat(SnapchatHandler sc) {
		snapchat = sc;
	}
}
