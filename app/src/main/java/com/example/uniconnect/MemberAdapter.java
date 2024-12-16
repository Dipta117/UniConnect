package com.example.uniconnect;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private Context context;
    private List<Member> members;
    private Map<String, Boolean> attendanceMap;

    public MemberAdapter(Context context, List<Member> members, Map<String, Boolean> attendanceMap) {
        this.context = context;
        this.members = members;
        this.attendanceMap = attendanceMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Member member = members.get(position);
        holder.memberName.setText(Html.fromHtml("<b>Name: </b>" + member.getName()));
        holder.memberId.setText(Html.fromHtml("<b>Roll/ID: </b>" + member.getId()));
        holder.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isPresent = checkedId == R.id.presentRadioButton;
            attendanceMap.put(member.getName(), isPresent);
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView memberName, memberId;
        RadioGroup radioGroup;
        CardView memberCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.memberName);
            memberId = itemView.findViewById(R.id.memberId);
            radioGroup = itemView.findViewById(R.id.radioGroup);

        }
    }
}
