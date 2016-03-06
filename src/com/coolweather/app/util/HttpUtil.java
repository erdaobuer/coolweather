package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



/**
 * 
 * 使用HttpURLConnection发送Http请求给服务器
 * 
 * 1.由于网络请求都是耗时操作,所有在sendHttpRequest方法内部需要开启子线程(如果没有开始子线程,容易导致在调用该方法的时候使得主线程阻塞).
 * 2.由于在sendHttpRequest内部开启子线程来发起Http请求, 服务器响应的数据(输入流读取的结果)是无法进行返回的(因为在主线程跑完的时候,
 * 子线程耗时操作都还未跑完,主线程不可能等待子线程返回的数据,所以无法返回数据).
 * 可以使用java的回调机制,定义一个接口,接口中定义两个方法:onFinish和onError,分别在服务器响应成功和失败的时候调用.
 * 
 * 
 */

public class HttpUtil {

	public interface HttpCallbackListener {
		void onFinish(String response);

		void onError(Exception e);

	}

	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection = null;
				try {
					// new一个URL对象,传入要访问的目标网络地址.
					URL url = new URL(address);
					// 调用url的openConnection方法获取HttpURLConnection实例.
					connection = (HttpURLConnection) url.openConnection();
					// 设置HTTP请求所使用的方法,GET表示请求,POST表示提交
					connection.setRequestMethod("GET");
					// 设置连接超时,读取超时的毫秒数,以及服务器希望得到的一些消息头?????
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					// 调用getInputStream方法获取服务器返回的输入流.
					InputStream in = connection.getInputStream();
					// 对输入流进行读取,InputStreamReader是字节流通向字符流的桥梁
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
//					reader.close();// 这里到底要不要关闭流????
					if (listener != null) {
						
						//此处为什么要以listener!=null为判断条件????listener在什么情况下会是null???
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					
					if (listener != null) {
						listener.onError(e);
					}
					e.printStackTrace();
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}
	
	/**
	 * 解析服务器返回的JSON数据,并将解析出的数据存储到本地
	 */
	
}
