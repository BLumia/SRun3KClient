package oing.java.reveng.srun3kclient.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * 深澜认证客户端 Srun3k
 * 
 * @author oing9179
 */
public class Srun3kClient
{
	/**
	 * 认证模式 Win32_Normal，默认的认证模式。
	 */
	public final static int AUTHMODE_WIN32_NORMAL = 0;
	/**
	 * 认证模式 Win32_RADIUS
	 */
	public final static int AUTHMODE_WIN32_RADIUS = 1;
	/**
	 * 认证模式 Mobile_Portal
	 */
	public final static int AUTHMODE_MOBILE_PORTAL = 2;
	
	private int mnAuthMode = AUTHMODE_WIN32_NORMAL;// The default AuthMode
	private ActionListener mActionListener = null;
	private String mStrUserName = null;
	private String mStrLocalMACAddress = null;
	private String mStrServerIP = null;
	private long mlUserID = 0;
	private ThreadKeepAlive mThreadKeepAlive = null;
	
	public Srun3kClient(ActionListener lActionListener)
	{
		this.mActionListener = lActionListener;
	}
	
	public void loginAsync(String lStrUserName, String lStrPassword, String lStrLocalMACAddress, String lStrServerIP)
	{
		this.mStrUserName = lStrUserName;
		this.mStrServerIP = lStrServerIP;
		new ThreadLogin(lStrUserName, lStrPassword, mnAuthMode, lStrLocalMACAddress, lStrServerIP, this).start();
	}
	
	public void logoutAsync()
	{
		stopKeepAlive();
		new ThreadLogout(mlUserID, mStrUserName, mnAuthMode, mStrLocalMACAddress, mStrServerIP, this).start();
	}
	
	public synchronized void startKeepAlive()
	{
		if(mThreadKeepAlive == null || (mThreadKeepAlive != null && mThreadKeepAlive.getState() == Thread.State.TERMINATED))
		{
			mThreadKeepAlive = new ThreadKeepAlive(mlUserID, mStrServerIP, this);
			mThreadKeepAlive.start();
		}
	}
	
	public synchronized void stopKeepAlive()
	{
		if(mThreadKeepAlive != null && mThreadKeepAlive.getState() == Thread.State.RUNNABLE)
		{
			mThreadKeepAlive.interrupt();
		}
	}
	
	/**
	 * 生成动态key
	 * 
	 * @param lnTimeOffsetSec
	 *            如果本地时间与服务器有时间差，则使用该值填补，单位秒。
	 */
	private static String genMykey(int lnTimeOffsetSec)
	{
		return (new Date().getTime() / 1000 - lnTimeOffsetSec) / 60 + "";
	}
	
	/**
	 * 加密lStrCode，key是lStrMykey。别问我方法名为何叫这个，Android客户端就这么叫的。
	 */
	private static String EnCode(String lStrCode/* p1 */, String lStrMykey/* p2 */)
	{
		int v3 = 0, v4 = 0, v5 = 0;
		byte v1 = 0, v2 = 0, v7 = 0;
		
		int v8 = lStrMykey.length();
		int v9 = lStrCode.length();
		
		v4 = v8 - 0x1;
		byte[] v10 = new byte[0x100];// 0x100=256
		byte[] v0 = lStrCode.getBytes();
		byte[] v6 = lStrMykey.getBytes();
		v5 = 0;
		while(v5 < v9)
		{
			v1 = (byte)(v0[v5] ^ v6[v4]);
			v2 = (byte)((v1 >> 0x4) & 0xf);
			v7 = (byte)(v1 & 0xf);
			v2 = (byte)(v2 + 0x63);
			v7 = (byte)(v7 + 0x36);
			v10[v3] = v4 % 0x2 <= 0 ? v2 : v7;
			v3 += 0x1;
			v10[v3] = v4 % 0x2 <= 0 ? v7 : v2;
			v3 += 0x1;
			v4 -= 0x1;
			if(v4 >= 0)
			{
				
			}
			else
			{
				v4 = v8 - 1;
			}
			v5 += 0x1;
		}
		v10[v3] = 0;
		return new String(v10, 0, v3);
	}
	
	/**
	 * 动作回调函数
	 */
	public interface ActionListener
	{
		/**
		 * 当登录成功或失败调用此回调函数
		 * 
		 * @param lbSuccess
		 *            登录是否成功
		 * @param lStrCause
		 *            登录失败原因，成功则为服务器返回的字符串。
		 */
		public void onLogin(boolean lbSuccess, String lStrCause);
		
		/**
		 * 每次发送KeepAlive请求后调用此回调函数
		 */
		public void onKeepAlive();
		
		/**
		 * 当登出时调用此回调函数
		 * 
		 * @param lStrReason
		 *            服务器返回的登出原因，null则为正常登出。
		 */
		public void onLogout(String lStrCause);
	}
	
