package com.example.groww_mf_assignment.presentation.Explore

data class ParsedFundName(
    val mainName: String,
    val planInfo: String?,
    val optionType: String?
)


private val OPTION_KEYWORDS = listOf(
    "growth", "idcw", "dividend", "income", "bonus",
    "direct", "regular", "option", "plan", "idcw option",
    "growth option", "dividend option", "income option"
)


private val AMC_PREFIXES = listOf(
    "UTI -", "ABN AMRO", "ING L.I.O.N."
)

fun parseMutualFundName(rawName: String): ParsedFundName {

    val normalized = rawName.replace(Regex("\\s{2,}"), " ").trim()


    val protected = AMC_PREFIXES.fold(normalized) { acc, prefix ->
        acc.replace(prefix, prefix.replace("-", "§"))
    }


    val spacedParts = protected.split(Regex("\\s+-\\s+")).map {
        it.replace("§", "-").trim()
    }


    val parts = if (spacedParts.size == 1) {
        protected.split(Regex("-")).map { it.replace("§", "-").trim() }
    } else {
        spacedParts
    }

    return when {
        parts.isEmpty() -> ParsedFundName(normalized, null, null)

        parts.size == 1 -> ParsedFundName(normalized, null, null)

        parts.size == 2 -> {
            val last = parts.last()
            if (OPTION_KEYWORDS.any { last.lowercase().contains(it) }) {
                ParsedFundName(
                    mainName = parts[0],
                    planInfo = null,
                    optionType = last
                )
            } else {
                ParsedFundName(normalized, null, null)
            }
        }

        else -> {
            val last = parts.last()
            val isLastAnOption = OPTION_KEYWORDS.any { last.lowercase().contains(it) }

            if (isLastAnOption) {
                ParsedFundName(
                    mainName = parts[0],
                    planInfo = parts.subList(1, parts.size - 1).joinToString(" - "),
                    optionType = last
                )
            } else {

                ParsedFundName(normalized, null, null)
            }
        }
    }
}