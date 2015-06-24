package oing.java.reveng.srun3kclient.main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import oing.java.reveng.srun3kclient.client.Srun3kClient;

public class Main {
	public final static String VERSION = "0.2.3";

	private final static int CLIENTSTATE_OFFLINE = 0;
	private final static int CLIENTSTATE_LOGGING_IN = 1;
	private final static int CLIENTSTATE_LOGGED_IN = 2;
	private final static int CLIENTSTATE_LOGGING_OUT = 3;

	private int mnClientState = CLIENTSTATE_OFFLINE;
	private long mlKeepAliveTimes = 0;

	public static void main(String[] args) {
		new Main()._main(args);
	}

	private void _main(String[] args) {
		Scanner lScannerCIN = null;
		Srun3kClient lSrun3kClient = null;
		long llTimeAtConnected = System.currentTimeMillis();

		System.out.println("Srun3kClient by oing9179, version " + VERSION);
		if (args != null && args.length == 1 && args[0].equals("-gui")) {
			JFrameMain.getInstance();
			return;
		}
		if (args != null && args.length != 4) {
			System.out.println("Usage: Srun3kClient.jar -gui");
			System.out.println("\tStart a GUI client");
			System.out.println("Usage: Srun3kClient.jar <UserName> <Password> <LocalMACAddress> <ServerIP>");
			System.out.println("\tDo login directly");
			return;
		}
		// Start login
		lSrun3kClient = new Srun3kClient(new Srun3kClientActionListener(this));
		mnClientState = CLIENTSTATE_LOGGING_IN;
		lSrun3kClient.loginAsync(args[0], args[1], args[2], args[3]);
		System.out.println("Waiting for login response...");
		while (mnClientState != CLIENTSTATE_LOGGED_IN) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (mnClientState == CLIENTSTATE_OFFLINE) {
				System.out.println("Login failed.");
				return;
			}
		}
		lSrun3kClient.startKeepAlive();
		lScannerCIN = new Scanner(System.in);
		String lStrCIN = null;

		System.out.println("Login success.");
		System.out.println("Help: i: Generic information. q: Disconnect and quit");
		do {
			lStrCIN = lScannerCIN.nextLine();
			switch (lStrCIN) {
			case "i": {
				StringBuilder lStringBuilder = new StringBuilder();
				lStringBuilder.append("Connected at " + SimpleDateFormat.getDateTimeInstance().format(new Date(llTimeAtConnected)) + "\n");
				lStringBuilder.append("UserName: " + args[0] + "\n");
				lStringBuilder.append("Password: " + args[1].replaceAll("\\S", "*") + "\n");
				lStringBuilder.append("KeptAlive: " + mlKeepAliveTimes + "\n");
				System.out.println(lStringBuilder.toString());
			}
				break;
			case "q":
				mnClientState = CLIENTSTATE_LOGGING_OUT;
				lSrun3kClient.logoutAsync();
				System.out.println("Waiting for logout response...");
				while (mnClientState == CLIENTSTATE_OFFLINE) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("Logged out.");
				break;
			default:
				System.out.println("Unknown command, i: Generic information, q: Disconnect and quit");
				break;
			}
		} while (lStrCIN.equals("q") == false);
		lScannerCIN.close();
		System.out.println("Srun3kClient terminated.");
	}

	private class Srun3kClientActionListener implements Srun3kClient.ActionListener {
		private Main mSuperclassInstance = null;

		public Srun3kClientActionListener(Main instance) {
			mSuperclassInstance = instance;
		}

		@Override
		public void onLogin(boolean lbSuccess, String lStrCause) {
			if (lbSuccess) {
				mSuperclassInstance.mnClientState = Main.CLIENTSTATE_LOGGED_IN;
			} else {
				System.out.println("Failed to login: " + lStrCause);
			}
		}

		@Override
		public void onKeepAlive() {
			mSuperclassInstance.mlKeepAliveTimes++;
		}

		@Override
		public void onLogout(String lStrCause) {
			mSuperclassInstance.mnClientState = Main.CLIENTSTATE_OFFLINE;
			mSuperclassInstance.mlKeepAliveTimes = 0;
			if (lStrCause != null) {
				System.out.println("Logged out with error: " + lStrCause);
			}
		}
	}
}
