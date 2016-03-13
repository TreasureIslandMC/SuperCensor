package mr.wruczek.supercensor3.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;

import mr.wruczek.supercensor3.SCConfigManager2;
import mr.wruczek.supercensor3.SCMain;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCLogger {

	public enum LogType {

		CHAT("chat.txt"), CENSOR("censor.txt"), PLUGIN("plugin.txt");

		private final String fileName;

		private LogType(String fileName) {
			this.fileName = fileName;
		}

		public String getFileName() {
			return fileName;
		}
	}

	public static List<String> lastError;
	
	public static void handleException(Exception e) {
		
		lastError = new ArrayList<String>();
		
		logerror("");
		logerror("Exception in plugin SuperCensor");
		logerror(e.toString());
		
		if(e.getCause() != null)
			logerror(e.getCause().getMessage());
		
		logerror("");

		logerror("Server informations:");
		logerror("  " + SCMain.getInstance().getDescription().getFullName());
		logerror("  Server: " + Bukkit.getBukkitVersion() + " [" + Bukkit.getVersion() + "]");
		logerror("  Java: " + System.getProperty("java.version"));
		logerror("  Thread: " + Thread.currentThread());
		logerror("");

		logerror("StackTrace");
		logerror("");

		for (StackTraceElement stacktrace : e.getStackTrace()) {
			String st = stacktrace.toString();
			
			if(st.contains("mr.wruczek"))
				logerror("  @ > " + st);
			else
				logerror("  @ " + st);
		}
		
		logerror("");
		logerror("End of error");
		logerror("");
		logerror("If you want get help with this error:");
		logerror("  1. Run command \"SCreport\". It will send this error to hastebin,");
		logerror("  2. Use this link when creating new issue on BukkitDev.");
		logerror("");
	}

	private static void logerror(String err) {
		SCUtils.logError(err, LogType.PLUGIN);
		lastError.add(err);
	}

	public static void log(String text, LogType logType) {
		if (SCConfigManager2.isInitialized() && SCConfigManager2.config.getBoolean("Logger.Enabled")) {
			try {

				File logFile = getLogFile(logType.getFileName());

				if (!logFile.exists()) {
					logFile.getParentFile().mkdirs();
					logFile.createNewFile();
				}

				FileWriter fw = new FileWriter(logFile, true);
				PrintWriter pw = new PrintWriter(fw);
				pw.println(SCConfigManager2.config.getString("Logger.Prefix").replace("%date%", getDate())
						.replace("%time%", getTime()) + SCUtils.unColor(text));
				pw.flush();
				pw.close();
				fw.close();
			} catch (Exception e) {
				SCLogger.handleException(e);
			}
		}
	}

	public static String getDate() {
		DateFormat dateFormat = new SimpleDateFormat(SCConfigManager2.config.getString("Logger.DateFormat"));
		return dateFormat.format(new Date());
	}

	public static String getTime() {
		DateFormat godzina = new SimpleDateFormat(SCConfigManager2.config.getString("Logger.TimeFormat"));
		return godzina.format(new Date());
	}

	public static File getLogFile(String fileName) {
		return new File(SCConfigManager2.logsFolder + File.separator + getDate(), fileName);
	}
}