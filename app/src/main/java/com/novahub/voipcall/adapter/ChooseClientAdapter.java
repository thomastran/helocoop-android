package com.novahub.voipcall.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.novahub.voipcall.R;

import java.util.List;

/**
 * Created by samnguyen on 24/12/2015.
 */
public class ChooseClientAdapter extends RecyclerView.Adapter<ChooseClientAdapter.ViewHolder> {

    private List<String> listContacts;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    private static OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        public TextView textViewContact;
        public CheckBox checkBox;
        public ViewHolder(final View itemView) {
            super(itemView);
            textViewContact = (TextView) itemView.findViewById(R.id.textViewContact);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onItemClick(checkBox, getLayoutPosition());
                }
            });

        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChooseClientAdapter(List<String> listContacts) {
        this.listContacts = listContacts;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChooseClientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detailed_choose_client_to_call, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textViewContact.setText(this.listContacts.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listContacts.size();
    }
}

