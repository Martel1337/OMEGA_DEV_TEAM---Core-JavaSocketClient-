package me.ByteCoder.Core.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import me.ByteCoder.Core.Client.Event.Event;
import me.ByteCoder.Core.JCController.Main;

public class ClientMessageReceive extends Event {

private String MessageAction;
private String MessageType;
private String Target;
private Object data;
private DataOutputStream out;
private DataInputStream in;

public ClientMessageReceive(String MessageType, String MessageAction, Object m, String t, DataInputStream input, DataOutputStream output){
	this.MessageAction = MessageAction;
	this.MessageType = MessageType;
	this.data = m;
	this.Target = t;
	this.out = output;
	this.in = input;
}

public DataInputStream getInput(){
	return this.in;
}

public DataOutputStream getOutput(){
	return this.out;
}

public String getMessageType(){
	return this.MessageType;
}

public String getMessageAction(){
	return this.MessageAction;
}

public String getTarget(){
	return this.Target;
}

public Object getData(){
	return this.data;
}

@Override
public Event run() {
	Main.em.callEvent(this);
	return this;
}
}
