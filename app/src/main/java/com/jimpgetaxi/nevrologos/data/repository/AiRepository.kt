package com.jimpgetaxi.nevrologos.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.jimpgetaxi.nevrologos.BuildConfig
import com.jimpgetaxi.nevrologos.data.network.AiModel
import com.jimpgetaxi.nevrologos.data.network.AiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepository @Inject constructor(
    private val aiService: AiService
) {
    private var currentModelName = "gemini-2.0-flash"

    fun setModel(modelName: String) {
        currentModelName = modelName.replace("models/", "")
    }

    fun getCurrentModel(): String = currentModelName

    suspend fun fetchAvailableModels(): List<AiModel> {
        return try {
            android.util.Log.d("AiRepository", "Attempting API call to fetch models...")
            val response = aiService.getModels(BuildConfig.GEMINI_API_KEY)
            val models = response.models
            android.util.Log.d("AiRepository", "Successfully fetched ${models.size} models")
            
            val geminiModels = models.filter { it.name.contains("gemini", ignoreCase = true) }
            android.util.Log.d("AiRepository", "Found ${geminiModels.size} Gemini models")
            
            geminiModels.sortedByDescending { it.name }
        } catch (e: Exception) {
            android.util.Log.e("AiRepository", "Error during fetch: ${e.localizedMessage}")
            throw e // Rethrow to handle in ViewModel
        }
    }

    suspend fun getAiDiagnosis(symptoms: String): String {
        val generativeModel = GenerativeModel(
            modelName = currentModelName,
            apiKey = BuildConfig.GEMINI_API_KEY,
            systemInstruction = com.google.ai.client.generativeai.type.content {
                text("Είσαι ένας εξειδικευμένος Νευρολόγος με εμπειρία στην Σκλήρυνση Κατά Πλάκας (ΣΚΠ). " +
                        "Ο χρήστης θα σου περιγράψει συμπτώματα ή θα ανεβάσει εξετάσεις. " +
                        "Πρέπει να αναλύσεις τα δεδομένα και να προτείνεις μια πιθανή διάγνωση (π.χ. RRMS, SPMS, PPMS) ή να ζητήσεις περαιτέρω διευκρινίσεις. " +
                        "Η απάντησή σου πρέπει να είναι στα Ελληνικά, επιστημονικά τεκμηριωμένη, καθησυχαστική αλλά και ειλικρινής. " +
                        "Πάντα να τονίζεις ότι είσαι ένας AI βοηθός και η τελική διάγνωση πρέπει να γίνει από γιατρό.")
            }
        )

        return try {
            val response = generativeModel.generateContent(symptoms)
            response.text ?: "Δεν μπόρεσα να παράγω διάγνωση. Παρακαλώ προσπαθήστε ξανά."
        } catch (e: Exception) {
            "Σφάλμα κατά την επικοινωνία με το AI: ${e.message}"
        }
    }

    suspend fun analyzeMedicalFile(fileBytes: ByteArray, mimeType: String, prompt: String = "Ανάλυσε αυτό το ιατρικό έγγραφο/εικόνα και εξήγησε τα ευρήματα στα Ελληνικά."): String {
        val generativeModel = GenerativeModel(
            modelName = currentModelName,
            apiKey = BuildConfig.GEMINI_API_KEY,
            systemInstruction = com.google.ai.client.generativeai.type.content {
                text("Είσαι ένας εξειδικευμένος Νευρολόγος. Ανάλυσε τις ιατρικές εξετάσεις (εικόνες ή PDF) που σου στέλνει ο χρήστης. " +
                        "Εξήγησε τα αποτελέσματα με απλά αλλά επιστημονικά λόγια στα Ελληνικά. " +
                        "Αν πρόκειται για μαγνητική (MRI), εστίασε σε απομυελινωτικές εστίες. " +
                        "Αν πρόκειται για αιματολογικές, έλεγξε επίπεδα Βιταμίνης D, B12 και λευκά αιμοσφαίρια.")
            }
        )

        val inputContent = com.google.ai.client.generativeai.type.content {
            blob(mimeType, fileBytes)
            text(prompt)
        }

        return try {
            val response = generativeModel.generateContent(inputContent)
            response.text ?: "Δεν μπόρεσα να αναλύσω το αρχείο."
        } catch (e: Exception) {
            "Σφάλμα κατά την ανάλυση του αρχείου: ${e.message}"
        }
    }
}
