package org.hearingthevoice.innerlife;


public class Question
{
	private long questionID;
	private long sectionID;
	private long number;
	
	private QuestionType type;
	
	private String description;
	
	public long getQuestionID() { return questionID; }
	
	public void setQuestionID(long questionID) { this.questionID = questionID; }

	public long getSectionID() { return sectionID; }

	public void setSectionID(long sectionID) { this.sectionID = sectionID;}

	public QuestionType getType() { return type; }

	public void setType(QuestionType type) { this.type = type; }

	public String getDescription() { return description; }

	public void setDescription(String description) { this.description = description; }

	public long getNumber() { return number; }

	public void setNumber(long number) { this.number = number; }

	public enum QuestionType
	{
		RADIO,
		YESNO,
		NUMSCALE;
		
		public static QuestionType parseType(String type)
		{
			     if(type.equalsIgnoreCase("radio"   )) return RADIO;
			else if(type.equalsIgnoreCase("yesno"   )) return YESNO;
			else if(type.equalsIgnoreCase("numscale")) return NUMSCALE;
			
			else return RADIO;
		}
	}
}
