package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	/**
	 * 这个类实现对数据库的创建和升级
	 * 
	 */

	/**
	 * Province表建表语句
	 * id integer primary key autoincrement 指的是id自增长 
	 * 其中primary[主要的,最初的,基本的],autoincrement[自动增长]
	 */

	public static final String CREATER_PROVINCE = "create table Province ("
			+ "id integer primary key autoincrement," + "province_name text,"
			+ "province_code text)";

	/**
	 * City表建表语句
	 */

	public static final String CREATE_CITY = "create table City ("
			+ "id integer primary key autoincrement," + "city_name text"
			+ "city_code text" + "province_id integer)";

	/**
	 * County表建表语句
	 * 
	 */

	public static final String CREATER_COUNTY = "create table county ("
			+ "id integer primary key autoincrement," + "county_name text,"
			+ "county_code text," + "city_id integer";
	

	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// context 上下文;name 数据库名;factory 查询数据时返回一个cursor;version 版本号
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATER_PROVINCE);//创建Province表
		db.execSQL(CREATE_CITY);//创建City表
		db.execSQL(CREATER_COUNTY);//创建County表

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
