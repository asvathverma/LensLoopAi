package com.example.ui.dm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DMListState(
    val conversations: List<Conversation> = emptyList(),
    val requests: List<Conversation> = emptyList(),
    val isLoading: Boolean = true
)

data class DMChatState(
    val conversation: Conversation? = null,
    val currentUserId: String = "user_me",
    val inputText: String = "",
    val isRecordingVoice: Boolean = false,
    val isVanishMode: Boolean = false,
    val replyingToMessage: Message? = null
)

class DMViewModel : ViewModel() {
    private val _listState = MutableStateFlow(DMListState())
    val listState: StateFlow<DMListState> = _listState.asStateFlow()

    private val _chatState = MutableStateFlow(DMChatState())
    val chatState: StateFlow<DMChatState> = _chatState.asStateFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        val mockConvos = listOf(
            Conversation(
                id = "c1",
                name = "priya_sharma",
                iconUrl = "P",
                participants = listOf("user_me", "priya"),
                messages = listOf(
                    Message(id = "m1", senderId = "priya", text = "Hey! Kaisi ho?", timestamp = System.currentTimeMillis() - 1500000, status = MessageStatus.READ),
                    Message(id = "m2", senderId = "user_me", text = "Main bilkul theek! Tum batao?", timestamp = System.currentTimeMillis() - 1380000, status = MessageStatus.READ),
                    Message(id = "m3", senderId = "priya", text = "Photo bhejo na \uD83D\uDCF8", timestamp = System.currentTimeMillis() - 120000)
                ),
                unreadCount = 3,
                isE2EEnabled = false,
                isOnline = true
            ),
            Conversation(
                id = "c2",
                name = "rahul_vlogs",
                iconUrl = "R",
                participants = listOf("user_me", "rahul"),
                messages = listOf(
                    Message(id = "m4", senderId = "rahul", text = "Kal live aayega?", timestamp = System.currentTimeMillis() - 900000)
                ),
                unreadCount = 0,
                isOnline = false
            ),
            Conversation(
                id = "c3",
                name = "neha_travels",
                iconUrl = "N",
                participants = listOf("user_me", "neha"),
                messages = listOf(
                    Message(id = "m5", senderId = "neha", text = "Goa trip plan kar rahe hain", timestamp = System.currentTimeMillis() - 3600000)
                ),
                unreadCount = 1,
                isOnline = true
            ),
            Conversation(
                id = "c4",
                name = "tech_guru",
                iconUrl = "T",
                participants = listOf("user_me", "tech"),
                messages = listOf(
                    Message(id = "m6", senderId = "tech", text = "Reel viral ho gayi! \uD83D\uDD25", timestamp = System.currentTimeMillis() - 10800000)
                ),
                unreadCount = 0,
                isOnline = false
            )
        )
        
        val mockRequests = emptyList<Conversation>()
        
        _listState.update { it.copy(conversations = mockConvos, requests = mockRequests, isLoading = false) }
    }

    fun openConversation(id: String) {
        val convo = _listState.value.conversations.find { it.id == id }
        _chatState.update { it.copy(conversation = convo, isVanishMode = convo?.isVanishModeActive ?: false) }
        _listState.update { state ->
            val updated = state.conversations.map { if (it.id == id) it.copy(unreadCount = 0) else it }
            state.copy(conversations = updated)
        }
    }

    fun updateInput(text: String) {
        _chatState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _chatState.value.inputText
        if (text.isBlank()) return
        
        val newMsg = Message(
            senderId = "user_me",
            text = text,
            isVanishMode = _chatState.value.isVanishMode,
            replyToMessageId = _chatState.value.replyingToMessage?.id
        )
        
        _chatState.update { state ->
            val convo = state.conversation ?: return@update state
            val updatedConvo = convo.copy(messages = convo.messages + newMsg)
            state.copy(conversation = updatedConvo, inputText = "", replyingToMessage = null)
        }
        
        updateConversationInList(_chatState.value.conversation!!)
    }
    
    fun sendVoiceMessage() {
         val newMsg = Message(
            senderId = "user_me",
            type = MessageType.VOICE,
            durationSec = 5,
            isVanishMode = _chatState.value.isVanishMode
        )
        _chatState.update { state ->
            val convo = state.conversation ?: return@update state
            val updatedConvo = convo.copy(messages = convo.messages + newMsg)
            state.copy(conversation = updatedConvo)
        }
        updateConversationInList(_chatState.value.conversation!!)
    }

    fun toggleReadReceipts() {
        _chatState.update { state ->
            val convo = state.conversation ?: return@update state
            val updated = convo.copy(readReceiptsEnabled = !convo.readReceiptsEnabled)
            state.copy(conversation = updated)
        }
        _chatState.value.conversation?.let { updateConversationInList(it) }
    }
    
    fun toggleVanishMode() {
        _chatState.update { state ->
            val isVanish = !state.isVanishMode
            val convo = state.conversation?.copy(isVanishModeActive = isVanish)
            state.copy(isVanishMode = isVanish, conversation = convo)
        }
        _chatState.value.conversation?.let { updateConversationInList(it) }
    }
    
    fun acceptRequest(convoId: String) {
        _listState.update { state ->
            val request = state.requests.find { it.id == convoId } ?: return@update state
            val acceptedConvo = request.copy(isRequest = false)
            state.copy(
                requests = state.requests.filter { it.id != convoId },
                conversations = state.conversations + acceptedConvo
            )
        }
    }
    
    fun setReplyTo(message: Message?) {
        _chatState.update { it.copy(replyingToMessage = message) }
    }
    
    fun addReaction(messageId: String, emoji: String) {
        _chatState.update { state ->
            val convo = state.conversation ?: return@update state
            val updatedMessages = convo.messages.map { msg ->
                if (msg.id == messageId) {
                    val newReactions = msg.reactions.toMutableMap().apply { put(state.currentUserId, emoji) }
                    msg.copy(reactions = newReactions)
                } else {
                    msg
                }
            }
            state.copy(conversation = convo.copy(messages = updatedMessages))
        }
        _chatState.value.conversation?.let { updateConversationInList(it) }
    }
    
    private fun updateConversationInList(convo: Conversation) {
        _listState.update { state ->
            val updated = state.conversations.map { if (it.id == convo.id) convo else it }
            state.copy(conversations = updated)
        }
    }
}
