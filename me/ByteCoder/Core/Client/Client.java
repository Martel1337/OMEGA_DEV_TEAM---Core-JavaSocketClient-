package me.ByteCoder.Core.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

import me.ByteCoder.Core.JCController.Main;

public class Client {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private ExecutorService service;
    private boolean isConn;
    private String cname;
    private String ip;
    private int port;
    private String cversion;
    public boolean fTarnsfer;
    private String pass;
    private String ckey;
    private ReentrantLock lock;
    private boolean reconecting;
    private InetSocketAddress address;
    
public Client(ExecutorService servc, String password){
	this.service = servc;
	this.pass = password;
}

    public void init(String a, int p) {
        this.ip = a;
        this.port = p;
        this.address = new InetSocketAddress(this.ip, this.port);
        try {
            this.reconecting = false;
            System.out.println("Tryining connect to core!");
            this.socket = new Socket(a, p);
            this.lock = new ReentrantLock();
            this.cname = "Client-" + System.getProperty("user.name");
            this.out = new DataOutputStream(this.socket.getOutputStream());
            this.in = new DataInputStream(this.socket.getInputStream());
            this.out.writeUTF("JavaClient");
            this.out.writeUTF(this.cname);
            this.out.writeUTF(this.pass);
            this.out.flush();
            this.out.writeUTF(getData());
            this.out.flush();
            System.out.println("Connected to the core!");
            this.ckey = this.in.readUTF();
            System.out.println("Key installed: " + ckey);
            this.isConn = true;
            this.readPacket();
        }
        catch (IOException e) {
        	System.out.println("Not connected to core!");
        	this.isConn = false;
            this.startReconnect();
        }
    }

    public void setName(String s) {
    	this.cname = s;
    }
    
    public ExecutorService getService() {
    	return this.service;
    }
    
    public String getIP() {
    	return this.ip;
    }
    
    public String getPassword() {
    	return this.pass;
    }
    
    public void reconnect() {
    	try {
    		this.socket = new Socket(this.ip, this.port);
	        this.isConn = true;
	    	this.out = new DataOutputStream(this.socket.getOutputStream());
	        this.in = new DataInputStream(this.socket.getInputStream());
	        this.out.writeUTF("JavaClient");
	        this.out.writeUTF(this.cname);
	        this.out.writeUTF(this.pass);
	        this.out.flush();
	        this.out.writeUTF(getData());
	        this.out.flush();
	        System.out.println("Connected to the core!");
	        this.ckey = this.in.readUTF();
	        System.out.println("Key installed: " + ckey);
    	} catch (IOException e) {
    		this.isConn = false;
    		System.out.println("Not connected to core!");
    	}
    }
    
    public void end() {
        try {
        	if(this.isConn) {
        		socket.close();
        	}
            System.out.println("Ending...");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.isConn = false;
    }

    public String getName() {
    	return cname;
    }
    
    public boolean isConncted(){
    	return this.isConn;
    }
    
    public String getCVersion() {
    	return this.cversion;
    }
    
    public DataOutputStream getOut(){
    	return this.out;
    }
    
    public DataInputStream getIn(){
    	return this.in;
    }
    
    public Socket getSocket(){
    	return this.socket;
    }
    
	private void readPacket() {
		this.service.submit(() -> {
			try {
			while(!this.socket.isClosed()) {
				if(this.fTarnsfer == false) {
						String MessageType = this.in.readUTF();
						String MessageAction = this.in.readUTF();
						String Data = this.in.readUTF();
						String Target = this.in.readUTF();
					    
						new ClientMessageReceive(SCryptor.aesDecryptString(MessageType, Main.getKey()), SCryptor.aesDecryptString(MessageAction, Main.getKey()), SCryptor.aesDecryptString(Data, Main.getKey()), SCryptor.aesDecryptString(Target, Main.getKey()), this.in, this.out).run();
					}
				}
			}catch (IOException e) {
				this.startReconnect();
			}
		});
	}
	
	public void startReconnect() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while(!Main.c.isConn) {
					System.out.println("Reconnecting to core...");
					reconnect();
				}
				reconecting = false;
			}
		}).start();
	}
	
	public void startFileReadPacket(String directory, String fname) {
		this.fTarnsfer = true;
		try {
			File dir = new File(directory);
			dir.mkdirs();
			FileOutputStream fos = new FileOutputStream(dir.toPath().toString() + File.separatorChar + fname);
			File f = new File(dir.toPath().toString() + File.separatorChar + fname);
			DataInputStream data = this.in;
			System.out.println("Starting File update...");
			byte[] buffer = new byte[16*1024];
		 
		 	int filesize = Integer.valueOf(String.valueOf(this.in.readLong()));
			int read = 0;
			int remaining = filesize;
			int loaded = 0;
			boolean finished = true;
			while(finished && (read = data.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
				remaining -= read;
				loaded += read;
				System.out.println("Receiving bytes... (" + loaded + "/" + filesize + ")");
				if(f.length() == filesize) {
					finished = false;
				}else{
					fos.write(buffer, 0, read);
				}
			}
			
			fos.close();
			
			this.reconnect();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("File successfully updated!");
			this.fTarnsfer = false;
	}
	
    public void write(String MessageType, String MessageAction, String object, String target){
    	if(this.isConncted()) {
	    	try {
	    		this.lock.lock();
	    		
				String cType = SCryptor.aesEncryptString(MessageType, this.ckey);
				String cAction = SCryptor.aesEncryptString(MessageAction, this.ckey);
				String cData = SCryptor.aesEncryptString(object, this.ckey);
				String cTarget = SCryptor.aesEncryptString(target, this.ckey);
	    		
				this.out.writeBoolean(true);
	    		this.out.writeUTF(cType);
	    		this.out.writeUTF(cAction);
	    		this.out.writeUTF(cData);
	    		this.out.writeUTF(cTarget);
	    		this.out.flush();
	    		System.out.println("Message: Type " + MessageType + ". MessageAction: " + MessageAction + ". Data: " + object + ". Target: " + target + ". SENDET!");
			} catch (IOException e) {
				if(this.reconecting == false) {
					this.startReconnect();
				}
				this.lock.unlock();
			} finally {
				if(this.lock.isLocked()) {
					this.lock.unlock();
				}
			}
    	}
    }
    
    public void write(String data) {
    	if(this.isConncted()) {
    		try {
    			this.lock.lock();
    			
    			this.out.writeBoolean(false);
    			this.out.writeUTF(data);
    			
    		} catch (IOException e) {
    			if(this.reconecting == false) {
    				this.startReconnect();
    			}
    			this.lock.unlock();
    		} finally {
    			if(this.lock.isLocked()) {
    				this.lock.unlock();
    			}
    		}
    	}
    }
    
    public int getPort() {
    	return this.port;
    }
    
    public String getCryptorKey() {
    	return this.ckey;
    }
    
    public ReentrantLock getLocker() {
    	return this.lock;
    }
    
	public void writeFile(String file) {
		try {
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[4096];
		
		while (fis.read(buffer) > 0) {
			this.out.write(buffer);
		}
		
		fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
			this.startReconnect();
		}
	}
	
	public String getData() {
		return System.getProperty("user.name") + ";" + System.getProperty("os.name") + ";" + System.getProperty("os.version") + ";" + System.getProperty("java.version") + ";" + 2.0 + ";" + ManagementFactory.getRuntimeMXBean().getUptime();
	}
}

