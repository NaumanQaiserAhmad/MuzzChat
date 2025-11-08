package com.muzz.chat.presentation

/**
 * Auto replies tailored to introduce ME (the candidate) and why I'm a fit for Muzz.
 * KISS: no networking; small keyword router + curated one-liners.
 */
object AutoReplies {

    data class Profile(
        val name: String? = null,               // e.g., "Nauman"
        val city: String? = null,               // e.g., "London"
        val yearsOfExperience: Int? = 5,
        val role: String = "Android Engineer",
        val stackHighlights: List<String> = listOf("Kotlin", "Jetpack Compose", "Coroutines/Flow", "Room", "Hilt"),
        val principles: List<String> = listOf("clean architecture", "testing first", "accessibility", "KISS"),
        val valuesLine: String = "intentional, faith-aligned, respectful"
    )

    /** Set this once at startup if you want personalization. */
    var defaultProfile = Profile(
        // name = "Nauman",
        // city = "London",
        yearsOfExperience = 11,
        role = "Android Engineer",
        stackHighlights = listOf("Kotlin", "Jetpack Compose", "Coroutines/Flow", "Room", "Hilt", "Paging"),
        principles = listOf("clean architecture", "explicit error handling", "observability & tests", "a11y"),
        valuesLine = "intentional, respectful, and marriage-focused"
    )

    // ---------------- Public API ----------------

    /** Context-aware reply (optional). */
    fun replyFor(userText: String?, profile: Profile = defaultProfile): String {
        val t = userText?.lowercase().orEmpty()

        return when {
            t.contains("intro") || t.contains("about") || t.contains("who are you") ->
                intro(profile)
            t.contains("architecture") || t.contains("clean") || t.contains("module") ->
                architecture(profile)
            t.contains("test") || t.contains("turbine") || t.contains("robolectric") || t.contains("ksp") ->
                testing(profile)
            t.contains("compose") || t.contains("ui") ->
                compose(profile)
            t.contains("scale") || t.contains("performance") || t.contains("paging") ->
                scale(profile)
            t.contains("offline") || t.contains("room") || t.contains("db") ->
                offline(profile)
            t.contains("a11y") || t.contains("access") || t.contains("semantic") ->
                accessibility(profile)
            t.contains("muzz") || t.contains("fit") || t.contains("why") ->
                whyMuzz(profile)
            t.contains("meet") || t.contains("plan") || t.contains("time") ->
                plan(profile)
            t.contains("salam") || t.contains("salaam") ->
                "Wa alaykum as-salaam! ${elevator(profile)}"
            else -> general(profile).random()
        }
    }

    /** Back-compat with existing usage: AutoReplies.options.random() */
    val options: List<String>
        get() = general(defaultProfile)

    // ---------------- Lines ----------------

    private fun intro(p: Profile): String {
        val yoe = p.yearsOfExperience?.let { "$it+ yrs " } ?: ""
        val who = listOfNotNull(p.name, p.city).joinToString(" • ").takeIf { it.isNotBlank() }
        val stack = if (p.stackHighlights.isNotEmpty()) p.stackHighlights.joinToString(", ") else "modern Android"
        return buildString {
            append("Hi! I’m a ${yoe}${p.role} focused on $stack.")
            if (who != null) append(" ($who)")
        }
    }

    private fun elevator(p: Profile): String =
        "Android engineer with strong Compose + clean architecture; I value ${p.valuesLine}."

    private fun architecture(p: Profile): String =
        "I used a modular clean architecture (domain/data/ui), explicit AppResult for errors, and a ViewModel UiState so the UI stays predictable."

    private fun testing(p: Profile): String =
        "I covered repo (in-memory Room + Robolectric), domain use cases (pure JVM), VM with Turbine + test Main, and a Compose UI test for message status."

    private fun compose(p: Profile): String =
        "The UI is Jetpack Compose: stateless components, semantics for a11y, and centralized tokens for colors/strings so it scales cleanly."

    private fun scale(p: Profile): String =
        "For scalability I added Paging-friendly data flow, centralized resources, and boundaries that make it easy to add threads or sockets later."

    private fun offline(p: Profile): String =
        "Messages persist with Room; flows keep the UI reactive. Clear DAO boundaries make migrations and indexing straightforward."

    private fun accessibility(p: Profile): String =
        "Status glyphs have content descriptions, date chips announce full text, and colors/strings are centralized for localization & a11y."

    private fun whyMuzz(p: Profile): String =
        "Why Muzz: I care about intentional, faith-aligned products and thoughtful UX. This exercise mirrors that: simple, robust, and respectful."

    private fun plan(p: Profile): String =
        "Happy to walk through the code decisions—architecture, tests, and trade-offs—and extend it if you want to see real-time delivery or paging."

    private fun general(p: Profile): List<String> = listOf(
        intro(p),
        elevator(p),
        architecture(p),
        testing(p),
        compose(p),
        scale(p),
        offline(p),
        accessibility(p),
        whyMuzz(p),
        plan(p)
    )
}
