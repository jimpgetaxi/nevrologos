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
            android.util.Log.d("AiRepository", "Starting to fetch models with API Key...")
            val response = aiService.getModels(BuildConfig.GEMINI_API_KEY)
            android.util.Log.d("AiRepository", "Raw API Response: Fetched ${response.models.size} models")
            
            // Log all names to see what we are getting
            response.models.forEach { 
                android.util.Log.d("AiRepository", "Model found: ${it.name} | Display: ${it.displayName}")
            }

            // Minimal filter: just make sure it's a gemini model and not a known text-only legacy model if we want
            val filtered = response.models.filter { 
                it.name.contains("gemini", ignoreCase = true)
            }.sortedByDescending { it.name }
            
            android.util.Log.d("AiRepository", "Filtered to ${filtered.size} Gemini models")
            filtered
        } catch (e: Exception) {
            android.util.Log.e("AiRepository", "FATAL Error fetching models: ${e.message}", e)
            emptyList()
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
}
