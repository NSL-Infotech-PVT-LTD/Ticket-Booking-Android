package com.surpriseme.user.util

import android.content.Context
import android.util.Log
import com.surpriseme.user.fragments.chatFragment.IOnMessageReceived
import com.surpriseme.user.fragments.chatListfragment.ChatDetailModel
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object ConnectionManger : WebSocketListener() {

    var listener: IOnMessageReceived? = null
    private var webSocket: WebSocket? = null
    var shared: PrefrenceShared? = null
    var context: Context? = null
    // val client = OkHttpClient()
    val request = Request.Builder().url("ws://23.20.179.178:8080").build()
    val client = OkHttpClient.Builder()
        .connectTimeout(180, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    fun instantiateWebSocket() {
// val socketListener = this
        webSocket = client.newWebSocket(request, this)

    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
    }

    fun sendMessage(message: String,sender :String
                    ,receive:String

//                    ,attachment:String
//                    ,senderrole:String
//                    ,wordaverage:Int
//                    ,request_id:String
    ) {

        if (message.isNotEmpty()) {
            val jsonObject = JSONObject()
            val model = ChatDetailModel()
            try {
                jsonObject.put("sender_id", sender)
//                jsonObject.put("attachment", attachment)
                jsonObject.put("receiver_id",receive)
                jsonObject.put("message", message)
                jsonObject.put("type", "text")
//                jsonObject.put("sender_role", senderrole)
//                jsonObject.put("word_average", wordaverage)
//                jsonObject.put("request_id", request_id)
//                jsonObject.put("device_type", "android")
                jsonObject.put("local_message_id", System.currentTimeMillis())

                Log.d("onetoOnChat", "onViewCreated: " + jsonObject)
                val messageJson = JSONObject()
                messageJson.put("message", jsonObject)

                webSocket?.send(jsonObject.toString())
// messageBox!!.setText("")
// adapter!!.addItem(model)jsonObject.put("type", "attachment")
// jsonObject.put("attachment", "image")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.e("","")
    }
    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.e("","")
        listener?.onMessage(text)
    }
}