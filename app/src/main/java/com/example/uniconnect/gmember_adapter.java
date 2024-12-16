package com.example.uniconnect;

import static android.view.View.GONE;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class gmember_adapter extends ArrayAdapter<Member> {

    private Context context;
    private List<Member> members;

    public gmember_adapter(Context context, List<Member> members) {
        super(context, R.layout.gmember_item, members);
        this.context = context;
        this.members = members;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.gmember_item, parent, false);
        }

        Member member = members.get(position);

        TextView nameTextView = convertView.findViewById(R.id.memberName);
        TextView idTextView = convertView.findViewById(R.id.memberId);

        if (member != null) {
            nameTextView.setText(member.getName());
            String idd = member.getId();

            if (idd != null && !idd.equals("null")) {
                idTextView.setText(Html.fromHtml("<b>Roll/ID: </b>" + idd));
            } else {
                idTextView.setVisibility(GONE);
            }
        }

        return convertView;
    }
}
