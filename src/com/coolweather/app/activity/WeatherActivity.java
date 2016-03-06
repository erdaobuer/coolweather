package com.coolweather.app.activity;

import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.HttpUtil.HttpCallbackListener;
import com.coolweather.app.util.Utility;
import com.example.coolweather.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {

	/**
	 * 设置变量分别用于显示城市名,发布时间,天气情况,气温1,气温2,当前时间.
	 */
	private TextView cityNameText, publishText, weatherDespText, temp1Text,
			temp2Text, currentDateText;

	private LinearLayout weatherInfoLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);

		// 初始化各控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);

		// 获得县级代号------------------------------------------------
		String countyCode = getIntent().getStringExtra("county_code");
		// 如果有县级代号的时候就去查询天气
		if (!TextUtils.isEmpty(countyCode)) {
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			// 加载天气数据.
			queryWeatherCode(countyCode);
		}
		// 如果没有县级代号,就证明是市级界面,直接显示对应城市天气.
		else {
			showWeather();
		}
	}

	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
	 */

	/**
	 * 查询县级代号对应的天气代号.
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	/**
	 * 查询天气代号对应的天气数据
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

	/**
	 * 根据传入的地址和类型去从服务器查询天气代号和天气信息
	 */
	private void queryFromServer(final String address, final String type) {
		// 向服务器发送请求
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(final String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// 从服务器返回的数据中解析出天气代号,以便后面查询使用
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					// 调用工具类Utility的handleWeatherResponse()方法将查询到的天气信息数据分析处理.
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {
						public void run() {
							// 将数据显示到UI界面,由于牵扯到UI操作,所以要回到主线程
							showWeather();
						}
					});

				}

			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						publishText.setText("同步失败");
					}
				});

			}
		});

	}

	/**
	 * 将读取到的天气信息显示到UI界面上
	 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}

}
