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
	 * ���ݿ���
	 */
	public static final String DB_NAME = "cool_weather";

	/**
	 * ���ݿ�汾
	 */

	public static final int VERSION = 1;

	/**
	 * xxxxxxxxxxxxxx
	 */
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	/**
	 * �����췽��˽�л�,������ʵ�����ݿ�Ĵ���.
	 * 
	 * @param context
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
		// �������ݿ������CoolWeatherOpenHelper��getWritableDatebse()�����������ݿ�,������һ��SQLieDatebase����
	}

	/**
	 * ����ΪʲôҪ��ȡCoolWeather��ʵ����?����ΪʲôҪ���õ���ģʽ???
	 * ʹ��synchronized�ؼ������η���,����public֮��,��������֮ǰ,Ҳ���Ǹ�ͬ����,����һ��ֻ����һ���̷߳��ʸ÷���,
	 * �����߳�Ҫ�ڴ˿̷��ʸ÷���,ֻ���Ŷӵȴ�.
	 * 
	 * ��ȡCoolWeather��ʵ�� ����ʹ��synchronized����,ʵ�ֵ���ģʽ.ȷ��ȫ�ַ�Χ��ֻ����һ��CoolWeatherDBʵ��.
	 * 
	 */

	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;

	}

	/**
	 * ��Provinceʵ���洢�����ݿ�
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			/*
			 * ʹ��ContentValues����Ҫ��ӵ����ݽ�����װ.
			 * ��װ��֮�����SQLiteDatabase��insert��������װ�õ�������ӵ�����. ����id����������,���Բ������.
			 */
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}

	/**
	 * �����ݿ��ж�ȡȫ�����е�ʡ����Ϣ
	 * 
	 */

	public List<Province> loadProvinces() {

		/**
		 * �½�һ��List����,�������ݿ��е�ȫ�������Ϣ,��д�뵽List������,��󷵻�List����.
		 * 
		 * �������ݿ�Ĳ�ѯ,ʹ��SQLiteDatabase��query����,ֻ��Ҫʹ�õ�һ������ָ����ѯ�ĸ���,��������ȫ����Ϊnull.
		 * �÷�������һ��Cursor����,Cursor�Ǳ���ÿһ�еļ���(Ҳָ�����α� ),
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
	 * ��Cityʵ���洢�����ݿ�
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
	 * �����ݿ��ж�ȡĳ��ʡ�µ����г�����Ϣ
	 * 
	 * 
	 * ���ݿ��ѯ����query������������???
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
	 * ��Countyʵ���洢�����ݿ���
	 */
	
	public void savaCounty(County county){
		/**
		 * ���county��Ϊ��,������ContentValues�����ݽ�����װ.
		 * ����SQLiteDatabase.insert()������ӽ����ݿ�.
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
	 * �����ݿ��ж�ȡĳ�������µ������س���Ϣ
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
