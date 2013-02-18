package org.hearingthevoice.innerlife.ui.adapter;

import java.util.List;
import java.util.Map;

import org.hearingthevoice.innerlife.R;
import org.hearingthevoice.innerlife.model.Question;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class QuestionListAdapter extends ArrayAdapter<Question>
{
	private Context context;
	private int rowLayoutResourceID;
	
	private Map<Long, String> responses;
	
	public QuestionListAdapter(Context context, int rowLayoutResourceID, List<Question> questionList, Map<Long, String> responses)
	{
		super(context, rowLayoutResourceID, questionList);
		this.context = context;
		this.rowLayoutResourceID = rowLayoutResourceID;
		
		this.responses = responses;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{		
		View v = convertView;
		ViewHolder holder;
				
		if (v == null)
		{			
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			v = inflater.inflate(rowLayoutResourceID, parent, false);
						
			holder = new ViewHolder();

			holder.txtHeader = (TextView) v.findViewById(R.id.txtQuestionHeader);
			holder.txtDescription = (TextView) v.findViewById(R.id.txtQuestionDescription);
			holder.txtResponse = (TextView) v.findViewById(R.id.txtQuestionResponse);
			
			v.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) v.getTag();
		}

		Question q = getItem(position);

		if (q != null)
		{
			if (holder.txtHeader != null)
			{
				holder.txtHeader.setText("Section: " + q.getSectionID() + ", Question: " + q.getNumber());
			}
			
			if (holder.txtDescription != null)
			{
				holder.txtDescription.setText(q.getDescription());
			}
			
			if (holder.txtResponse != null && responses != null)
			{
				holder.txtResponse.setText(""+responses.get(q.getQuestionID()));
			}
		}
		
		return v;
	}
	
	private static class ViewHolder
	{
		public TextView txtHeader;
		public TextView txtDescription;
		public TextView txtResponse;
	}
}
