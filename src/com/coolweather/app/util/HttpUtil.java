package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



/**
 * 
 * ʹ��HttpURLConnection����Http�����������
 * 
 * 1.�������������Ǻ�ʱ����,������sendHttpRequest�����ڲ���Ҫ�������߳�(���û�п�ʼ���߳�,���׵����ڵ��ø÷�����ʱ��ʹ�����߳�����).
 * 2.������sendHttpRequest�ڲ��������߳�������Http����, ��������Ӧ������(��������ȡ�Ľ��)���޷����з��ص�(��Ϊ�����߳������ʱ��,
 * ���̺߳�ʱ��������δ����,���̲߳����ܵȴ����̷߳��ص�����,�����޷���������).
 * ����ʹ��java�Ļص�����,����һ���ӿ�,�ӿ��ж�����������:onFinish��onError,�ֱ��ڷ�������Ӧ�ɹ���ʧ�ܵ�ʱ�����.
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
					// newһ��URL����,����Ҫ���ʵ�Ŀ�������ַ.
					URL url = new URL(address);
					// ����url��openConnection������ȡHttpURLConnectionʵ��.
					connection = (HttpURLConnection) url.openConnection();
					// ����HTTP������ʹ�õķ���,GET��ʾ����,POST��ʾ�ύ
					connection.setRequestMethod("GET");
					// �������ӳ�ʱ,��ȡ��ʱ�ĺ�����,�Լ�������ϣ���õ���һЩ��Ϣͷ?????
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					// ����getInputStream������ȡ���������ص�������.
					InputStream in = connection.getInputStream();
					// �����������ж�ȡ,InputStreamReader���ֽ���ͨ���ַ���������
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
//					reader.close();// ���ﵽ��Ҫ��Ҫ�ر���????
					if (listener != null) {
						
						//�˴�ΪʲôҪ��listener!=nullΪ�ж�����????listener��ʲô����»���null???
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
	 * �������������ص�JSON����,���������������ݴ洢������
	 */
	
}
