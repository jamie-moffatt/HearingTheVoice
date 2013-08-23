package org.hearingthevoice.innerlife.model;

import java.util.ArrayList;
import java.util.List;

import android.util.Pair;

public class Section
{
	public static final int START_TRAIT_SESSION_ID = 29;
	public static final int MIDDLE_TRAIT_SESSION_ID = 30;
	public static final int END_TRAIT_SESSION_ID = 31;
	
	long sectionID;
	String name;
	String description;
	
	List<Question> questions;
	List<Pair<String, String>> responses;

	public Section()
	{
		questions = new ArrayList<Question>();
		responses = new ArrayList<Pair<String,String>>();
	}

	public long getSectionID() {
		return sectionID;
	}

	public void setSectionID(long sectionID) {
		this.sectionID = sectionID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public List<Pair<String,String>> getResponses() {
		return responses;
	}

	public void setResponses(List<Pair<String,String>> responses) {
		this.responses = responses;
	}
	
	public String toString()
	{
		return "(" + sectionID + "," + name + ")";
	}
}
