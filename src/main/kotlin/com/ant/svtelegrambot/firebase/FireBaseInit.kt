package com.ant.svtelegrambot.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.stereotype.Component
import java.io.FileInputStream

private const val BASE_URL = "https://vsmuta-f3e3d-default-rtdb.europe-west1.firebasedatabase.app"
private const val TOKEN_FILE = "/home/ant/IdeaProjects/svtelegrambot/src/main/resources/vsmuta-f3e3d-firebase-adminsdk-3dzma-12edada204.json"

@Component
class FireBaseInit {
//    @Value("\${firebase.tokenfile}")
//    private val tokenFile: String? = null
//
//    @Value("\${firebase.baseurl}")
//    private val baseUrl: String? = null

   private val serviceAccount = FileInputStream(TOKEN_FILE)

    fun firebaseInit() {
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl(BASE_URL)
            .build()
        FirebaseApp.initializeApp(options)
    }
}