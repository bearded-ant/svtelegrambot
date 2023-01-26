package com.ant.svtelegrambot.firebase

import com.ant.svtelegrambot.model.ChatMember
import com.ant.svtelegrambot.model.ResourceLocation
import com.google.firebase.database.*
import org.springframework.stereotype.Component
import java.util.concurrent.Semaphore

@Component
class FirebaseRepo {

    private val dbInstance = FirebaseDatabase.getInstance()
    val dbLocationsRef = dbInstance.getReference("locstatus")
    val dbUserRef = dbInstance.getReference("chatMember")


    fun getLocation(callBack: LocationsCallback) {
        val location = mutableListOf<ResourceLocation>()

        dbLocationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    location.add(data.getValue(ResourceLocation::class.java))
                }
                callBack.onLocationCallback(location)
            }

            override fun onCancelled(error: DatabaseError) {
                println("error status loading ${error.message} ")
            }
        })
    }

    fun getChatMember(callBack: ChatMembersCallback) {
        val chatMembers = mutableListOf<ChatMember>()
        val semaphore = Semaphore(0)
        dbUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    chatMembers.add(data.getValue(ChatMember::class.java))
                }
                callBack.onChatMemberCallback(chatMembers)
                semaphore.release()
            }

            override fun onCancelled(error: DatabaseError) {
                println("error status loading ${error.message} ")
            }
        })
        semaphore.acquire()
    }
}