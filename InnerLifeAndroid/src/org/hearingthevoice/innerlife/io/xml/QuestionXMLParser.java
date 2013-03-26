package org.hearingthevoice.innerlife.io.xml;

import java.util.ArrayList;
import java.util.List;

import org.hearingthevoice.innerlife.model.Question;
import org.hearingthevoice.innerlife.model.Section;
import org.hearingthevoice.innerlife.model.Question.QuestionType;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Pair;

public class QuestionXMLParser extends DefaultHandler
{
	private Section section;
	private List<Section> sectionList;
	private Question question;
	private List<Question> questionList;
	
	private List<Pair<String, String>> responses;

	public QuestionXMLParser(List<Section> sectionList)
	{
		question = new Question();
		this.sectionList = sectionList;
		
		questionList = new ArrayList<Question>();
		responses = new ArrayList<Pair<String,String>>();
		
	}

	@Override
	public void startElement(String URI, String localName, String qName, Attributes attributes)
	{
		if (localName.equalsIgnoreCase("section"))
		{
			section = new Section();
			
			section.setSectionID(Long.parseLong(attributes.getValue("id")));
			section.setName(attributes.getValue("name"));
			section.setDescription(attributes.getValue("description"));
		}
		else if (localName.equalsIgnoreCase("questions"))
		{
			questionList = new ArrayList<Question>();
		}
		else if (localName.equalsIgnoreCase("question"))
		{
			question = new Question();
			
			question.setQuestionID(Long.parseLong(attributes.getValue("id")));
			question.setSectionID(section.getSectionID());
			question.setNumber(Long.parseLong(attributes.getValue("number")));
			
			question.setType(QuestionType.parseType(attributes.getValue("type")));
			question.setDescription(attributes.getValue("description"));
			
			questionList.add(question);
			
		}
		else if (localName.equalsIgnoreCase("choices"))
		{
			responses = new ArrayList<Pair<String,String>>();
		}
		else if (localName.equalsIgnoreCase("choice"))
		{
			responses.add(Pair.create(attributes.getValue("text"), attributes.getValue("value")));
		}
	}

	@Override
	public void endElement(String URI, String localName, String qName)
	{
		     if (localName.equalsIgnoreCase("section"  ))
		    {
		    	 sectionList.add(section);
		    }
		else if (localName.equalsIgnoreCase("questions"))
		{
			section.setQuestions(questionList);
		}
		else if (localName.equalsIgnoreCase("choices"  ))
		{
			section.setResponses(responses);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
	{
		
	}

}
