package com.africogram.www.firebase

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Firebase FireStore :: contain every recurring task dealing with FireStore database
 */
class FireStoreBase(val context: Context) {

    /**
     * Access a Cloud Firestore instance
     * @return void
     */
    private fun getFirestoreInstance(): FirebaseFirestore {
        return Firebase.firestore
    }

    // interface that will check for generated doc data add process status
    interface AddDocDataWithGeneratedIdProcessCallback {
        fun onAddedDocDataProcessStatus(isSuccessful: Boolean, errorMessage: String?, docRefId: String?)
    }

    /**
     * Add doc data to collection with
     * generated doc id
     * @param collectionName :: collection name
     * @param docData :: doc data
     * Eg:
     * val docData = hashMapOf(
           "first" to "Ada",
           "last" to "Lovelace",
           "born" to 1815
        )
     */
    fun addDocDataToCollectionWithGeneratedId(collectionName: String,docData: Any,AddDocDataWithGeneratedIdProcessCallback: AddDocDataWithGeneratedIdProcessCallback) {
        // Add a new document with a generated ID
        getFirestoreInstance().collection(collectionName)
            .add(docData)
            .addOnSuccessListener { documentReference ->
                AddDocDataWithGeneratedIdProcessCallback.onAddedDocDataProcessStatus(
                    true,
                    null,
                    documentReference.id
                )
            }
            .addOnFailureListener { e ->
                // error adding document
                AddDocDataWithGeneratedIdProcessCallback.onAddedDocDataProcessStatus(false,e.localizedMessage,null)
            }
    }

    // interface that will check for generated doc data add process status
    interface AddDocDataWithSpecificIdProcessCallback {
        fun onAddedDocDataProcessStatus(isSuccessful: Boolean, errorMessage: String?)
    }

    /**
     * Add doc data to collection with specific doc ID
     * @param collectionName :: collection name
     * @param docSpecificId :: specific doc ID
     * @param docData :: doc data
     */
    fun addDocDataToCollectionWithSpecificId(collectionName: String, docSpecificId: String, docData: Any, AddDocDataWithSpecificIdProcessCallback: AddDocDataWithSpecificIdProcessCallback) {
        // add a new document with specific ID
        getFirestoreInstance().collection(collectionName).document(docSpecificId)
            .set(docData, SetOptions.merge())
            .addOnSuccessListener {
                AddDocDataWithSpecificIdProcessCallback.onAddedDocDataProcessStatus(true,null)
            }
            .addOnFailureListener { e ->
                AddDocDataWithSpecificIdProcessCallback.onAddedDocDataProcessStatus(false, e.localizedMessage)
            }
    }

    // interface that will check for updated doc field data process status
    interface UpdateDocFieldDataProcessCallback {
        fun onUpdatedDocFieldDataProcessStatus(isSuccessful: Boolean, errorMessage: String?)
    }

    /**
     * Update document data field
     * @param collectionName :: collection name
     * @param docSpecificId :: document ID
     * @param fieldDataKey :: field data key
     * @param fieldDataNewValue :: field new data value
     */
    fun updateDocDataField(collectionName: String, docSpecificId: String, fieldDataKey: String, fieldDataNewValue: Any,UpdateDocFieldDataProcessCallback: UpdateDocFieldDataProcessCallback) {
        getFirestoreInstance().collection(collectionName).document(docSpecificId)
            .update(fieldDataKey, fieldDataNewValue)
            .addOnSuccessListener {
                UpdateDocFieldDataProcessCallback.onUpdatedDocFieldDataProcessStatus(true, null)
            }
            .addOnFailureListener { e ->
                UpdateDocFieldDataProcessCallback.onUpdatedDocFieldDataProcessStatus(false, e.localizedMessage)
            }
    }
}