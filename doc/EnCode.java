private String EnCode(String lStrCode/* p1 */, String lStrMykey/* p2 */)
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