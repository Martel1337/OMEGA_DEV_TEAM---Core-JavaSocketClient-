package me.ByteCoder.Core.JCController;

public class SystemType {

	static String OS = System.getProperty("os.name");
	public static enum OSType { WIN, MAC, LINUX, SOLARIS, DEFAULT }
	
public static OSType getOSType() {
	OSType type;
	if (isWindows()) {
		type = OSType.WIN;
	} else if (isMac()) {
		type = OSType.MAC;
	} else if (isUnix()) {
		type = OSType.LINUX;
	} else if (isSolaris()) {
		type = OSType.SOLARIS;
	} else {
		type = OSType.DEFAULT;
	}
	return type;
}
	
	public static boolean isWindows() {

		return (OS.indexOf("win") >= 0);

	}

	public static boolean isMac() {

		return (OS.indexOf("mac") >= 0);

	}

	public static boolean isUnix() {

		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );

	}

	public static boolean isSolaris() {

		return (OS.indexOf("sunos") >= 0);

	}
	
}
