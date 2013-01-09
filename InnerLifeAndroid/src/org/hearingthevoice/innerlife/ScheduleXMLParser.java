package org.hearingthevoice.innerlife;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class ScheduleXMLParser extends DefaultHandler
{
	private Schedule schedule;
	private List<Long> session;

	public ScheduleXMLParser(Schedule schedule)
	{
		this.schedule = schedule;
		session = new ArrayList<Long>();
		
	}

	@Override
	public void startElement(String URI, String localName, String qName, Attributes attributes)
	{
		if (localName.equalsIgnoreCase("session"))
		{
			session = new ArrayList<Long>();
		}
		else if (localName.equalsIgnoreCase("section"))
		{
			long section = Long.parseLong(attributes.getValue("id"));
			session.add(section);
		}
	}

	@Override
	public void endElement(String URI, String localName, String qName)
	{
		if (localName.equalsIgnoreCase("session"))
		{
			schedule.addSession(session);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
	{
		
	}
}
