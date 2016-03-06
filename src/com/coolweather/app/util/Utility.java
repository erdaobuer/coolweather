package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class Utility {
	/**
	 * 新建一个工具类来解析和处理服务器返回的数据,因为服务器返回的数据都是"代号|省份,代号|省份,""代号|城市,代号|城市"这种格式.
	 */

	/**
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");// 190401|苏州,190402|常熟,190403|张家港,......
			// 通过循环解析省级数据,并存储到Province表中
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");// 190401|苏州
					Province province = new Province();
					province.setProvinceName(array[1]);
					province.setProvinceCode(array[0]);
					// 调用CoolWeatherDB.savaProvince方法将解析出来的数据存储到Province表中
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的市级数据
	 * 
	 */

	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityName(array[1]);
					city.setCityCode(array[0]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的县级数据
	 */
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyName(array[1]);
					county.setCountyCode(array[0]);
					county.setCityId(cityId);
					coolWeatherDB.savaCounty(county);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的JSON天气数据
	 */
	public static void handleWeatherResponse(Context context, String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			// 把解析出来的数据存储到SharedPreferences文件中
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
					weatherDesp, publishTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 将服务器返回的所有有关天气的数据存储起来
	 */
	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		//通过SharedPreferences存储文件,SharedPreferences有三种获取方式.这里采用的是第三种
		SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
		//增加标志位------------------------------------------------
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		//下面这句不懂?????--------------------------------
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
}
