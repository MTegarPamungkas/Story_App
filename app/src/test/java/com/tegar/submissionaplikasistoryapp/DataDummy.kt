package com.tegar.submissionaplikasistoryapp

import com.tegar.submissionaplikasistoryapp.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoriesResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "photoURL $i",
                "29102023 $i",
                "name $i",
                "Description $i",
                16.0,
                "id $i",
                15.0
            )
            items.add(story)
        }
        return items
    }
}