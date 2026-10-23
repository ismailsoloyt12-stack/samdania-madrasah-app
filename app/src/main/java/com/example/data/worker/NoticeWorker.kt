package com.example.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.R
import com.example.data.remote.SupabaseRepository
import java.util.concurrent.TimeUnit

class NoticeWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val repository = SupabaseRepository(applicationContext)
        val prefs = applicationContext.getSharedPreferences("notice_prefs", Context.MODE_PRIVATE)
        val lastId = prefs.getString("last_notice_id", "")

        return try {
            val notices = repository.fetchNotices()
            if (notices.isNotEmpty()) {
                val latest = notices.first() // Assuming list is sorted by most recent
                if (latest.id != lastId) {
                    showNotification(latest.title, latest.content)
                    prefs.edit().putString("last_notice_id", latest.id).apply()
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification(title: String, content: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "madrasah_notices"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "মাদ্রাসা নোটিশ",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Fallback icon
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        fun startPeriodicPolling(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<NoticeWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "notice_polling",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
