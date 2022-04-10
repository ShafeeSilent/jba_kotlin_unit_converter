package converter

fun main() {
    while (true) {
        try {
            val (sourceValue, SOURCE_UNIT, TARGET_UNIT) = askToEnterSourceData()
            printConversionResult(sourceValue, SOURCE_UNIT, TARGET_UNIT)
        } catch (e: ExitException) {
            return
        }
    }
}

fun askToEnterSourceData(): Triple<Double, Units, Units> {
    while (true) {
        print("Enter what you want to convert (or exit): ")
        val answer = formatInput(readLine()!!)

        if (answer.matches(FOR_CONVERSION)) {
            val (sourceValue: Double, sourceUnitName, targetUnitName)
                    = extractTokens(answer)

            val SOURCE_UNIT = getUnitEnum(sourceUnitName)
            val TARGET_UNIT = getUnitEnum(targetUnitName)

            if (isConversionPossible(sourceValue, SOURCE_UNIT, TARGET_UNIT)) {
                return Triple(sourceValue, SOURCE_UNIT!!, TARGET_UNIT!!)
            } else {
                printReason(sourceValue, SOURCE_UNIT, TARGET_UNIT)
            }
        } else if (answer.matches(EXIT)) {
            throw  ExitException()
        } else {
            println("Parse error\n")
        }
    }
}

private fun extractTokens(string: String): Triple<Double, String, String> {
    val stringList = string.split("\\s+".toRegex())

    if (stringList.size !in 4..6) throw Exception("The string should consist of 4-6 tokens")

    val sourceValue = stringList.first().toDouble()
    val sourceUnitName = extractUnitToken(stringList, extractTargetUnit = false)
    val targetUnitName = extractUnitToken(stringList, extractTargetUnit = true)

    return Triple(sourceValue, sourceUnitName, targetUnitName)
}

private fun extractUnitToken(stringList: List<String>, extractTargetUnit: Boolean): String {

    val string: String?
    string = when {
        stringList.size == 6 -> {
            if (extractTargetUnit) "${stringList[4]} ${stringList[5]}"
            else "${stringList[1]} ${stringList[2]}"
        }
        stringList.size == 4 -> {
            if (extractTargetUnit) stringList[3]
            else stringList[1]
        }
        else -> null
    }

    if (string != null) return string

    // if stringList size 5
    return when (extractTargetUnit){
        true -> if (stringList[1].contains("degree")) stringList[4] else "${stringList[3]} ${stringList[4]}"
        false -> if (stringList[1].contains("degree")) "${stringList[1]} ${stringList[2]}" else stringList[1]
    }
}

/**
 * Deletes potential whitespaces at the beginning and all another extra ones
 * and makes the string lowercase by default.
 */
private fun formatInput(
    input: String,
    toLowerCase: Boolean = true
): String {
    val result = input
        .replaceIndent() // at the beginning
        .replace("\\s+".toRegex(), " ") // between words
        .dropLastWhile { it.isWhitespace() } // last whitespaces

    return if (toLowerCase) result.lowercase() else result
}
