package com.hover.tester.services;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hover.sdk.onboarding.HoverIntegrationActivity;
import com.hover.sdk.utils.Utils;
import com.hover.tester.actions.OperatorAction;
import com.hover.tester.database.Contract;
import com.hover.tester.database.DbHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class OperatorService {
	public static final String TAG = "OperatorService", PIN = "pin";

	public int mId;
	public String mName, mOpSlug, mCountryIso, mCurrencyIso, mEncryptedPin;
	public List<OperatorAction> mActions;

	public OperatorService(Intent data, Context c) {
		mId = data.getIntExtra("serviceId", -1);
		mName = data.getStringExtra("serviceName");
		mOpSlug = data.getStringExtra("opSlug");
		mCountryIso = data.getStringExtra("countryName");
		mCurrencyIso = data.getStringExtra("currency");
	}

	public OperatorService(Cursor cursor, Context c) {
		mId = cursor.getInt(cursor.getColumnIndex(Contract.OperatorServiceEntry.COLUMN_SERVICE_ID));
		mName = cursor.getString(cursor.getColumnIndex(Contract.OperatorServiceEntry.COLUMN_NAME));
		mOpSlug = cursor.getString(cursor.getColumnIndex(Contract.OperatorServiceEntry.COLUMN_OP_SLUG));
		mCountryIso = cursor.getString(cursor.getColumnIndex(Contract.OperatorServiceEntry.COLUMN_COUNTRY));
		mCurrencyIso = cursor.getString(cursor.getColumnIndex(Contract.OperatorServiceEntry.COLUMN_CURRENCY));
	}

	public OperatorService save(Context c) {
		mId = (int) ContentUris.parseId(c.getContentResolver().insert(Contract.OperatorServiceEntry.CONTENT_URI, getBasicContentValues()));
		return this;
	}

	public static int count(Context c) {
		Cursor countCursor = c.getContentResolver().query(Contract.OperatorServiceEntry.CONTENT_URI, new String[] {"count(*) AS count"}, null, null, null);
		if (countCursor != null) {
			countCursor.moveToFirst();
			int count = countCursor.getInt(0);
			countCursor.close();
			return count;
		}
		return 0;
	}

	private ContentValues getBasicContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(Contract.OperatorServiceEntry.COLUMN_SERVICE_ID, mId);
		cv.put(Contract.OperatorServiceEntry.COLUMN_NAME, mName);
		cv.put(Contract.OperatorServiceEntry.COLUMN_OP_SLUG, mOpSlug);
		cv.put(Contract.OperatorServiceEntry.COLUMN_COUNTRY, mCountryIso);
		cv.put(Contract.OperatorServiceEntry.COLUMN_CURRENCY, mCurrencyIso);
		cv.put(Contract.OperatorServiceEntry.COLUMN_PIN, mEncryptedPin);
		return cv;
	}

	public void saveActions(Context c) {
		for (OperatorAction opAction: mActions)
			opAction.save(c);
	}

	public static int getId(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(Contract.OperatorServiceEntry.COLUMN_SERVICE_ID));
	}
	public static String getPin(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(Contract.OperatorServiceEntry.COLUMN_PIN));
	}
	public void setPin(String value) {
		mEncryptedPin = value;
	}

	public static OperatorService load(int id, Context c) {
		OperatorService service = null;
		SQLiteDatabase database = new DbHelper(c).getReadableDatabase();
		Cursor cursor = database.query(Contract.OperatorServiceEntry.TABLE_NAME, Contract.SERVICE_PROJECTION,
				Contract.OperatorServiceEntry.COLUMN_SERVICE_ID + " = " + id,
				null, null, null, null);
		if (cursor.moveToFirst())
			service = new OperatorService(cursor, c);
		cursor.close();
		database.close();
		return service;
	}
}
