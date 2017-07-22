package com.learnopengles.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

import com.learnopengles.android.lesson5.LessonFiveActivity;
import com.learnopengles.android.lesson6.LessonSixActivity;
import com.learnopengles.android.lesson7.LessonSevenActivity;
import com.learnopengles.android.lesson7b.LessonSevenBActivity;
import com.learnopengles.android.lesson8.LessonEightActivity;

public class TableOfContents extends ListActivity 
{
	private static final String ITEM_IMAGE = "item_image";
	private static final String ITEM_TITLE = "item_title";
	private static final String ITEM_SUBTITLE = "item_subtitle";	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setTitle(R.string.toc);
		setContentView(R.layout.table_of_contents);
		
		// Initialize data
		final List<Map<String, Object>> data = new ArrayList<>();
		final SparseArray<Class<? extends android.app.Activity>> activityMapping = new SparseArray<>();
		
		int i = 0;
		
		{
			final Map<String, Object> item = new HashMap<>();
			item.put(ITEM_IMAGE, R.drawable.ic_lesson_one);
			item.put(ITEM_TITLE, getText(R.string.lesson_one));
			item.put(ITEM_SUBTITLE, getText(R.string.lesson_one_subtitle));
			data.add(item);
			activityMapping.put(i++, com.learnopengles.android.lesson1.Activity.class);
		}
		
		{
			final Map<String, Object> item = new HashMap<>();
			item.put(ITEM_IMAGE, R.drawable.ic_lesson_two);
			item.put(ITEM_TITLE, getText(R.string.lesson_two));
			item.put(ITEM_SUBTITLE, getText(R.string.lesson_two_subtitle));
			data.add(item);
			activityMapping.put(i++, com.learnopengles.android.lesson2.Activity.class);
		}
		
		{
			final Map<String, Object> item = new HashMap<>();
			item.put(ITEM_IMAGE, R.drawable.ic_lesson_three);
			item.put(ITEM_TITLE, getText(R.string.lesson_three));
			item.put(ITEM_SUBTITLE, getText(R.string.lesson_three_subtitle));
			data.add(item);
			activityMapping.put(i++, com.learnopengles.android.lesson3.Activity.class);
		}
		
		{
			final Map<String, Object> item = new HashMap<>();
			item.put(ITEM_IMAGE, R.drawable.ic_lesson_four);
			item.put(ITEM_TITLE, getText(R.string.lesson_four));
			item.put(ITEM_SUBTITLE, getText(R.string.lesson_four_subtitle));
			data.add(item);
			activityMapping.put(i++, com.learnopengles.android.lesson4.Activity.class);
		}
		
		{
			final Map<String, Object> item = new HashMap<>();
			item.put(ITEM_IMAGE, R.drawable.ic_lesson_five);
			item.put(ITEM_TITLE, getText(R.string.lesson_five));
			item.put(ITEM_SUBTITLE, getText(R.string.lesson_five_subtitle));
			data.add(item);
			activityMapping.put(i++, LessonFiveActivity.class);
		}
		
		{
			final Map<String, Object> item = new HashMap<>();
			item.put(ITEM_IMAGE, R.drawable.ic_lesson_six);
			item.put(ITEM_TITLE, getText(R.string.lesson_six));
			item.put(ITEM_SUBTITLE, getText(R.string.lesson_six_subtitle));
			data.add(item);
			activityMapping.put(i++, LessonSixActivity.class);
		}
		
		{
			final Map<String, Object> item = new HashMap<>();
			item.put(ITEM_IMAGE, R.drawable.ic_lesson_seven);
			item.put(ITEM_TITLE, getText(R.string.lesson_seven));
			item.put(ITEM_SUBTITLE, getText(R.string.lesson_seven_subtitle));
			data.add(item);
			activityMapping.put(i++, LessonSevenActivity.class);
		}

		{
			final Map<String, Object> item = new HashMap<>();
			item.put(ITEM_IMAGE, R.drawable.ic_lesson_seven);
			item.put(ITEM_TITLE, getText(R.string.lesson_seven_b));
			item.put(ITEM_SUBTITLE, getText(R.string.lesson_seven_b_subtitle));
			data.add(item);
			activityMapping.put(i++, LessonSevenBActivity.class);
		}
		
		{
			final Map<String, Object> item = new HashMap<>();
			item.put(ITEM_IMAGE, R.drawable.ic_lesson_eight);
			item.put(ITEM_TITLE, getText(R.string.lesson_eight));
			item.put(ITEM_SUBTITLE, getText(R.string.lesson_eight_subtitle));
			data.add(item);
			activityMapping.put(i++, LessonEightActivity.class);
		}
		{
			final Map<String, Object> item = new HashMap<>();
			item.put(ITEM_IMAGE, R.drawable.ic_lesson_eight);
			item.put(ITEM_TITLE, getText(R.string.lesson_eight_b));
			item.put(ITEM_SUBTITLE, getText(R.string.lesson_eight_b_subtitle));
			data.add(item);
			activityMapping.put(i++, com.learnopengles.android.lesson8b.Activity.class);
		}
		{
			final Map<String, Object> item = new HashMap<>();
			item.put(ITEM_IMAGE, R.drawable.ic_lesson_nine);
			item.put(ITEM_TITLE, getText(R.string.lesson_nine));
			item.put(ITEM_SUBTITLE, getText(R.string.lesson_nine_subtitle));
			data.add(item);
			activityMapping.put(i++, com.learnopengles.android.lesson9.Activity.class);
		}
		{
			final Map<String, Object> item = new HashMap<>();
			item.put(ITEM_IMAGE, R.drawable.ic_lesson_ten);
			item.put(ITEM_TITLE, getText(R.string.lesson_ten));
			item.put(ITEM_SUBTITLE, getText(R.string.lesson_ten_subtitle));
			data.add(item);
			activityMapping.put(i++, com.learnopengles.android.lesson10.Activity.class);
		}
		{
			final Map<String, Object> item = new HashMap<>();
			item.put(ITEM_IMAGE, R.drawable.ic_lesson_eleven);
			item.put(ITEM_TITLE, getText(R.string.lesson_eleven));
			item.put(ITEM_SUBTITLE, getText(R.string.lesson_eleven_subtitle));
			data.add(item);
			activityMapping.put(i++, com.learnopengles.android.lesson11.Activity.class);
		}

		
		final SimpleAdapter dataAdapter = new SimpleAdapter(this, data, R.layout.toc_item, new String[] {ITEM_IMAGE, ITEM_TITLE, ITEM_SUBTITLE}, new int[] {R.id.Image, R.id.Title, R.id.SubTitle});
		setListAdapter(dataAdapter);	
		
		getListView().setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			 public void onItemClick(AdapterView<?> parent, View view,
				        int position, long id) 
			{
				final Class<? extends android.app.Activity> activityToLaunch = activityMapping.get(position);
				
				if (activityToLaunch != null)
				{
					final Intent launchIntent = new Intent(TableOfContents.this, activityToLaunch);
					startActivity(launchIntent);
				}				
			}
		});
	}	
}
