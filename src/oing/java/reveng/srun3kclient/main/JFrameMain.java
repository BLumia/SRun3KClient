package oing.java.reveng.srun3kclient.main;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import oing.java.reveng.srun3kclient.client.Srun3kClient;
import oing.java.reveng.srun3kclient.entity.HardwareAddressEntity;
import oing.java.reveng.srun3kclient.util.ConfigurationUtil;

public class JFrameMain extends JFrame {
	private static final long serialVersionUID = 399039662715871874L;

	private static JFrameMain mInstance = null;

	private SystemTray mSystemTray = null;
	private TrayIcon mTrayIconMain = null;
	private Srun3kClient mSrun3kClient = null;

	private JTextField mJTextFieldAccount = null;
	private JPasswordField mJPasswordFieldPassword = null;
	private JComboBox<HardwareAddressEntity> mJComboBoxMACAddress = null;
	private JTextField mJTextFieldServerIP = null;
	private JCheckBox mJCheckBoxGoSystemTray = null;
	private JButton mJButtonLogin = null;
	private JButton mJButtonLogout = null;
	private JLabel mJLabelStatus = null;

	private JFrameMain() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}

		initUI();
		initSystemTray();
		initListeners();
		try {
			initMACAddressList();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		loadConfiguration();

		setSize(270, 280);
		{
			// show main jframe in center of screen
			DisplayMode lDisplayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
			int lnLeft = lDisplayMode.getWidth() / 2 - getSize().width / 2;
			int lnTop = lDisplayMode.getHeight() / 2 - getSize().height / 2;
			setLocation(lnLeft, lnTop);
		}
		// setResizable(false);
		setTitle("Srun3kClientGUI");
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		mSrun3kClient = new Srun3kClient(new Srun3kClientActionListener());
	}

	public static JFrameMain getInstance() {
		if (mInstance == null) {
			mInstance = new JFrameMain();
		}
		return mInstance;
	}

	private void initUI() {
		GridBagConstraints lGridBagConstraints = new GridBagConstraints();
		Container lContainer = getContentPane();
		setLayout(new GridBagLayout());

		setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("srun3k_original_icon.png")));

		JLabel lJLabelAccount = new JLabel("Account:", SwingConstants.RIGHT);
		lGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		lGridBagConstraints.gridx = 0;
		lGridBagConstraints.gridy = 0;
		lGridBagConstraints.weightx = 0.3;
		lGridBagConstraints.insets = new Insets(3, 3, 0, 3);
		lContainer.add(lJLabelAccount, lGridBagConstraints);

		mJTextFieldAccount = new JTextField();
		lGridBagConstraints.gridx = 1;
		lGridBagConstraints.weightx = 0.7;
		lContainer.add(mJTextFieldAccount, lGridBagConstraints);

		JLabel lJLabelPassword = new JLabel("Password:", SwingConstants.RIGHT);
		lGridBagConstraints.gridx = 0;
		lGridBagConstraints.gridy = 1;
		lGridBagConstraints.weightx = 0.3;
		lContainer.add(lJLabelPassword, lGridBagConstraints);

		mJPasswordFieldPassword = new JPasswordField();
		lGridBagConstraints.gridx = 1;
		lGridBagConstraints.weightx = 0.7;
		lContainer.add(mJPasswordFieldPassword, lGridBagConstraints);

		JLabel lJLabelLocalMACAddress = new JLabel("MAC Address:");
		lJLabelLocalMACAddress.setHorizontalAlignment(SwingConstants.RIGHT);
		lGridBagConstraints.gridx = 0;
		lGridBagConstraints.gridy = 2;
		lGridBagConstraints.weightx = 0.3;
		lContainer.add(lJLabelLocalMACAddress, lGridBagConstraints);

		mJComboBoxMACAddress = new JComboBox<HardwareAddressEntity>();
		mJComboBoxMACAddress.setPreferredSize(new Dimension(128, 26));
		mJComboBoxMACAddress.setMaximumSize(mJComboBoxMACAddress.getPreferredSize());
		mJComboBoxMACAddress.setEditable(true);
		lGridBagConstraints.gridx = 1;
		lGridBagConstraints.weightx = 0.7;
		lContainer.add(mJComboBoxMACAddress, lGridBagConstraints);

		JLabel lJLabelServerIP = new JLabel("Server IP:");
		lJLabelServerIP.setHorizontalAlignment(SwingConstants.RIGHT);
		lGridBagConstraints.gridx = 0;
		lGridBagConstraints.gridy = 3;
		lGridBagConstraints.weightx = 0.3;
		lContainer.add(lJLabelServerIP, lGridBagConstraints);

		mJTextFieldServerIP = new JTextField();
		lGridBagConstraints.gridx = 1;
		lGridBagConstraints.weightx = 0.7;
		lContainer.add(mJTextFieldServerIP, lGridBagConstraints);

		JPanel lJPanelButtom = new JPanel(new GridBagLayout());
		lJPanelButtom.setBackground(new Color(lContainer.getBackground().getRGB()));
		lGridBagConstraints.gridx = 0;
		lGridBagConstraints.gridy = 4;
		lGridBagConstraints.gridwidth = 2;
		lGridBagConstraints.anchor = GridBagConstraints.PAGE_START;
		lContainer.add(lJPanelButtom, lGridBagConstraints);

		lGridBagConstraints.gridwidth = 1;
		mJCheckBoxGoSystemTray = new JCheckBox("Go system tray when closing window");
		lGridBagConstraints.gridx = 0;
		lGridBagConstraints.gridy = 0;
		lGridBagConstraints.weightx = 0;
		lGridBagConstraints.gridwidth = 2;
		lJPanelButtom.add(mJCheckBoxGoSystemTray, lGridBagConstraints);
		// 当系统托盘可用，则启用该按钮并设置已勾选，否则禁用并取消勾选
		mJCheckBoxGoSystemTray.setEnabled(SystemTray.isSupported());
		mJCheckBoxGoSystemTray.setSelected(mJCheckBoxGoSystemTray.isEnabled());

		mJButtonLogin = new JButton("Login");
		lGridBagConstraints.gridx = 0;
		lGridBagConstraints.gridy = 1;
		lGridBagConstraints.gridwidth = 1;
		lGridBagConstraints.weightx = 0.5;
		lJPanelButtom.add(mJButtonLogin, lGridBagConstraints);

		mJButtonLogout = new JButton("Logout");
		lGridBagConstraints.gridx = 1;
		lJPanelButtom.add(mJButtonLogout, lGridBagConstraints);

		mJLabelStatus = new JLabel("Ready");
		mJLabelStatus.setHorizontalAlignment(SwingConstants.CENTER);
		mJLabelStatus.setPreferredSize(new Dimension(128, 26));
		lGridBagConstraints.anchor = GridBagConstraints.CENTER;
		lGridBagConstraints.gridx = 0;
		lGridBagConstraints.gridy = 2;
		lGridBagConstraints.gridwidth = 2;
		lGridBagConstraints.insets = new Insets(0, 0, 0, 0);
		lJPanelButtom.add(mJLabelStatus, lGridBagConstraints);

		lGridBagConstraints.gridy = 5;
		lGridBagConstraints.anchor = GridBagConstraints.PAGE_END;
		JLabel lJLabelAuthor = new JLabel("Srun3kClientGUI by oing9179");
		lJLabelAuthor.setHorizontalAlignment(SwingConstants.CENTER);
		lGridBagConstraints.gridx = 0;
		lGridBagConstraints.gridwidth = 2;
		lGridBagConstraints.weighty = 1;
		lGridBagConstraints.insets = new Insets(0, 0, 3, 0);
		lContainer.add(lJLabelAuthor, lGridBagConstraints);
	}

	private void initSystemTray() {
		if (SystemTray.isSupported()) {
			mSystemTray = SystemTray.getSystemTray();
			mTrayIconMain = new TrayIcon(this.getIconImage(), "Srun3KClientGUI by oing9179");
			mTrayIconMain.setImageAutoSize(true);
			try {
				mSystemTray.add(mTrayIconMain);
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}

	private void initListeners() {
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				this_windowClosing(e);
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		mTrayIconMain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = JFrameMain.getInstance();
				frame.setVisible(!frame.isVisible());
			}
		});
		mJButtonLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mJButtonLogin_actionPerformed(e);
			}
		});
		mJButtonLogout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mJButtonLogout_actionPerformed(e);
			}
		});
		mJComboBoxMACAddress.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				mJComboBoxMACAddress_itemStateChanged(e);
			}
		});
	}

	private void initMACAddressList() throws SocketException {
		HardwareAddressEntity[] lHardwareAddressEntitiesArr = HardwareAddressEntity.getAvailable();
		for (int i = 0; i < lHardwareAddressEntitiesArr.length; i++) {
			mJComboBoxMACAddress.addItem(lHardwareAddressEntitiesArr[i]);
		}
	}

	private void loadConfiguration() {
		ConfigurationUtil.load();
		mJTextFieldAccount.setText(ConfigurationUtil.get(ConfigurationUtil.KEY_ACCOUNT));
		mJPasswordFieldPassword.setText(ConfigurationUtil.get(ConfigurationUtil.KEY_PASSWORD));
		mJComboBoxMACAddress.setSelectedItem(ConfigurationUtil.get(ConfigurationUtil.KEY_MAC_ADDRESS));
		mJTextFieldServerIP.setText(ConfigurationUtil.get(ConfigurationUtil.KEY_SERVER_IP));
	}

	private void mJButtonLogin_actionPerformed(ActionEvent e) {
		String lStrMACAddress = null;
		Object lObjSelectedItem = mJComboBoxMACAddress.getSelectedItem();

		if (lObjSelectedItem.getClass().equals(HardwareAddressEntity.class)) {
			lStrMACAddress = ((HardwareAddressEntity) lObjSelectedItem).getMacAddress();
		} else {
			lStrMACAddress = lObjSelectedItem.toString();
		}

		mSrun3kClient.loginAsync(mJTextFieldAccount.getText(), new String(mJPasswordFieldPassword.getPassword()), lStrMACAddress, mJTextFieldServerIP.getText());
		mJLabelStatus.setText("Logging in...");

		ConfigurationUtil.put(ConfigurationUtil.KEY_ACCOUNT, mJTextFieldAccount.getText());
		ConfigurationUtil.put(ConfigurationUtil.KEY_PASSWORD, new String(mJPasswordFieldPassword.getPassword()));
		ConfigurationUtil.put(ConfigurationUtil.KEY_MAC_ADDRESS, lStrMACAddress);
		ConfigurationUtil.put(ConfigurationUtil.KEY_SERVER_IP, mJTextFieldServerIP.getText());
		ConfigurationUtil.save();
	}

	private void mJButtonLogout_actionPerformed(ActionEvent e) {
		mSrun3kClient.logoutAsync();
		mJLabelStatus.setText("Logging out...");
	}

	private void mJComboBoxMACAddress_itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			mJComboBoxMACAddress.setToolTipText(e.getItem().toString());
			// 寻找在combobox里填写的和HardwareAddressEntity的mac地址一样的项目，并设为选中。
			for (int i = 0; i < mJComboBoxMACAddress.getItemCount(); i++) {
				HardwareAddressEntity lHardwareAddressEntity = mJComboBoxMACAddress.getItemAt(i);
				if (e.getItem().toString().equals(lHardwareAddressEntity.getMacAddress())) {
					mJComboBoxMACAddress.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	private void this_windowClosing(WindowEvent e) {
		if (mSrun3kClient.isLoggedIn() && mJCheckBoxGoSystemTray.isSelected()) {
			this.setVisible(false);
		} else {
			dispose();
			System.exit(0);
		}
	}

	@Override
	public void dispose() {
		TrayIcon[] lTrayIconsArr = mSystemTray.getTrayIcons();

		if (mSrun3kClient.isLoggedIn()) {
			mSrun3kClient.logoutAsync();
		}
		// remove all TrayIcons
		for (int i = 0; i < lTrayIconsArr.length; i++) {
			mSystemTray.remove(lTrayIconsArr[i]);
		}
		super.dispose();
	}

	private class Srun3kClientActionListener implements Srun3kClient.ActionListener {
		@Override
		public void onLogin(boolean lbSuccess, String lStrCause) {
			if (lbSuccess) {
				mJLabelStatus.setText("Logged in.");
			} else {
				mJLabelStatus.setText("Login failed, " + lStrCause);
			}
			mJLabelStatus.setToolTipText(mJLabelStatus.getText());
		}

		@Override
		public void onKeepAlive() {
		}

		@Override
		public void onLogout(String lStrCause) {
			if (lStrCause == null) {
				mJLabelStatus.setText("Logged out.");
			} else {
				mJLabelStatus.setText("Logout failed. " + lStrCause);
			}
			mJLabelStatus.setToolTipText(mJLabelStatus.getText());
		}
	}

	public static void main(String[] args) {
		JFrameMain.getInstance();
	}
}
