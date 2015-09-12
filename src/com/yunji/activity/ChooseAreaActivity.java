package com.yunji.activity;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import com.example.yunji.R;
import com.yunji.db.YunWeatherDB;
import com.yunji.model.City;
import com.yunji.model.County;
import com.yunji.model.Province;
import com.yunji.util.HttpCallbackListener;
import com.yunji.util.HttpUtil;
import com.yunji.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity{
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private YunWeatherDB yunWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	
	/**
	 * ʡ�б�
	 */
	private List<Province> provinceList;
	
	/**
	 * ���б�
	 */
	private List<City> cityList;
	
	/**
	 * ���б�
	 */
	private List<County> countyList;
	
	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;
	/**
	 * ѡ�еĳ���
	 */
	private City selectedCity;
	/**
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		yunWeatherDB = YunWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int index, long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();
				}else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();
				}	
			}
		});
		queryProvinces();			
	}
	/**
	 * ��ѯ����ʡ,���ȴ����ݿ��ѯ,���û�в�ѯ���ٵ���������ѯ
	 */
	private void queryProvinces() {
		provinceList = yunWeatherDB.loadProvince();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	/**
	 * ��ѯѡ��ʡ��������,���ȴ����ݿ��ѯ,���û�в�ѯ���ٵ���������ѯ
	 */
	private void queryCities() {
		cityList = yunWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0 ) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{
			Log.i("wtf", "�����������ȡ������");
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}
	/**
	 * ��ѯѡ������������,���ȴ����ݿ��ѯ,���û�в�ѯ���ٵ���������ѯ
	 */
	private void queryCounties() {
		countyList = yunWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0 ) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectedCity.getCityCode(),"county");
		}
	}
	
	/**
	 * ���ݴ�����ź����ʹӷ������ϲ�ѯʡ������Ϣ
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(yunWeatherDB, response);
				}else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(yunWeatherDB, response, selectedProvince.getId());
				}else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(yunWeatherDB, response, selectedCity.getId());
				}
				if (result) {
					//�ص����̴߳���
					runOnUiThread( new Runnable() {
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							}else if ("city".equals(type)) {
								queryCities();
							}else if ("county".equals(type)) {
								queryCounties();
							}
						}

					});
				}
				
			}
			
			@Override
			public void onError(Exception e) {
			runOnUiThread(new Runnable() {
				public void run() {
				closeProgressDialog();
				Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
				}
			});	
				
			}
		});
	}
	
	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 *�رս��ȶԻ��� 
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * ��дback��,�жϸ÷���ʡ�л����˳�
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		}else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		}else{
			finish();
		}
	}
}










