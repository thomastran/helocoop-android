package com.novahub.voipcall.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.novahub.voipcall.R;
import com.novahub.voipcall.model.Distance;

import java.util.List;

/**
 * Created by samnguyen on 19/01/2016.
 */
public class ConnectedPeopleAdapter extends RecyclerView.Adapter<ConnectedPeopleAdapter.ViewHolder> {

    private List<Distance> distanceList;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    private static OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(String value, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        public TextView textViewName;
        public TextView textViewDescription;
        public TextView textViewLiving;
        public TextView textViewDistance;
        public RadioGroup radioGroup;
        private final String Abusive = "Abusive";
        private final String Not_Good = "Not Good";
        private final String Ok = "Ok";
        private final String Good = "Good";

        public ViewHolder(final View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewDescription = (TextView) itemView.findViewById(R.id.textViewDescription);
            textViewLiving = (TextView) itemView.findViewById(R.id.textViewLiving);
            textViewDistance = (TextView) itemView.findViewById(R.id.textViewDistance);
            radioGroup = (RadioGroup) itemView.findViewById(R.id.radioGroup);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (listener != null) {
                        String estimation;
                        switch (checkedId) {
                            case R.id.radioButtonAbusive:
                                estimation = Abusive;
                                break;
                            case R.id.radioButtonNotGood:
                                estimation = Not_Good;
                                break;
                            case R.id.radioButtonOk:
                                estimation = Ok;
                                break;
                            case R.id.radioButtonGood:
                                estimation = Good;
                                break;
                            default: estimation = Good;
                        }
                        listener.onItemClick(estimation, getLayoutPosition());
                    }
                }
            });
        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ConnectedPeopleAdapter(List<Distance> distanceList) {
        this.distanceList = distanceList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ConnectedPeopleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detailed_connected_people, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textViewName.setText(this.distanceList.get(position).getName());
        holder.textViewDescription.setText(this.distanceList.get(position).getDescription());
        holder.textViewLiving.setText(this.distanceList.get(position).getAddress());
        if (this.distanceList.get(position).getMile() > 0.0) {
            holder.textViewDistance.setText(this.distanceList.get(position).getMile() + " miles");
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return distanceList.size();
    }
}