package org.hearingthevoice.innerlife;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.util.Log;

public class QuestionAPI
{	
	private static final String INNER_LIFE_BASE_URL = "http://www.dur.ac.uk/matthew.bates/HearingTheVoice/";
	
	public static InputStream getHTTPResponseStream(String url, String httpMethod,
			byte[] postData) throws IOException
	{
		InputStream inputStream = null;
		int responseCode = -1;

		URLConnection urlConnection = (new URL(INNER_LIFE_BASE_URL + url)).openConnection();

		if (!(urlConnection instanceof HttpURLConnection))
			throw new IOException("Not an HTTP connection");

		HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
		httpConnection.setAllowUserInteraction(false);
		httpConnection.setInstanceFollowRedirects(true);
		httpConnection.setRequestMethod(httpMethod);

		if (httpMethod.equalsIgnoreCase("POST") && postData != null)
		{
			httpConnection.setRequestProperty("Content-type", "application/xml");
			httpConnection.setDoOutput(true);
			OutputStream requestOutput = httpConnection.getOutputStream();
			requestOutput.write(postData);
			requestOutput.close();
		}
		
		httpConnection.connect();
		responseCode = httpConnection.getResponseCode();
		
		Log.d("RESPONSE_CODE", ""+responseCode);
		
		inputStream = httpConnection.getInputStream();

		return inputStream;
	}

	public static List<Section> downloadQuestionXML(Context context, String urlExtension)
			throws MalformedURLException, IOException, ProtocolException, FactoryConfigurationError
			{
		List<Section> questionList = new ArrayList<Section>();

		URL url = new URL(INNER_LIFE_BASE_URL + urlExtension);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);

		conn.connect();
		int httpResponseCode = conn.getResponseCode();

		if (httpResponseCode != HttpURLConnection.HTTP_OK) throw new IOException();

		InputStream inputStream = conn.getInputStream();

		Scanner scanner = new Scanner(inputStream);

		FileOutputStream fos = context.openFileOutput("questions", Context.MODE_PRIVATE);

		while(scanner.hasNextLine()) fos.write(scanner.nextLine().getBytes());

		fos.close();

		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();

			QuestionXMLParser myXMLHandler = new QuestionXMLParser(questionList);

			Log.d("QUESTION_LIST", ""+questionList.size());

			reader.setContentHandler(myXMLHandler);
			reader.parse(new InputSource(inputStream));

			Log.d("QUESTION_LIST", ""+questionList.size());
		}
		catch (SAXException e)
		{
			e.printStackTrace();
			throw new IOException();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			throw new IOException();
		}
		finally
		{
			inputStream.close();
			conn.disconnect();
		}

		return questionList;
			}

	public static List<Section> retrieveCachedQuestions(Context context)
			throws MalformedURLException, IOException, ProtocolException, FactoryConfigurationError
	{
		List<Section> questionList = new ArrayList<Section>();

		FileInputStream inputStream = context.openFileInput("questions");

		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();

			QuestionXMLParser myXMLHandler = new QuestionXMLParser(questionList);

			Log.d("QUESTION_LIST", ""+questionList.size());

			reader.setContentHandler(myXMLHandler);
			reader.parse(new InputSource(inputStream));

			Log.d("QUESTION_LIST", ""+questionList.size());
		}
		catch (SAXException e)
		{
			e.printStackTrace();
			throw new IOException();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			throw new IOException();
		}
		finally
		{
			inputStream.close();
		}

		return questionList;
	}

	public static Schedule downloadScheduleXML(Context context, String urlExtension)
			throws MalformedURLException, IOException, ProtocolException, FactoryConfigurationError
	{
		Schedule schedule = new Schedule();

		URL url = new URL(INNER_LIFE_BASE_URL + urlExtension);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);

		conn.connect();
		int httpResponseCode = conn.getResponseCode();

		if (httpResponseCode != HttpURLConnection.HTTP_OK) throw new IOException();

		InputStream inputStream = conn.getInputStream();

		Scanner scanner = new Scanner(inputStream);

		FileOutputStream fos = context.openFileOutput("schedule", Context.MODE_PRIVATE);

		while(scanner.hasNextLine()) fos.write(scanner.nextLine().getBytes());

		fos.close();

		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();

			ScheduleXMLParser myXMLHandler = new ScheduleXMLParser(schedule);

			reader.setContentHandler(myXMLHandler);
			reader.parse(new InputSource(inputStream));
		}
		catch (SAXException e)
		{
			e.printStackTrace();
			throw new IOException();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			throw new IOException();
		}
		finally
		{
			inputStream.close();
			conn.disconnect();
		}

		return schedule;
	}
	
	public static Schedule retrieveCachedSchedule(Context context)
			throws MalformedURLException, IOException, ProtocolException, FactoryConfigurationError
	{
		Schedule schedule = new Schedule();

		FileInputStream inputStream = context.openFileInput("schedule");

		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();

			ScheduleXMLParser myXMLHandler = new ScheduleXMLParser(schedule);

			reader.setContentHandler(myXMLHandler);
			reader.parse(new InputSource(inputStream));
		}
		catch (SAXException e)
		{
			e.printStackTrace();
			throw new IOException();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			throw new IOException();
		}
		finally
		{
			inputStream.close();
		}

		return schedule;
	}
}
