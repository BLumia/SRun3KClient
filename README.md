#SRun3K Java Client

###这是什么？

这是一个深澜校园网的java客户端程序。可以在装有[Jre](http://www.java.com/en/download/)的Windows，Mac OS和Linux等操作系统上运行。

###怎么用？

两种用法：

1. 通过终端/控制台
<pre><code>
	java -jar Srun3kClient.jar &lt;UserNameHere&gt; &lt;PasswordHere&gt; &lt;MAC Address Here&gt; &lt;ServerIP Here&gt;
	//e.g. java -jar Srun3kClient_v0.2.3.jar 144316090087 pasSw0rd E0:CA:5C:ED:FC:18 10.12.1.29
	//注：以上示例账号密码等信息均为瞎编的
</code></pre>
2. 通过图形化界面
<pre><code>
	java -jar Srun3kClient.jar -gui
</code></pre>

十分建议linux党自己写个sh开个tty用终端登录玩耍

###作者？

软件作者[oing9179](http://github.com/oing9179)。现已不打算继续维护。此repo上传已经经过原作者本人同意...