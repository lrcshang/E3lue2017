package com.e3lue.us.utils;

import com.e3lue.us.model.ContactPerson;

import java.util.Comparator;

public class PinyinComparator implements Comparator<ContactPerson>
{

	public int compare(ContactPerson o1, ContactPerson o2)
	{
		if (o1.getIndex().equals("@") || o2.getIndex().equals("#"))
			return -1;
		else if (o1.getIndex().equals("#") || o2.getIndex().equals("@"))
			return 1;
	    else 
			return o1.getIndex().compareTo(o2.getIndex());
	}

}
