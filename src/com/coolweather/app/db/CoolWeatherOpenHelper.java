package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	/**
	 * �����ʵ�ֶ����ݿ�Ĵ���������
	 * 
	 */

	/**
	 * Province�������
	 * id integer primary key autoincrement ָ����id������ 
	 * ����primary[��Ҫ��,�����,������],autoincrement[�Զ�����]
	 */

	public static final String CREATER_PROVINCE = "create table Province ("
			+ "id integer primary key autoincrement," + "province_name text,"
			+ "province_code text)";

	/**
	 * City�������
	 */

	public static final String CREATE_CITY = "create table City ("
			+ "id integer primary key autoincrement," + "city_name text"
			+ "city_code text" + "province_id integer)";

	/**
	 * County�������
	 * 
	 */

	public static final String CREATER_COUNTY = "create table county ("
			+ "id integer primary key autoincrement," + "county_name text,"
			+ "county_code text," + "city_id integer";
	

	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// context ������;name ���ݿ���;factory ��ѯ����ʱ����һ��cursor;version �汾��
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATER_PROVINCE);//����Province��
		db.execSQL(CREATE_CITY);//����City��
		db.execSQL(CREATER_COUNTY);//����County��

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
