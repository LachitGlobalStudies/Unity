package com.example.firebase

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.storage.FirebaseStorage

/**
 * Central Firebase configuration and service provider for Lachit Social.
 * Manages initialization, service singletons, and offline persistence settings.
 */
object FirebaseConfig {
    private const val TAG = "FirebaseConfig"

    @Volatile
    private var isInitialized = false

    private var _auth: FirebaseAuth? = null
    private var _firestore: FirebaseFirestore? = null
    private var _storage: FirebaseStorage? = null

    val auth: FirebaseAuth?
        get() = _auth

    val firestore: FirebaseFirestore?
        get() = _firestore

    val storage: FirebaseStorage?
        get() = _storage

    /**
     * Checks if Firebase credentials are provided and valid.
     */
    val isConfigured: Boolean
        get() {
            if (_auth != null) return true
            val key = BuildConfig.FIREBASE_API_KEY
            val projectId = BuildConfig.FIREBASE_PROJECT_ID
            return key.isNotBlank() &&
                    !key.contains("Placeholder") &&
                    !key.contains("demo_lachit") &&
                    projectId.isNotBlank() &&
                    isInitialized
        }

    /**
     * Initialize Firebase with google-services.json or options loaded from BuildConfig/Secrets.
     * Safe to call multiple times from Application or MainActivity.
     */
    fun initialize(context: Context) {
        if (isInitialized) return

        synchronized(this) {
            if (isInitialized) return

            try {
                val existingApps = FirebaseApp.getApps(context)
                val app = if (existingApps.isNotEmpty()) {
                    FirebaseApp.getInstance()
                } else {
                    // 1. First attempt default initialization via google-services.json
                    val autoApp = try {
                        FirebaseApp.initializeApp(context.applicationContext)
                    } catch (e: Exception) {
                        Log.d(TAG, "Default google-services.json initialization skipped: ${e.message}")
                        null
                    }

                    // 2. If not found or failed, attempt programmatic initialization via BuildConfig secrets
                    autoApp ?: run {
                        val apiKey = BuildConfig.FIREBASE_API_KEY
                        val projectId = BuildConfig.FIREBASE_PROJECT_ID
                        val appId = BuildConfig.FIREBASE_APP_ID
                        val storageBucket = BuildConfig.FIREBASE_STORAGE_BUCKET
                        val gcmSenderId = BuildConfig.FIREBASE_MESSAGING_SENDER_ID

                        if (apiKey.isNotBlank() && projectId.isNotBlank() && appId.isNotBlank() &&
                            !apiKey.contains("Placeholder") && !apiKey.contains("demo_lachit")
                        ) {
                            val options = FirebaseOptions.Builder()
                                .setApiKey(apiKey)
                                .setProjectId(projectId)
                                .setApplicationId(appId)
                                .apply {
                                    if (storageBucket.isNotBlank()) setStorageBucket(storageBucket)
                                    if (gcmSenderId.isNotBlank()) setGcmSenderId(gcmSenderId)
                                }
                                .build()
                            FirebaseApp.initializeApp(context.applicationContext, options)
                        } else {
                            Log.w(TAG, "Firebase credentials missing or unconfigured. Running with local fallback.")
                            null
                        }
                    }
                }

                if (app != null) {
                    _auth = FirebaseAuth.getInstance(app)
                    _firestore = FirebaseFirestore.getInstance(app).apply {
                        try {
                            firestoreSettings = FirebaseFirestoreSettings.Builder()
                                .setLocalCacheSettings(
                                    PersistentCacheSettings.newBuilder()
                                        .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                                        .build()
                                )
                                .build()
                        } catch (e: Exception) {
                            Log.w(TAG, "Firestore cache settings already configured: ${e.message}")
                        }
                    }
                    try {
                        _storage = FirebaseStorage.getInstance(app)
                    } catch (e: Exception) {
                        Log.w(TAG, "Firebase Storage not initialized: ${e.message}")
                    }
                    Log.i(TAG, "Firebase initialized successfully for project: ${app.options.projectId}")
                }
                isInitialized = true
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing Firebase: ${e.message}", e)
                isInitialized = true // Avoid crashing loop; falls back to local database
            }
        }
    }
}