	private class ThreadLogin extends Thread
	{
		private Srun3kClient mSrun3kClientInstance = null;
		private int mnAuthMode;
		private String mStrUserName;
		private String mStrPassword;
		private String mStrLocalMACAddress;
		private String mStrServerIP;
		
		public ThreadLogin(String lStrUserName, String lStrPassword, int lnAuthMode, String lStrLocalMACAddress, String lStrServerIP, Srun3kClient instance)
		{
			this.mnAuthMode = lnAuthMode;
			this.mStrUserName = lStrUserName;
			this.mStrPassword = lStrPassword;
			this.mStrLocalMACAddress = lStrLocalMACAddress;
			this.mStrServerIP = lStrServerIP;
			this.mSrun3kClientInstance = instance;
		}
		
		@Override
		public void run()
		{
			String lStrUrl = "http://" + this.mStrServerIP + "/cgi-bin/";
			String lStrMykey = null;
			
			try
			{
				// 拼接HTTP请求地址
				switch (this.mnAuthMode)
				{
					case Srun3kClient.AUTHMODE_WIN32_NORMAL:
						lStrUrl += "do_login?type=2";
						lStrMykey = Srun3kClient.genMykey(0);
						this.mStrPassword = URLEncoder.encode(Srun3kClient.EnCode(this.mStrPassword, lStrMykey), "gb2312");
						this.mStrLocalMACAddress = URLEncoder.encode(Srun3kClient.EnCode(this.mStrLocalMACAddress, lStrMykey), Charset.defaultCharset().name());
						break;
					case Srun3kClient.AUTHMODE_WIN32_RADIUS:
						lStrUrl += "do_radius_login?type=11";
						lStrMykey = "95387643";
						this.mStrPassword = URLEncoder.encode(Srun3kClient.EnCode(this.mStrPassword, lStrMykey), "gb2312");
						this.mStrLocalMACAddress = URLEncoder.encode(this.mStrLocalMACAddress, Charset.defaultCharset().name());
						break;
					case Srun3kClient.AUTHMODE_MOBILE_PORTAL:
						lStrUrl += "srun_portal?type=1&action=login";
						// lStrMykey = "1234567890";
						// No needed to change lStrMykey cause useless. For more details at "BuildAuthCmd.txt" line "Mobile_srunPortal".
						this.mStrLocalMACAddress = URLEncoder.encode(this.mStrLocalMACAddress, Charset.defaultCharset().name());
						break;
					default:
						throw new IllegalArgumentException("Unknown auth mode " + this.mnAuthMode);
				}
				lStrUrl += "&username=" + this.mStrUserName;
				lStrUrl += "&password=" + this.mStrPassword;
				lStrUrl += "&drop=0";// Default drop string is "0", For more details at "BuildAuthCmd.txt" line "m_drop_str"
				lStrUrl += "&n=100";// Hard coded be cause the original does.
				lStrUrl += "&mac=" + this.mStrLocalMACAddress;
				if(this.mnAuthMode == Srun3kClient.AUTHMODE_MOBILE_PORTAL)
				{
					lStrUrl += "&ac_id=6";// The default_ac_id is 0x6.
				}
				// 向服务器请求
				URL lUrl = new URL(lStrUrl);
				HttpURLConnection lHttpURLConnection = (HttpURLConnection)lUrl.openConnection();
				ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
				byte[] lByteArrBuffer = new byte[1024];// 1KiB
				int lnReadedBytes = -1;
				
				lHttpURLConnection.setRequestMethod("POST");
				lHttpURLConnection.setConnectTimeout(3000);
				lHttpURLConnection.setReadTimeout(3000);
				lHttpURLConnection.connect();
				InputStream lInputStream = lHttpURLConnection.getInputStream();
				while((lnReadedBytes = lInputStream.read(lByteArrBuffer)) != -1)
				{
					lByteArrayOutputStream.write(lByteArrBuffer, 0, lnReadedBytes);
				}
				lHttpURLConnection.disconnect();
				lByteArrBuffer = lByteArrayOutputStream.toByteArray();
				String lStrHttpResponse = new String(lByteArrBuffer, 0, lByteArrBuffer.length, "UTF-8");
				lByteArrayOutputStream.close();
				String[] lStrArrSplitedHttpResponse = lStrHttpResponse.split("[,]");
				if(lStrArrSplitedHttpResponse.length == 5)// Login success, [0] is uid.
				{
					this.mSrun3kClientInstance.mlUserID = Long.parseLong(lStrArrSplitedHttpResponse[0]);
					this.mSrun3kClientInstance.mActionListener.onLogin(true, lStrHttpResponse);
				}
				else
				{
					this.mSrun3kClientInstance.mActionListener.onLogin(false, lStrHttpResponse);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				this.mSrun3kClientInstance.mActionListener.onLogin(false, e.toString());
			}
		}
	}
	
