package oing.java.reveng.srun3kclient.entity;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class HardwareAddressEntity {
	private NetworkInterface networkInterface = null;
	private String displayName = null;
	private String macAddress = null;

	private HardwareAddressEntity() {
	}

	private HardwareAddressEntity(NetworkInterface networkInterface) throws SocketException {
		this.networkInterface = networkInterface;
		this.displayName = networkInterface.getDisplayName();
		this.macAddress = "";

		byte[] lbArrHardwareAddress = networkInterface.getHardwareAddress();
		for (int i = 0; i < lbArrHardwareAddress.length; i++) {
			this.macAddress += Integer.toString(lbArrHardwareAddress[i] < 0 ? lbArrHardwareAddress[i] + 256 : lbArrHardwareAddress[i], 16) + ":";
		}
		this.macAddress = this.macAddress.substring(0, this.macAddress.length() - 1);
	}

	public static HardwareAddressEntity[] getAvailable() throws SocketException {
		ArrayList<HardwareAddressEntity> lListEntities = new ArrayList<HardwareAddressEntity>();

		for (Enumeration<NetworkInterface> lEnumerationNetworkInterfaces = NetworkInterface.getNetworkInterfaces(); lEnumerationNetworkInterfaces.hasMoreElements();) {
			NetworkInterface lNetworkInterface = lEnumerationNetworkInterfaces.nextElement();
			if (lNetworkInterface.getHardwareAddress() != null) {
				lListEntities.add(new HardwareAddressEntity(lNetworkInterface));
			}
		}

		return lListEntities.toArray(new HardwareAddressEntity[] {});
	}

	public NetworkInterface getNetworkInterface() {
		return networkInterface;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getMacAddress() {
		return macAddress;
	}

	@Override
	public boolean equals(Object obj) {
		return networkInterface.equals(obj);
	}

	@Override
	public String toString() {
		return displayName + " [" + macAddress + "]";
	}
}
