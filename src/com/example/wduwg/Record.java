package com.example.wduwg;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class Record {
	public int totalMale;
	public int totalFemale;

	public static List<Record[]> data = new ArrayList<Record[]>();

	public Record() {

	}

	public void getData(ParseObject event) {

		try {
			ParseQuery<ParseObject> gatewayQuery = ParseQuery
					.getQuery("Gateway");
			gatewayQuery.whereEqualTo("parent", event);
			gatewayQuery.orderByAscending("createdAt");
			List<ParseObject> gateways;
			gateways = gatewayQuery.find();

			int skip = 0;
			for (int i = 0; i < 10; i++) {
				Record[] r = new Record[gateways.size()];
				int c = 0;
				for (int k = 0; k < gateways.size(); k++) {
					r[c] = new Record();
					r[c].totalMale = 0;
					ParseQuery<ParseObject> counterQuery = ParseQuery
							.getQuery("Counter");
					counterQuery.whereEqualTo("parent", gateways.get(k));
					counterQuery.whereEqualTo("type", "Male");
					counterQuery.orderByAscending("time");
					counterQuery.setSkip(skip);
					counterQuery.setLimit(5);
					List<ParseObject> counterResults = counterQuery.find();
					for (ParseObject counterM : counterResults) {
						try {
							r[c].totalMale += Integer.parseInt(counterM
									.getString("MenIn"));
						} catch (Exception ex) {
							Log.d("Err Male:===", ex.getMessage());
						}

					}
					Log.d("record Male:===", "" + r[c].totalMale);
					r[c].totalFemale = 0;
					ParseQuery<ParseObject> counterQuery1 = ParseQuery
							.getQuery("Counter");
					counterQuery1.whereEqualTo("parent", gateways.get(k));
					counterQuery1.whereEqualTo("type", "Female");
					counterQuery1.orderByAscending("time");
					counterQuery1.setLimit(5);
					counterQuery1.setSkip(skip);
					List<ParseObject> counterResults1 = counterQuery1.find();
					for (ParseObject counterF : counterResults1) {
						try {
							r[c].totalFemale += Integer.parseInt(counterF
									.getString("WomenIn"));
						} catch (Exception ex) {
							Log.d("Err feMale:===", ex.getMessage());
						}
					}
					Log.d("Record Female===", "" + r[c].totalFemale);
					c = c + 1;

				}
				skip += 5;
				data.add(r);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

}
