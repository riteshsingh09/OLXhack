package com.olxhack.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.olxhack.R;
import com.olxhack.offlinedata.OfflineData;



public class OfflineAdapter extends RecyclerView.Adapter<OfflineAdapter.OfflineHolder> {

	private static final String LOG_TAG = "offlineAdapter";
	private Context mContext;
	public OfflineAdapter(Context contex) {

		mContext=contex;

	}

	@Override
	public int getItemCount() {
		return OfflineData.getAllCategory().size();
	}

	@Override
	public void onBindViewHolder(OfflineHolder cate, int i) {
		String cat = OfflineData.getAllCategory().get(i);
		cate.titel.setText(cat+"");
		cate.image.setImageDrawable(mContext.getResources().getDrawable(
				getCatDrawable(i)));

	}

	@Override
	public OfflineHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
				R.layout.offlineitem, viewGroup, false);

		return new OfflineHolder(itemView);
	}

	public  class OfflineHolder extends RecyclerView.ViewHolder 	{
		protected TextView titel;
		protected ImageView image;

		public OfflineHolder(final View itemView) {
			super(itemView);
			titel = (TextView) itemView.findViewById(R.id.catName);
			image = (ImageView) itemView.findViewById(R.id.icon);
		}

	}
	private int getCatDrawable(int postion) {

		switch (postion) {
		case 0:
		return	R.drawable.zipcall;
		case 1:
			return	R.drawable.car1;
			
		case 2:
			return	R.drawable.icon_fo;
		default:
			return	R.drawable.ic_launcher;
		}

	}


}