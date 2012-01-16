package org.rscdaemon.server.model;

import org.rscdaemon.server.util.DataConversions;

public class ChatMessage {
	/**
	 * Who sent the message
	 */
	private Mob sender;
	/**
	 * The message it self, in byte format
	 */
	private byte[] message;
	/**
	 * Who the message is for
	 */
	private Mob recipient = null;

	public static String[] abusiveWords = { "fuck", "gay", "faggot", "bitch",
			"whore", "cunt", "dick", "cock", "shit", "fag", "bastard", "pussy",
			"ass", "asshole", "twat", "bullass", "lesbian", "retard",
			"spastic", "prostitute", "vagina", "arsehole", "tosser", "homosex",
			"heterosex", "hitler", "urinate", "lesbo", "phuck", "bisex",
			"penis", "sperm", "rapist", "shag", "slag", "clit", "piss", "nazi",
			"urine", "wank", "naked", "niga", "nige", "gay", "rape", "cock",
			"homo", "arse", "crap", "poo" };

	public ChatMessage(Mob sender, byte[] message) {
		String chatMessage = DataConversions.byteToString(message, 0,
				message.length);
		for (int msg = 0; msg < abusiveWords.length; msg++) {
			String abusiveReplaceString = "";
			for (int msgs = 0; msgs < abusiveWords[msg].length(); msgs++) {
				abusiveReplaceString += "*";
			}
			chatMessage = chatMessage.replaceAll("(?i)" + abusiveWords[msg],
					abusiveReplaceString);
		}
		message = DataConversions.stringToByteArray(chatMessage);
		this.sender = sender;
		this.message = message;
	}

	public ChatMessage(Mob sender, String message, Mob recipient) {
		String chatMessage = message;
		for (int msg = 0; msg < abusiveWords.length; msg++) {
			String abusiveReplaceString = "";
			for (int msgs = 0; msgs < abusiveWords[msg].length(); msgs++) {
				abusiveReplaceString += "*";
			}
			chatMessage = chatMessage.replaceAll("(?i)" + abusiveWords[msg],
					abusiveReplaceString);
		}
		message = chatMessage;
		this.sender = sender;
		this.message = DataConversions.stringToByteArray(message);
		this.recipient = recipient;
	}

	public Mob getRecipient() {
		return recipient;
	}

	public Mob getSender() {
		return sender;
	}

	public byte[] getMessage() {
		return message;
	}

	public int getLength() {
		return message.length;
	}

}
