package com.ant.svtelegrambot.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.stereotype.Component
import java.io.FileInputStream

@Component
class FireBaseInit {
//    @Value("\${firebase.tokenfile}")
//    private val tokenFile: String? = null
//
//    @Value("\${firebase.baseurl}")
//    private val baseUrl: String? = null

    val baseUrl = "https://vsmuta-f3e3d-default-rtdb.europe-west1.firebasedatabase.app"
    val tokenFile = "/home/ant/IdeaProjects/svtelegrambot/src/main/resources/vsmuta-f3e3d-firebase-adminsdk-3dzma-12edada204.json"


    private val serviceAccount = FileInputStream(tokenFile)

    fun firebaseInit() {
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl(baseUrl)
            .build()
        FirebaseApp.initializeApp(options)
    }
}