	private class ThreadKeepAlive extends Thread
	{
		private Srun3kClient mSrun3kClientInstance = null;
		private long mlUserID = -1;
		private String mStrServerIP = null;
		
		public ThreadKeepAlive(long llUserID, String lStrServerIP, Srun3kClient instance)
		{
			this.mlUserID = llUserID;
			this.mStrServerIP = lStrServerIP;
			this.mSrun3kClientInstance = instance;
		}
		
		@Override
		public void run()
		{
			while(this.isInterrupted() == false)
			{
				try
				{
					ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
					DataOutputStream lOutputStream = new DataOutputStream(lByteArrayOutputStream);
					lOutputStream.writeLong(Long.reverseBytes(this.mlUserID));
					lOutputStream.writeLong(Long.reverseBytes(1));
					lOutputStream.writeLong(0);
					lOutputStream.writeLong(0);
					lOutputStream.writeLong(0);
					lOutputStream.writeLong(0);
					lOutputStream.close();
					DatagramSocket lDatagramSocket = new DatagramSocket();
					InetAddress address = InetAddress.getByName(this.mStrServerIP);
					lByteArrayOutputStream.toByteArray();
					DatagramPacket lDatagramPacket = new DatagramPacket(lByteArrayOutputStream.toByteArray(), 8, address, 0xd07);// 0xd07=3335
					lDatagramSocket.send(lDatagramPacket);
					lByteArrayOutputStream.close();
					lDatagramSocket.close();
					Thread.sleep(30000);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					this.mSrun3kClientInstance.mActionListener.onKeepAlive();
				}
			}
		}
	}
	
	private class ThreadLogout extends Thread
	{
		private long mlUserID;
		private String mStrUserName;
		private int mnAuthMode;
		private String mStrLocalMACAddress;
		private String mStrServerIP;
		private Srun3kClient mSrun3kClientInstance;
		
		public ThreadLogout(long llUserID, String lStrUserName, int lnAuthMode, String lStrLocalMACAddress, String lStrServerIP, Srun3kClient instance)
		{
			this.mlUserID = llUserID;
			this.mStrUserName = lStrUserName;
			this.mnAuthMode = lnAuthMode;
			this.mStrLocalMACAddress = lStrLocalMACAddress;
			this.mStrServerIP = lStrServerIP;
			this.mSrun3kClientInstance = instance;
		}
		
		@Override
		public void run()
		{
			try
			{
				String lStrUrl = "http://" + this.mStrServerIP + "/cgi-bin/";
				
				switch (this.mnAuthMode)
				{
					case Srun3kClient.AUTHMODE_WIN32_NORMAL:
						lStrUrl += "do_logout?uid=" + this.mlUserID;
						break;
					case Srun3kClient.AUTHMODE_WIN32_RADIUS:
						lStrUrl += "do_radius_logout?uid=" + this.mlUserID;
						break;
					case Srun3kClient.AUTHMODE_MOBILE_PORTAL:
						lStrUrl += "srun_portal?type=1&action=logout";
						lStrUrl += "&ac_id=6";
						lStrUrl += "&username=" + this.mStrUserName;
						lStrUrl += "&mac=" + URLEncoder.encode(this.mStrLocalMACAddress, Charset.defaultCharset().name());
						break;
					default:
						throw new IllegalArgumentException("Unknown auth mode " + this.mnAuthMode);
				}
				URL lUrl = new URL(lStrUrl);
				HttpURLConnection lHttpURLConnection = (HttpURLConnection)lUrl.openConnection();
				ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
				byte[] lByteArrBuffer = new byte[1024];// 1KiB
				int lnReadedBytes = -1;
				
				lHttpURLConnection.setRequestMethod("POST");
				lHttpURLConnection.setConnectTimeout(3000);
				lHttpURLConnection.setReadTimeout(3000);
				lHttpURLConnection.connect();
				InputStream lInputStream = lHttpURLConnection.getInputStream();
				while((lnReadedBytes = lInputStream.read(lByteArrBuffer)) != -1)
				{
					lByteArrayOutputStream.write(lByteArrBuffer, 0, lnReadedBytes);
				}
				lHttpURLConnection.disconnect();
				lByteArrBuffer = lByteArrayOutputStream.toByteArray();
				String lStrHttpResponse = new String(lByteArrBuffer, 0, lByteArrBuffer.length, "UTF-8");
				lByteArrayOutputStream.close();
				if(lStrHttpResponse.equals("连接已断开") == true)
				{
					this.mSrun3kClientInstance.mActionListener.onLogout(null);
				}
				else
				{
					this.mSrun3kClientInstance.mActionListener.onLogout(lStrHttpResponse);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				this.mSrun3kClientInstance.mActionListener.onLogout(e.toString());
			}
		}
	}
}
