package oing.java.reveng.srun3kclient.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

public class ConfigurationUtil {
	public static final String KEY_ACCOUNT = "account";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_MAC_ADDRESS = "mac_address";
	public static final String KEY_SERVER_IP = "server_ip";
	public static final String KEY_SAVE_CONFIG = "save_config";

	private static String mStrAppStartPath = null;
	private static Properties mProperties = new Properties();

	public static void load() {
		try {
			mProperties.load(new BufferedInputStream(new FileInputStream(new File(getApplicationStartPath(), "config.properties"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void save() {
		try {
			mProperties.store(new BufferedOutputStream(new FileOutputStream(new File(getApplicationStartPath(), "config.properties"))), "Srun3kClientGUI configuration file");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void put(String key, String value) {
		mProperties.put(key, value);
	}

	public static String get(String key) {
		return mProperties.getProperty(key, "");
	}

	private static String getApplicationStartPath() {
		if (mStrAppStartPath == null) {
			try {
				mStrAppStartPath = URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").getFile(), "utf8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			System.out.println("Application start path: " + mStrAppStartPath);
		}
		return mStrAppStartPath;
	}
}
