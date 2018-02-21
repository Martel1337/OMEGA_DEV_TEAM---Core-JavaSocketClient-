package me.ByteCoder.Core.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import me.ByteCoder.Core.JCController.Main;

public class PacketWriter {

private DataOutputStream out;
private DataInputStream in;
private Socket socket;
private String data;
	
public PacketWriter(){
	if(Main.c.isConncted()) {
		try{
			this.socket = new Socket(Main.c.getIP(), Main.c.getPort());
			this.in = new DataInputStream(this.socket.getInputStream());
			this.out = new DataOutputStream(this.socket.getOutputStream());
			this.out.writeUTF("Handshake");
	        this.out.writeUTF(Main.c.getName());
	        this.out.writeUTF(Main.c.getPassword());
	        this.out.flush();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.data = null;
		this.readPacket();
	}
}

public DataInputStream getInputStream(){
	return this.in;
}

public DataOutputStream getOutputStream(){
	return this.out;
}

public void close(){
	if(Main.c.isConncted()) {
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

public Socket getSocket(){
	return this.socket;
}

public void sendPacket(String type, String action, String data, String target){
	    if (Main.c.isConncted()) {
		    try
		    {
		      this.out.writeUTF(type);
		      this.out.writeUTF(action);
		      this.out.writeUTF(data);
		      this.out.writeUTF(target);
		      this.out.flush();
		    }
		    catch (Exception ex)
		    {
		    	ex.printStackTrace();
		    }
	    }
	  }

public String readPacket(){
	return this.data;
}
}