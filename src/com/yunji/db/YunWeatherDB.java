package com.yunji.db;

import java.util.ArrayList;
import java.util.List;

import com.yunji.model.City;
import com.yunji.model.County;
import com.yunji.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class YunWeatherDB {
	/**
	 * ���ݿ��� �汾
	 */
	public static final String DB_NAME = "yun_weather";
	public static final int VERSION = 1;
	private static YunWeatherDB yunWeatherDB;
	private SQLiteDatabase db;
	
	/**
	 * �����췽��˽�л�
	 */
	private YunWeatherDB(Context context){
		LocationOpenHelper dbHelper = new LocationOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	/**
	 * ��ȡYunWeatherDB ʵ��
	 */
	public synchronized static YunWeatherDB getInstance(Context context){
		if(yunWeatherDB == null){
			yunWeatherDB = new YunWeatherDB(context);
		}
		return yunWeatherDB;
	}
	
	/**
	 * ��Provinceʵ�����浽���ݿ�
	 */
	public void saveProvince(Province province){
		if(province != null){
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province",null, values);
		}
	}
	
	/**
	 * ���ݿ��ȡȫ��ʡ����Ϣ
	 */
	public List<Province> loadProvince(){
		List<Province> list = new ArrayList<>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}
	
	/**
	 * ��Cityʵ�����浽���ݿ�
	 */
	public void saveCity(City city){
		if(city != null){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City",null, values);
		}
	}
	
	/**
	 * ���ݿ��ȡĳʡ�����г�����Ϣ
	 */
	public List<City> loadCities(int provinceId){
		List<City> list = new ArrayList<>();
		Cursor cursor = db
				.query("City", null, "provinceId =?", new String[]{ String.valueOf(provinceId)}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
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
	 * ��Countyʵ�����浽���ݿ�
	 */
	public void saveCounty(County county){
		if(county != null){
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County",null, values);
		}
	}
	
	/**
	 * ���ݿ��ȡĳ��������������Ϣ
	 */
	public List<County> loadCounties(int cityId){
		List<County> list = new ArrayList<>();
		Cursor cursor = db
				.query("County", null, "cityId =?", new String[]{ String.valueOf(cityId)}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}
	
	

	
	
}