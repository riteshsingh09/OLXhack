package com.olxhack.offlinedata;

import java.util.ArrayList;

public class OfflineData {
	public static  ArrayList<String> mCatagoryList;
	public static  ArrayList<String> mMobileList;
	public static  ArrayList<String> mCarsList;
	public static  ArrayList<String> mElectronicsList;
	
	
	public static ArrayList<String> getAllCategory(){
		if(mCatagoryList==null){
			mCatagoryList= new ArrayList<String>();
			mCatagoryList.add("Mobile");
			mCatagoryList.add("Cars");
			mCatagoryList.add("Electronics");
			
		}
		
		return mCatagoryList;
		
		
	}
	
	public static ArrayList<String> getAllItemsOfCategory(String catagory){
		
		if(catagory.equals("Mobile") ){
			if(mMobileList==null){
				mCatagoryList= new ArrayList<String>();
				mMobileList.add("Windows");
				mMobileList.add("Nokia");
				mMobileList.add("Iphone");
				mMobileList.add("Blackberry");
				mMobileList.add("Android");
				return mMobileList;
			}else{
				return mMobileList;
			}
			//
						
		}if(catagory.equals("Cars") ){
			if(mCarsList==null){
				mCarsList= new ArrayList<String>();
				mCarsList.add("Fiat");
				mCarsList.add("Honda");
				mCarsList.add("Maruti");
				mCarsList.add("Hyundai");
				mCarsList.add("Volkswagon");
				mCarsList.add("Skoda");
				return mCarsList;
			}else{
				return mCarsList;
			}
			
		}if(catagory.equals("Electronics") ){
			if(mElectronicsList==null){
				mElectronicsList= new ArrayList<String>();
				mElectronicsList.add("Air Conditioner");
				mElectronicsList.add("TV");
				mElectronicsList.add("Refrigerator");
				mElectronicsList.add("Camera");
				mElectronicsList.add("Washing Machine");
				return mElectronicsList;
			}else{
				return mElectronicsList;
			}
			
			
			
		}else{
			ArrayList<String> newData = null;
			return newData;
		}
		
		
	}

}
