package com.e3lue.us.ui.waveside;

import com.e3lue.us.model.ContactPerson;

import java.util.Comparator;

/**
 * Created by Leo on 2017/4/12.
 */

public class LetterComparator implements Comparator<ContactPerson> {

    @Override
    public int compare(ContactPerson contactModel, ContactPerson t1) {
        if (contactModel == null || t1 == null){
            return 0;
        }
        String lhsSortLetters = contactModel.getIndex().substring(0, 1).toUpperCase();
        String rhsSortLetters = t1.getIndex().substring(0, 1).toUpperCase();
        return lhsSortLetters.compareTo(rhsSortLetters);
    }
}

