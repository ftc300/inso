package com.inshow.watch.android.view.indexable;


import java.util.Comparator;

/**
 * Created by chendong on 2017/10/24.
 */
class ContactComparator<T extends IndexableEntity> implements Comparator<EntityWrapper<T>> {

    @Override
    public int compare(EntityWrapper<T> lhs, EntityWrapper<T> rhs) {
        return lhs.getPinyin().compareTo(rhs.getPinyin());
    }

}
