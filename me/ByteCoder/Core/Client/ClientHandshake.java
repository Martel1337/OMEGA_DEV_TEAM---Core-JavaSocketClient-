package me.ByteCoder.Core.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import me.ByteCoder.Core.JCController.Main;

public class ClientHandshake {

private Socket socket;
private DataOutputStream out;
private DataInputStream in;

public ClientHandshake(String name, String format) {
	try {
        this.socket = new Socket(Main.c.getIP(), 8888);
        this.out = new DataOutputStream(this.socket.getOutputStream());
        this.in = new DataInputStream(this.socket.getInputStream());
        out.writeUTF("JavaClientFileHandshake");
        out.writeUTF(Main.c.getName());
        out.writeUTF(Main.c.getPassword());
        out.flush();
        
        out.writeUTF(name);
        out.writeUTF(format);
        out.flush();
    }
    catch (IOException e) {
    	e.printStackTrace();
    	System.out.println("Not connected to core!");
    }
}

public ClientHandshake sendFile(File file) {
	try {
	DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());
    FileInputStream fis = new FileInputStream(file);
    byte[] buffer = new byte[16 * 1024];

    while ((fis.read(buffer) > 0)) {
        dos.write(buffer);
    }

    fis.close();
    dos.close();    
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return this;
}

public void close() {
	try {
		this.in.close();
		this.out.close();
		this.socket.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
