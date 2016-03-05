package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.HttpUtil.HttpCallbackListener;
import com.coolweather.app.util.Utility;
import com.example.coolweather.R;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
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

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;

	private TextView titleText;
	private ListView listView;
	private CoolWeatherDB coolWeatherDB;
	private ArrayAdapter<String> adapter;
	private ProgressDialog progressDialog;
	private List<String> dataList = new ArrayList<String>();

	/**
	 * ʡ�б���
	 */
	private List<Province> provinceList;

	/**
	 * ���б���
	 */

	private List<City> cityList;

	/**
	 * ���б���
	 */
	private List<County> countyList;

	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;

	/**
	 * ѡ�е��к�
	 */
	private City selectedCity;

	/**
	 * ����ѡ�еļ���
	 */
	private int nowLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �����ޱ���
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ΪActivity���������ļ�
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		/**
		 * ��ȡ������������װ���ʵ��
		 */
		coolWeatherDB = CoolWeatherDB.getInstance(this);

		/**
		 * ΪlistView����������,�������ڶ�������ָ����listView����Ĳ��ָ�ʽ,����������ָ����listView��������ݼ���
		 */
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		/**
		 * ȡ��ListView�ָ���
		 */
		// listView.setDivider(null);

		/**
		 * ListView��һЩ������������
		 */
		// ���÷ָ��ߵĿ��,ͨ������Ĭ��ֵΪ1.
		// listView.setDividerHeight(2);
		// ���÷ָ��ߵ���ɫ.
		// listView.setBackgroundColor(Color.GRAY);
		// ���ñ���͸����(0-255)
		 listView.getBackground().setAlpha(80);

		/**
		 * ����ʡ������,������ΪdataList�����������
		 */
		queryProvinces();

		/**
		 * ΪListView���ð�������¼�
		 * ����,�жϵ�����ǲ���ʡ����ʶ,�������,�ж��ǲ����м���ʶ,�����,�жϵ�������ĸ���,���ظ��������
		 * �����ʡ����ʾ,�����жϵ������ʡ�������ĸ�ʡ, ���,���ظ�ʡ������м�����.
		 */
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// ������µ���ʡ����ʶ,�ʹ����ݿ��ж�ȡʡ����Ϣ
				if (nowLevel == LEVEL_PROVINCE) {
					// ѡ�е����ĸ�ʡ
					selectedProvince = provinceList.get(position);
					// ���ظ�ʡ������м�����
					queryCities();
				} else if (nowLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					// ���ظ���������ؼ�����
					queryCounties();
				}
			}
		});

	}

	/**
	 * ��ѯȫ�����е�ʡ,���ȴ����ݿ��в�ѯ,���û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryProvinces() {
		/**
		 * �����ݿ��л�ȡ����ʡ������,���뵽ʡ�б�����
		 */
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			/**
			 * ˢ��ListView�е�����
			 * ***************************�ش�ע��,��������*******************
			 */
			adapter.notifyDataSetChanged();
			/**
			 * ��ListView��λ����һ��(��ͷ),Ҳ���Ǵ򿪽����ListView�ӵ�һ�п�ʼ��ʾ
			 */
			listView.setSelection(0);
			/**
			 * ����������Ϊ�й�
			 */
			titleText.setText("�й�");
			nowLevel = LEVEL_PROVINCE;
		} else {
			/**
			 * ��������ݿ��ѯ��������,�ͱ�д����ȥ�������ϲ�ѯ
			 */
			queryFromServer(null, "province");
		}

	}

	/**
	 * ��ѯĳʡ�����е���,���ȴӷ�������ѯ,���û��ѯ��,��ȥ��������ѯ
	 */
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			/**
			 * ˢ��ListView�е�����,notify(֪ͨ)
			 */
			adapter.notifyDataSetChanged();
			/**
			 * ��ListView��λ����һ��
			 */
			listView.setSelection(0);
			/**
			 * ���ñ���Ϊѡ��ʡ������
			 */
			titleText.setText(selectedProvince.getProvinceName());
			nowLevel = LEVEL_CITY;
		} else {
			/**
			 * ������ݿ���δ��ȡ������,��ȥ�������ϲ�ѯ
			 */
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/**
	 * ��ѯĳ�������е���,���ȴ����ݿ��ϲ�ѯ,�����ѯ����,��ȥ�������ϲ�ѯ.
	 */
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			nowLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	/**
	 * �ӷ������ϲ�ѯ����,�����ѯ�Ĵ��ź����� address��ʾƴװ��URL��ַ address =
	 * "http://www.weather.com.cn/data/list3/city"+code+".xml"
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		/**
		 * ���code��Ϊ��,˵�����ʵ����м����ؼ�������;���codeΪ��,˵�����ʵ���ʡ��������.
		 * TextUtils.isEmpty(����)�ж����,���codeΪ�վͷ���true,����Ϊfalse.
		 */
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		/**
		 * ��ʾ������,��ʾ�ڼ���������...
		 */
		showProgressDialog();

		/**
		 * �����������,��ѯ����������.
		 */
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			/**
			 * ����һ���жϵı�ʶ,�ɹ�Ϊtrue,ʧ��Ϊfalse.
			 */
			boolean result = false;

			@Override
			public void onFinish(String response) {
				/**
				 * �жϲ�ѯ������,Ȼ���������
				 */
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolWeatherDB,
							response, selectedCity.getId());
				}

				/**
				 * ���ݲ�ѯ���result�ж��Ƿ�����ɹ�,��������ɹ�,���ݿ��оͻ���������,�����ֵ���queryProvinces()
				 * ����. ע��:�������µ���queryProvinces()����ǣ����UI����, ----���������߳��в���,
				 * ���Խ���runOnUiThread
				 * ()����ʵ�����߳��л������߳�.(---onFinished()����Ĳ����������߳�---).
				 */
				if (result) {
					runOnUiThread(new Runnable() {
						public void run() {
							/**
							 * ���ȹرս��ȶԻ���,Ȼ��������������������ȥ��ѯ����
							 */
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}

			/**
			 * �����ѯʧ����,ִ��onError����,�رս��ȶԻ���.
			 */
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * ��ʾ���ȶԻ��򷽷�
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
	 * �رս��ȶԻ��򷽷�
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();// ɾ��������
		}
	}

	/**
	 * ����Back����,���ݵ�ǰ�ļ����ж�,��ʱӦ�÷���ʡ�б�,���б���ֱ���Ƴ�.
	 */
	public void onBackPressed() {
		if (nowLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (nowLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}

}
