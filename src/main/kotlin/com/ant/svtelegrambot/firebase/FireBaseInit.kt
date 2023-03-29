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

   private val serviceAccount = FileInputStream(TOKEN_FILE)

    fun firebaseInit() {
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl(BASE_URL)
            .build()
        FirebaseApp.initializeApp(options)
    }
}
