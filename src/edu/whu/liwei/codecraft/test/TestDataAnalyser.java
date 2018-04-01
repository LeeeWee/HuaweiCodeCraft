package edu.whu.liwei.codecraft.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class TestDataAnalyser {
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static String beginDateStr;
	public static String endDateStr;

	public static void main(String[] args) throws Exception {
		String testFile = "D:\\data\\2018HuaweiCodeCraft\\初赛文档\\TestData_2015_1_2015_5.txt";
		String outputDir = "D:\\data\\2018HuaweiCodeCraft\\初赛文档\\data_analyse_charts";
		HashMap<String, List<Integer>> testData = reorganizeTestData(testFile);
		
		savePlotPicture(testData, outputDir);
	}
	
	public static void plotTestData(HashMap<String, List<Integer>> testData) {
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		for (Entry<String, List<Integer>> entry : testData.entrySet()) {
			final TimeSeries series = new TimeSeries(entry.getKey());
			Day day = Day.parseDay(beginDateStr);
			for (int i = 0; i < entry.getValue().size(); i++) {
				series.add(day, entry.getValue().get(i));
				day = (Day)day.next();
			}
			dataset.addSeries(series);
		}
		JFreeChart timechart = ChartFactory.createTimeSeriesChart(
				"flavors",
				"day",
				"count",
				dataset,
				true,
				true,
				false);
		
		 final ChartPanel panel = new ChartPanel(timechart);

        final JFrame f = new JFrame();
        f.add(panel);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.pack();

        f.setVisible(true);
	}
	
	public static void savePlotPicture(HashMap<String, List<Integer>> testData, String outputDir) {
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		for (Entry<String, List<Integer>> entry : testData.entrySet()) {
			final TimeSeries series = new TimeSeries(entry.getKey());
			String[] values = beginDateStr.split("-");
			Day day = new Day(Integer.parseInt(values[2]), Integer.parseInt(values[1]), Integer.parseInt(values[0]));
			for (int i = 0; i < entry.getValue().size(); i++) {
				series.add(day, entry.getValue().get(i));
				day = (Day)day.next();
			}
			dataset.addSeries(series);
			
			final XYDataset singleDataSet =( XYDataset )new TimeSeriesCollection(series);
			JFreeChart timechart = ChartFactory.createTimeSeriesChart(
				entry.getKey(), 
				"Seconds", 
				"Value", 
				singleDataSet,
				false, 
				false, 
				false);
			int width = 1120;   /* Width of the image */
			int height = 740;  /* Height of the image */ 
			File timeChart = new File(outputDir, entry.getKey() + "_chart.jpeg"); 
			try {
				ChartUtilities.saveChartAsJPEG( timeChart, timechart, width, height );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		JFreeChart timechart = ChartFactory.createTimeSeriesChart(
			"flavors",
			"day",
			"count",
			dataset,
			true,
			true,
			false);
		
		int width = 1120;   /* Width of the image */
		int height = 740;  /* Height of the image */ 
		File timeChart = new File(outputDir,  "merged_chart.jpeg" ); 
		try {
			ChartUtilities.saveChartAsJPEG( timeChart, timechart, width, height );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static HashMap<String, List<Integer>> reorganizeTestData(String testData) {
		HashMap<String, List<Integer>> result = new HashMap<String, List<Integer>>();
		HashMap<String, List<String>> flavorDataMap = new HashMap<String, List<String>>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(testData));
			String line = reader.readLine();
			beginDateStr = line.split("\t")[2].split(" ")[0];
			String flavorName = "";
			String date = "";
			while (true) {
				if (line == null) {
					endDateStr = date;
					break;
				}
				if (line.isEmpty())
					continue;
				String[] values = line.split("\t");
				flavorName = values[1];
				date = values[2].split(" ")[0];
				if (!flavorDataMap.containsKey(flavorName)) {
					List<String> dataList = new ArrayList<String>();
					flavorDataMap.put(flavorName, dataList);
				}
				flavorDataMap.get(flavorName).add(date);
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		for (Entry<String, List<String>> entry : flavorDataMap.entrySet()) {
			List<Integer> flavorCount = new ArrayList<Integer>();
			try {
				Date beginDate = sdf.parse(beginDateStr);
				Date endDate = sdf.parse(endDateStr);
				List<String> dateList = entry.getValue();
				Date tempDate = beginDate;
				int index = 0;
				while (tempDate.compareTo(endDate) <= 0) {
					String tempDateStr = sdf.format(tempDate);
					if (index >= dateList.size() || (!tempDateStr.equals(dateList.get(index)))) {
						flavorCount.add(0);
					} else {
						int count = 0;
						do {
							index++;
							count++;
						} while (index < dateList.size() && dateList.get(index).equals(tempDateStr));
							
						flavorCount.add(count);
					}
					tempDate = addDays(tempDate, 1);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			result.put(entry.getKey(), flavorCount);
		}
		return result;
	}
	
	public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
	
}
