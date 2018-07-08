package com.adapter;

import java.util.List;




import com.hanastudio.columns.activity.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class AdapterShare extends ArrayAdapter {
	protected List items;
	
	@SuppressWarnings("unchecked")
	public AdapterShare(Context context, int textViewResourceId,
			List objects) {
		super(context, textViewResourceId, objects);
		this.items = objects;

	}





	public int getCount() {
		return this.items.size();
	}

	public Object getItem(int position) {
		return this.items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}



	
	/**
	 * Set the content for a row here
	 */
	public View getView(int position, View convertView, ViewGroup parent) {

		final ItemShare item = (ItemShare) this.items.get(position);
		View itemView = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = inflater.inflate(R.layout.item_share, null);
		} else {
			itemView = convertView;
		}
		
		if(item.getText()!=null){
			// Set the check-icon
			TextView textView = (TextView) itemView
					.findViewById(R.id.textView1);
			textView.setText(item.getText());
		}

		if(item.getItemIdIcon()!=-1){
			// Set the check-icon
			ImageView imageView = (ImageView) itemView
					.findViewById(R.id.iconview);
			imageView.setImageResource(item.getItemIdIcon());
		}

		 
		
		return itemView;

	}
}