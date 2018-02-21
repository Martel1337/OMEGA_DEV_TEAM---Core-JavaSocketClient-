package me.ByteCoder.Core.JCController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class BC_Console {

private Process prc;
private BufferedReader reader;	
private PrintWriter stdin;

public BC_Console() {
	try {
		this.prc = new ProcessBuilder("cmd.exe", "/c", "cmd").start();
		this.reader = new BufferedReader(new InputStreamReader(this.prc.getInputStream()));
		this.stdin = new PrintWriter(this.prc.getOutputStream());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public BufferedReader getReader() {
	return this.reader;
}

public Process getProcess() {
	return this.prc;
}

public void println(String s) {
	this.stdin.println(s);
	this.stdin.flush();
}

public String executeCommand(String command) {

	StringBuffer output = new StringBuffer();

	Process p;
	try {
		p = Runtime.getRuntime().exec("cmd.exe /c " + command);
		BufferedReader reader =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));

                    String line = "";
		while ((line = reader.readLine())!= null) {
			output.append(line + "\n");
		}

	} catch (Exception e) {
		e.printStackTrace();
	}

	return output.toString();

}

public String readLine() {
	String s = null;
	try {
		s = this.reader.readLine();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return s;
}
}
