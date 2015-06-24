package oing.java.reveng.srun3kclient.localization;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ResourceBundle;

public class LocalizationMessageBundle {
	public static final String KEY_account = "account";
	public static final String KEY_password = "password";
	public static final String KEY_mac_address = "mac_address";
	public static final String KEY_server_ip = "server_ip";
	public static final String KEY_save_config_password_etc = "save_config_password_etc";
	public static final String KEY_go_system_tray = "go_system_tray";
	public static final String KEY_login = "login";
	public static final String KEY_logout = "logout";
	public static final String KEY_ready = "ready";
	public static final String KEY_logging_in = "logging_in";
	public static final String KEY_logging_out = "logging_out";
	public static final String KEY_logged_in = "logged_in";
	public static final String KEY_logged_out = "logged_out";
	public static final String KEY_login_failed = "login_failed";
	public static final String KEY_logout_failed = "logout_failed";

	public static final String MESSAGE_account = new String();
	public static final String MESSAGE_password = new String();
	public static final String MESSAGE_mac_address = new String();
	public static final String MESSAGE_server_ip = new String();
	public static final String MESSAGE_save_config_password_etc = new String();
	public static final String MESSAGE_go_system_tray = new String();
	public static final String MESSAGE_login = new String();
	public static final String MESSAGE_logout = new String();
	public static final String MESSAGE_ready = new String();
	public static final String MESSAGE_logging_in = new String();
	public static final String MESSAGE_logging_out = new String();
	public static final String MESSAGE_logged_in = new String();
	public static final String MESSAGE_logged_out = new String();
	public static final String MESSAGE_login_failed = new String();
	public static final String MESSAGE_logout_failed = new String();

	private static ResourceBundle mResourceBundle = null;

	public static void loadDefault() {
		mResourceBundle = ResourceBundle.getBundle("oing.java.reveng.srun3kclient.localization.DefaultLocalizationMessageBundle");

		try {
			// 载入所有本地化消息，并使用反射设置到 static final 的 MESSAGE_ 开头的字段上。
			Field[] lFieldsArrKeys = LocalizationMessageBundle.class.getFields();

			for (int i = 0; i < lFieldsArrKeys.length; i++) {
				Field lFieldKey = lFieldsArrKeys[i];
				String lStrFieldName_key = lFieldKey.getName();

				if (lStrFieldName_key.startsWith("KEY_")) {
					String lStrFieldName_message = "MESSAGE_" + lStrFieldName_key.substring(lStrFieldName_key.indexOf('_') + 1, lStrFieldName_key.length());
					Field lFieldMessage = LocalizationMessageBundle.class.getField(lStrFieldName_message);
					setFinalStatic(lFieldMessage, getString(lFieldKey.get(null).toString()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getString(String key) {
		String lStrMessage = null;

		try {
			lStrMessage = new String(mResourceBundle.getString(key).getBytes("ISO-8859-1"), "UTF8");
		} catch (UnsupportedEncodingException e) {
			lStrMessage = mResourceBundle.getString(key);
		}

		return lStrMessage;
	}

	/**
	 * 设置带有 static final 修饰词的字段的值
	 */
	private static void setFinalStatic(Field field, Object newValue) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field lFieldModifiers = Field.class.getDeclaredField("modifiers");

		field.setAccessible(true);
		lFieldModifiers.setAccessible(true);
		lFieldModifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, newValue);
	}
}
