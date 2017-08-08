package com.e3lue.us.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e3lue.us.R;
import com.e3lue.us.model.ContactPerson;
import com.e3lue.us.ui.UIHelper;

import java.util.List;

/**
 * Created by Leo on 2017/4/12.
 */

public class ContactPersonAdapter extends RecyclerView.Adapter<ContactPersonAdapter.ContactsViewHolder> {

    private List<ContactPerson> contacts;
    private static final String TAG = "ContactsAdapter";
    Activity context;
    int type;

    public ContactPersonAdapter(Activity context, List<ContactPerson> contacts, int type) {
        this.contacts = contacts;
        this.type=type;
        this.context = context;
    }

    public void setContacts(List<ContactPerson> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.contactperson_item, null);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactsViewHolder holder, final int position) {
        final ContactPerson contact = contacts.get(position);

        if (position == 0 || !contacts.get(position - 1).getIndex().equals(contact.getIndex())) {
            holder.tvIndex.setVisibility(View.VISIBLE);
            holder.tvIndex.setText(contact.getIndex());
        } else {
            holder.tvIndex.setVisibility(View.GONE);
        }
        String cname = contact.getCompanyName() == null ? "" : contact.getCompanyName();
        holder.tvName.setText(Html.fromHtml("<font color='#424242'><big>" + contact.getName() + "</big></font> &#160;<font color='gray' ><small>" + cname + "</small></font>"));
        holder.relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type==0)
                UIHelper.showContactPersonDetail(context, contact.getID());
                else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("contact", contact);
                    intent.putExtras(bundle);
                    context.setResult(102, intent);
                    context.finish();
                }
            }
        });
        holder.relative.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (type==1)return true;
                Intent myInt = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + contact.getMobile()));
                context.startActivity(myInt);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex;
        ImageView ivAvatar;
        TextView tvName;
        RelativeLayout relative;

        ContactsViewHolder(View itemView) {
            super(itemView);
            tvIndex = (TextView) itemView.findViewById(R.id.contactperson_index);
            ivAvatar = (ImageView) itemView.findViewById(R.id.contactperson_avatar);
            tvName = (TextView) itemView.findViewById(R.id.contactperson_name);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
        }
    }
}
