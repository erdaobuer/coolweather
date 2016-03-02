package com.coolweather.app.model;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.db.CoolWeatherOpenHelper;

import android.R.integer;
import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {

	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "cool_weather";

	/**
	 * 数据库版本
	 */

	public static final int VERSION = 1;

	/**
	 * xxxxxxxxxxxxxx
	 */
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	/**
	 * 将构造方法私有化,在其中实现数据库的创建.
	 * 
	 * @param context
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
		// 调用数据库帮助类CoolWeatherOpenHelper的getWritableDatebse()方法创建数据库,并返回一个SQLieDatebase类型
	}

	/**
	 * 这里为什么要获取CoolWeather的实例呢?而且为什么要采用单例模式???
	 * 使用synchronized关键字修饰方法,放在public之后,返回类型之前,也就是个同步锁,表明一次只能有一个线程访问该方法,
	 * 其他线程要在此刻访问该方法,只能排队等待.
	 * 
	 * 获取CoolWeather的实例 这里使用synchronized修饰,实现单例模式.确保全局范围内只会有一个CoolWeatherDB实例.
	 * 
	 */

	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;

	}

	/**
	 * 将Province实例存储到数据库
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			/*
			 * 使用ContentValues来对要添加的数据进行组装.
			 * 组装好之后调用SQLiteDatabase的insert方法将组装好的数据添加到表中. 由于id设置自增长,所以不用添加.
			 */
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}

	/**
	 * 从数据库中读取全国所有的省份信息
	 * 
	 */

	public List<Province> loadProvinces() {

		/**
		 * 新建一个List集合,遍历数据库中的全国身份信息,并写入到List集合中,最后返回List集合.
		 * 
		 * 对于数据库的查询,使用SQLiteDatabase的query方法,只需要使用第一个参数指明查询哪个表,其他参数全设置为null.
		 * 该方法返回一个Cursor对象,Cursor是表中每一行的集合(也指的是游标 ),
		 */
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}

	/**
	 * 将City实例存储到数据库
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			db.insert("City", null, values);
		}
	}

	/**
	 * 从数据库中读取某个省下的所有城市信息
	 * 
	 * 
	 * 数据库查询方法query各参数的意义???
	 */

	public List<City> loadCities(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id=?",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}

		return list;
	}
	
	/**
	 * 将County实例存储到数据库中
	 */
	
	public void savaCounty(County county){
		/**
		 * 如果county不为空,则利用ContentValues对数据进行组装.
		 * 调用SQLiteDatabase.insert()方法添加进数据库.
		 */
		if (county!=null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}
	
	/**
	 * 从数据库中读取某个城市下的所有县城信息
	 */
	
	public List<County> loadCounties(int cityId){
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "cityId=?", new String[]{String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst()){
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				list.add(county);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}
	
	
}
