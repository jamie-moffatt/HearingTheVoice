package org.hearingthevoice.innerlife;

import java.util.ArrayList;
import java.util.List;

public class Schedule
{
	private List<List<Long>> sessions;
	
	public Schedule()
	{
		sessions = new ArrayList<List<Long>>();
	}
	
	public void addSession(List<Long> session) { sessions.add(session); }
	
	public List<Long> getSession(int sessionID) { return sessions.get(sessionID); }
	
	public List<Section> filterBySession(List<Section> sections, int sessionID)
	{
		List<Long> session = getSession(sessionID);
		
		List<Section> _sections = new ArrayList<Section>();
		
		for(Section section : sections) if(session.contains(section.sectionID)) _sections.add(section);
		
		return _sections;
	}
	
	public int numberOfSessions() { return sessions.size(); }
	
	public String toString()
	{
		String str = "Schedule: \n";
		
		for(int i = 0; i < sessions.size(); i++)
		{
			str = str.concat("Session: " + i + ": " + sessions.get(i) + "\n");
		}
		
		return str;
	}
}
