package org.hearingthevoice.innerlife.model;

import java.util.ArrayList;
import java.util.List;

public class Section
{
	long sectionID;
	String name;
	String description;
	
	List<Question> questions;
	List<String> responses;

	public Section()
	{
		questions = new ArrayList<Question>();
		responses = new ArrayList<String>();
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

	public List<String> getResponses() {
		return responses;
	}

	public void setResponses(List<String> responses) {
		this.responses = responses;
	}
	
	public String toString()
	{
		return "(" + sectionID + "," + name + ")";
	}
}
