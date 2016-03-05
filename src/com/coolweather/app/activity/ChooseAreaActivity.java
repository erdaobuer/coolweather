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
	 * 省列表集合
	 */
	private List<Province> provinceList;

	/**
	 * 市列表集合
	 */

	private List<City> cityList;

	/**
	 * 县列表集合
	 */
	private List<County> countyList;

	/**
	 * 选中的省号
	 */
	private Province selectedProvince;

	/**
	 * 选中的市号
	 */
	private City selectedCity;

	/**
	 * 现在选中的级别
	 */
	private int nowLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 为Activity关联布局文件
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		/**
		 * 获取服务器操作封装类的实例
		 */
		coolWeatherDB = CoolWeatherDB.getInstance(this);

		/**
		 * 为listView设置适配器,适配器第二个参数指的是listView子项的布局格式,第三个参数指的是listView子项的数据集合
		 */
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		/**
		 * 取消ListView分割线
		 */
		// listView.setDivider(null);

		/**
		 * ListView的一些基本属性设置
		 */
		// 设置分割线的宽度,通过测试默认值为1.
		// listView.setDividerHeight(2);
		// 设置分割线的颜色.
		// listView.setBackgroundColor(Color.GRAY);
		// 设置背景透明度(0-255)
		 listView.getBackground().setAlpha(80);

		/**
		 * 加载省级数据,在其中为dataList集合添加数据
		 */
		queryProvinces();

		/**
		 * 为ListView设置按键点击事件
		 * 首先,判断点击的是不是省级标识,如果不是,判断是不是市级标识,如果是,判断点击的是哪个市,加载该市下面的
		 * 如果是省级表示,接着判断点击的是省级里面哪个省, 最后,加载该省下面的市级数据.
		 */
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 如果按下的是省级标识,就从数据库中读取省级信息
				if (nowLevel == LEVEL_PROVINCE) {
					// 选中的是哪个省
					selectedProvince = provinceList.get(position);
					// 加载该省下面的市级数据
					queryCities();
				} else if (nowLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					// 加载该市下面的县级数据
					queryCounties();
				}
			}
		});

	}

	/**
	 * 查询全国所有的省,优先从数据库中查询,如果没有查询到再去服务器上查询
	 */
	private void queryProvinces() {
		/**
		 * 从数据库中获取所有省的数据,放入到省列表集合中
		 */
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			/**
			 * 刷新ListView中的数据
			 * ***************************特此注释,谨防遗忘*******************
			 */
			adapter.notifyDataSetChanged();
			/**
			 * 将ListView定位到第一行(开头),也就是打开界面后ListView从第一行开始显示
			 */
			listView.setSelection(0);
			/**
			 * 将标题设置为中国
			 */
			titleText.setText("中国");
			nowLevel = LEVEL_PROVINCE;
		} else {
			/**
			 * 如果从数据库查询不到数据,就编写方法去服务器上查询
			 */
			queryFromServer(null, "province");
		}

	}

	/**
	 * 查询某省下所有的市,优先从服务器查询,如果没查询到,就去服务器查询
	 */
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			/**
			 * 刷新ListView中的数据,notify(通知)
			 */
			adapter.notifyDataSetChanged();
			/**
			 * 将ListView定位到第一行
			 */
			listView.setSelection(0);
			/**
			 * 设置标题为选中省的名称
			 */
			titleText.setText(selectedProvince.getProvinceName());
			nowLevel = LEVEL_CITY;
		} else {
			/**
			 * 如果数据库中未读取到数据,就去服务器上查询
			 */
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/**
	 * 查询某市区所有的县,优先从数据库上查询,如果查询不到,就去服务器上查询.
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
	 * 从服务器上查询数据,传入查询的代号和类型 address表示拼装的URL地址 address =
	 * "http://www.weather.com.cn/data/list3/city"+code+".xml"
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		/**
		 * 如果code不为空,说明访问的是市级或县级的数据;如果code为空,说明访问的是省级的数据.
		 * TextUtils.isEmpty(参数)判断语句,如果code为空就返回true,否则为false.
		 */
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		/**
		 * 显示进度条,表示在加载数据中...
		 */
		showProgressDialog();

		/**
		 * 与服务器交互,查询并返回数据.
		 */
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			/**
			 * 设置一个判断的标识,成功为true,失败为false.
			 */
			boolean result = false;

			@Override
			public void onFinish(String response) {
				/**
				 * 判断查询的类型,然后解析数据
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
				 * 根据查询后的result判断是否解析成功,如果解析成功,数据库中就会有数据了,就重现调用queryProvinces()
				 * 方法. 注意:由于重新调用queryProvinces()方法牵扯到UI操作, ----必须在主线程中操作,
				 * 所以借助runOnUiThread
				 * ()方法实现子线程切换到主线程.(---onFinished()里面的操作属于子线程---).
				 */
				if (result) {
					runOnUiThread(new Runnable() {
						public void run() {
							/**
							 * 首先关闭进度对话框,然后根据所传入的类型重新去查询数据
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
			 * 如果查询失败了,执行onError方法,关闭进度对话框.
			 */
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * 显示进度对话框方法
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();

	}

	/**
	 * 关闭进度对话框方法
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();// 删除进度条
		}
	}

	/**
	 * 捕获Back按键,根据当前的级别判断,此时应该返回省列表,市列表还是直接推出.
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
