package com.willow.android.tv.common.cards

import androidx.leanback.widget.ObjectAdapter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.PresenterSelector
import java.util.Collections

/**
 * Created by eldhosepaul on 25/04/23.
 */

/**
 * An [ObjectAdapter] implemented with an [ArrayList].
 */
class CustumArrayObjectAdapter : ObjectAdapter {
    private var mItems = ArrayList<Any>()

    /**
     * Constructs an adapter with the given [PresenterSelector].
     */
    constructor(presenterSelector: PresenterSelector?) : super(presenterSelector) {}

    /**
     * Constructs an adapter that uses the given [Presenter] for all items.
     */
    constructor(presenter: Presenter?) : super(presenter) {}

    /**
     * Constructs an adapter.
     */
    constructor() : super() {}

    override fun size(): Int {
        return mItems.size
    }

    override operator fun get(index: Int): Any {
        return mItems[index]
    }

    /**
     * Returns the index for the first occurrence of item in the adapter, or -1 if
     * not found.
     *
     * @param item  The item to find in the list.
     * @return Index of the first occurrence of the item in the adapter, or -1
     * if not found.
     */
    fun indexOf(item: Any): Int {
        return mItems.indexOf(item)
    }

    /**
     * Notify that the content of a range of items changed. Note that this is
     * not same as items being added or removed.
     *
     * @param positionStart The position of first item that has changed.
     * @param itemCount The count of how many items have changed.
     */
    fun notifyArrayItemRangeChanged(positionStart: Int, itemCount: Int) {
        notifyItemRangeChanged(positionStart, itemCount)
    }

    fun setItems(items: ArrayList<Any>){
        mItems = items
        notifyItemRangeChanged(0,1)
    }


    /**
     * Adds an item to the end of the adapter.
     *
     * @param item The item to add to the end of the adapter.
     */
    fun add(item: Any) {
        add(mItems.size, item)
    }

    /**
     * Inserts an item into this adapter at the specified index.
     * If the index is >= [.size] an exception will be thrown.
     *
     * @param index The index at which the item should be inserted.
     * @param item The item to insert into the adapter.
     */
    fun add(index: Int, item: Any) {
        mItems.add(index, item)
        notifyItemRangeInserted(index, 1)
    }

    /**
     * Adds the objects in the given collection to the adapter, starting at the
     * given index.  If the index is >= [.size] an exception will be thrown.
     *
     * @param index The index at which the items should be inserted.
     * @param items A [Collection] of items to insert.
     */
    fun addAll(index: Int, items: Collection<Any>) {
        val itemsCount = items.size
        if (itemsCount == 0) {
            return
        }
        mItems.addAll(index, items)
        notifyItemRangeInserted(index, itemsCount)
    }

    /**
     * Replaces the objects in the given collection to the adapter, starting at the
     * index 0.
     *
     * @param items A [Collection] of items to insert.
     */
    fun replaceAll(items: Collection<Any>) {
        val itemsCount = items.size
        if (itemsCount == 0) {
            return
        }
        mItems.clear()
        mItems.addAll(0, items)
        notifyItemRangeChanged(0, itemsCount)
    }

    /**
     * Removes the first occurrence of the given item from the adapter.
     *
     * @param item The item to remove from the adapter.
     * @return True if the item was found and thus removed from the adapter.
     */
    fun remove(item: Any): Boolean {
        val index = mItems.indexOf(item)
        if (index >= 0) {
            mItems.removeAt(index)
            notifyItemRangeRemoved(index, 1)
        }
        return index >= 0
    }

    /**
     * Replaces item at position with a new item and calls notifyItemRangeChanged()
     * at the given position.  Note that this method does not compare new item to
     * existing item.
     * @param position  The index of item to replace.
     * @param item      The new item to be placed at given position.
     */
    fun replace(position: Int, item: Any) {
        mItems[position] = item
        notifyItemRangeChanged(position, 1)
    }

    fun isRowExisting(row: Any): Boolean {
        for (i in 0 until  size()) {
            if (mItems[i] == row) {
                return true
            }
        }
        return false
    }

    /**
     * Removes a range of items from the adapter. The range is specified by giving
     * the starting position and the number of elements to remove.
     *
     * @param position The index of the first item to remove.
     * @param count The number of items to remove.
     * @return The number of items removed.
     */
    fun removeItems(position: Int, count: Int): Int {
        val itemsToRemove = Math.min(count, mItems.size - position)
        if (itemsToRemove <= 0) {
            return 0
        }
        for (i in 0 until itemsToRemove) {
            mItems.removeAt(position)
        }
        notifyItemRangeRemoved(position, itemsToRemove)
        return itemsToRemove
    }

    /**
     * Removes all items from this adapter, leaving it empty.
     */
    fun clear() {
        val itemCount = mItems.size
        if (itemCount == 0) {
            return
        }
        mItems.clear()
        notifyItemRangeRemoved(0, itemCount)
    }

    /**
     * Gets a read-only view of the list of object of this CustumArrayObjectAdapter.
     */
    fun <E> unmodifiableList(): List<E> {
        return Collections.unmodifiableList(mItems as List<E>)
    }
}