package com.sandycodes.doneright.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.sandycodes.doneright.data.local.Entity.TaskEntity
import com.sandycodes.doneright.data.local.Entity.TaskStatus

object FirestoreService {

    private val db = FirebaseFirestore.getInstance()

    fun tasksCollection(): CollectionReference? {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return null
        return FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("tasks")
    }

    fun upsertTask(task: TaskEntity) {
        val firestoreTask = FirestoreTask(
            id = task.id,
            title = task.title,
            description = task.description,
            status = task.status.name,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt
        )

        tasksCollection()
            ?.document(task.id)
            ?.set(firestoreTask)
    }

    fun listenTasks(
        onTasksChanged: (List<FirestoreTask>) -> Unit
    ) {
        val collection = tasksCollection() ?: return

        collection.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener

            val tasks = snapshot.documents.mapNotNull {
                it.toObject(FirestoreTask::class.java)
            }

            onTasksChanged(tasks)
        }
    }

    fun deleteTask(task: TaskEntity) {
        tasksCollection()
            ?.document(task.id)
            ?.delete()
    }
}
