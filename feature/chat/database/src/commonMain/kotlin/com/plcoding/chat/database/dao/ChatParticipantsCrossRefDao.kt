package com.plcoding.chat.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.plcoding.chat.database.entities.ChatParticipantCrossRef
import com.plcoding.chat.database.entities.ChatParticipantEntity

@Dao
interface ChatParticipantsCrossRefDao {
    
    @Upsert
    suspend fun upsertCrossRefs(crossRefs: List<ChatParticipantCrossRef>)
    
    @Query("SELECT userId FROM chatparticipantcrossref WHERE chatId = :chatId AND isActive = TRUE")
    suspend fun getActiveParticipantsIdByChat(chatId: String): List<String>
    
    @Query("SELECT userId FROM chatparticipantcrossref WHERE chatId = :chatId")
    suspend fun getAllParticipantsIdByChat(chatId: String): List<String>
    
    @Query("""
        UPDATE chatparticipantcrossref
        SET isActive = FALSE
        WHERE chatId = :chatId AND userId IN (:userIds)
        """)
    suspend fun markParticipantsAsInactive(chatId: String, userIds: List<String>)
    
    @Query("""
        UPDATE chatparticipantcrossref
        SET isActive = TRUE
        WHERE chatId = :chatId AND userId IN (:userIds)
        """)
    suspend fun reactivateParticipants(chatId: String, userIds: List<String>)
    
    @Transaction
    suspend fun syncChatParticipants(chatId: String, participants: List<ChatParticipantEntity>) {
        if (participants.isEmpty()) {
            return
        }
        
        val serverParticipantIds = participants.map { it.userId }.toSet()
        val allLocalParticipantIds = getAllParticipantsIdByChat(chatId).toSet()
        val activeLocalParticipantIds = getActiveParticipantsIdByChat(chatId).toSet()
        val inactiveLocalParticipantIds = allLocalParticipantIds - activeLocalParticipantIds
        
        val participantsToReactivate = serverParticipantIds.intersect(inactiveLocalParticipantIds)
        val participantsToDeactivate = activeLocalParticipantIds - serverParticipantIds
        
        reactivateParticipants(chatId, participantsToReactivate.toList())
        markParticipantsAsInactive(chatId, participantsToDeactivate.toList())
        
        val completelyNewParticipantIds = serverParticipantIds - allLocalParticipantIds
        val newCrossRefs = completelyNewParticipantIds.map { userId ->
            ChatParticipantCrossRef(
                chatId = chatId,
                userId = userId,
                isActive = true,
            )
        }
        upsertCrossRefs(newCrossRefs)
    }
}







