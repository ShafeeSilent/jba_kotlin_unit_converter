package converter

/**
 * Enums representing units of measure.
 *
 * @param type is used for checking conversion possibility
 * @param interUnitValue determines what one unit of
 * a certain unit of measure is equal to in its intermediate unit (meters for distance units and grams for mass ones)
 * @param irregularPlural represents a plural form of a unit name if it has an irregular one
 */
enum class Units(
    val type: UnitType,
    val interUnitValue: Double = 0.0, // millimeters for distance and grams for mass
    val possibleNamesOrAbbreviations: List<String>,
    val hasIrregularPlural: Boolean = false,
    val irregularPlural: String = "",
) {
    // TEMPERATURE UNITS.
    CELSIUS(
        UnitType.TEMPERATURE,
        possibleNamesOrAbbreviations = listOf("degree Celsius", "degrees Celsius", "dc", "c")
    ),
    FAHRENHEIT(
        UnitType.TEMPERATURE,
        possibleNamesOrAbbreviations = listOf("degree Fahrenheit", "degrees Fahrenheit", "df", "f")
    ),
    KELVIN(
        UnitType.TEMPERATURE,
        possibleNamesOrAbbreviations = listOf("kelvin", "kelvins", "k")
    ),

    // DISTANCE UNITS. The intermediate unit is meter
    METER(UnitType.DISTANCE, 1.0, listOf("m")),
    KILOMETER(UnitType.DISTANCE, 1000.0, listOf("km")),
    CENTIMETER(UnitType.DISTANCE, 0.01, listOf("cm")),
    MILLIMETER(UnitType.DISTANCE, 0.001, listOf("mm")),
    MILE(UnitType.DISTANCE, 1609.35, listOf("mi")),
    YARD(UnitType.DISTANCE, 0.9144, listOf("yd")),
    FOOT(UnitType.DISTANCE, 0.3048, listOf("ft"), hasIrregularPlural = true, irregularPlural = "feet"),

    INCH(UnitType.DISTANCE, 0.0254, listOf("in"), hasIrregularPlural = true, irregularPlural = "inches"),
    // MASS UNITS. The intermediate unit is gram
    GRAM(UnitType.MASS, 1.0, listOf("g")),
    KILOGRAM(UnitType.MASS, 1000.0, listOf("kg")),
    MILLIGRAM(UnitType.MASS, 0.001, listOf("mg")),
    POUND(UnitType.MASS, 453.592, listOf("lb")),

    OUNCE(UnitType.MASS, 28.3495, listOf("oz")),
}

enum class UnitType {
    MASS, DISTANCE, TEMPERATURE
}

/**
 * Makes conversion and prints the result in the specified way.
 */
fun printConversionResult(sourceValue: Double, SOURCE_UNIT: Units, TARGET_UNIT: Units) {
    val convertedResult = convert(sourceValue, SOURCE_UNIT, TARGET_UNIT)
    val sourceUnitName = getUnitName(SOURCE_UNIT, sourceValue)
    val targetUnitName = getUnitName(TARGET_UNIT, convertedResult)
    println("$sourceValue $sourceUnitName is $convertedResult $targetUnitName\n")
}

fun convert(sourceValue: Double, SOURCE_UNIT: Units, TARGET_UNIT: Units): Double {
    if (SOURCE_UNIT.type != TARGET_UNIT.type) throw Exception(
        "Convert from $SOURCE_UNIT to $TARGET_UNIT is impossible"
    )

    var interResult = 0.0
    return when (SOURCE_UNIT.type) {
        UnitType.DISTANCE -> {
            interResult = convertToOrFromMeters(fromMeters = false, sourceValue, SOURCE_UNIT)
            convertToOrFromMeters(fromMeters = true, interResult, TARGET_UNIT)
        }
        UnitType.MASS -> {
            interResult = convertToOrFromGrams(fromGrams = false, sourceValue, SOURCE_UNIT)
            convertToOrFromGrams(fromGrams = true, interResult, TARGET_UNIT)
        }
        UnitType.TEMPERATURE -> {
            interResult = convertToCelsius(sourceValue, SOURCE_UNIT)
            convertFromCelsius(interResult, TARGET_UNIT)
        }
    }
}

/**
 * Prints that conversion with specified units is impossible (if it is) else throws an exception
 */
fun printReason(sourceValue: Double, SOURCE_UNIT: Units?, TARGET_UNIT: Units?) {

    val isNotNull = SOURCE_UNIT != null && TARGET_UNIT != null
    if (isNotNull) {
        val isTheSameType = SOURCE_UNIT?.type == TARGET_UNIT?.type
        val isLengthOrDistanceUnit = SOURCE_UNIT?.type == UnitType.DISTANCE || SOURCE_UNIT?.type == UnitType.MASS
        if (isTheSameType && isLengthOrDistanceUnit && sourceValue < 0.0) {
            val nameOfUnitType = if (SOURCE_UNIT?.type == UnitType.DISTANCE) "Length" else "Weight"
            println("$nameOfUnitType shouldn't be negative")
            return
        } else if (isTheSameType) {
            throw Exception("Conversion is possible")
        }
    }

    val sourceUnit = getUnitName(SOURCE_UNIT, justEnumName = if (SOURCE_UNIT?.type == UnitType.TEMPERATURE) true else false)
    val targetUnit = getUnitName(TARGET_UNIT, justEnumName = if (TARGET_UNIT?.type == UnitType.TEMPERATURE) true else false)

    println("Conversion from $sourceUnit to $targetUnit is impossible\n")
}

/**
 * Returns a plural or single form of the unit name according to the value.
 *
 * If the value 1.0 the single form will be returned and the plural one in another cases.
 * Also returns "???" if its null
 */
fun getUnitName(UNIT: Units?, sourceValue: Double = 0.0, justEnumName: Boolean = false): String {

    return if (UNIT == null) "???"
    else if (justEnumName) when (UNIT == Units.KELVIN) {
        true -> UNIT.name.lowercase() + "s"
        else -> "degrees " + UNIT.name[0] + UNIT.name.substring(1, UNIT.name.lastIndex + 1).lowercase()
    }
    // degree <temperature unit>
    else if (UNIT.type == UnitType.TEMPERATURE && sourceValue == 1.0) UNIT.possibleNamesOrAbbreviations[0]
    // just single form of the unit name
    else if (sourceValue == 1.0) UNIT.name.lowercase()
    // degrees <temperature unit>
    else if (UNIT.type == UnitType.TEMPERATURE) UNIT.possibleNamesOrAbbreviations[1]
    else if (UNIT.hasIrregularPlural) UNIT.irregularPlural
    else UNIT.name.lowercase() + "s"
}

/**
 * Returns true if the conversion possible
 */
fun isConversionPossible(sourceValue: Double, SOURCE_UNIT: Units?, TARGET_UNIT: Units?): Boolean {

    val isTypeTheSame = SOURCE_UNIT != null && TARGET_UNIT != null && SOURCE_UNIT.type == TARGET_UNIT.type
    val isLengthOrWeighUnits = isTypeTheSame && (SOURCE_UNIT?.type == UnitType.DISTANCE || SOURCE_UNIT?.type == UnitType.MASS);

    return if (isTypeTheSame && isLengthOrWeighUnits) {
        if (sourceValue > 0.0) true else false
    } else isTypeTheSame
}

/**
 * Returns the unit enum based on its possible name else null
 */
fun getUnitEnum(enteredName: String): Units? {
    val singleFormOrAbbreviation = if (enteredName.length > 3 && enteredName.endsWith("s", true))
        enteredName.dropLast(1) else enteredName

    for (UNIT in Units.values()) {
        UNIT.possibleNamesOrAbbreviations.forEach {
            if (it.equals(enteredName, true)) return UNIT
        }

        if (UNIT.name.equals(singleFormOrAbbreviation, true) || UNIT.name.equals(enteredName, true)) return UNIT

        if (UNIT.hasIrregularPlural && UNIT.irregularPlural.equals(enteredName, true)) return UNIT
    }

    return null
}

/**
 * Makes conversion from/to meters.
 * @param UNIT in both cases should be a unit we convert to meters or meters to it
 */
fun convertToOrFromMeters(fromMeters: Boolean, sourceValue: Double, UNIT: Units): Double {
    if (UNIT.type != UnitType.DISTANCE) throw Exception("Conversion to/from meters is impossible")

    return if (fromMeters) {
        sourceValue / UNIT.interUnitValue
    } else {
        sourceValue * UNIT.interUnitValue
    }
}

/**
 * Makes conversion from/to grams.
 * @param UNIT in both cases should be a unit we convert to grams or grams to it
 */
fun convertToOrFromGrams(fromGrams: Boolean, sourceValue: Double, UNIT: Units): Double {
    if (UNIT.type != UnitType.MASS) throw Exception("Conversion to/from grams is impossible")

    return if (fromGrams) {
        sourceValue / UNIT.interUnitValue
    } else {
        sourceValue * UNIT.interUnitValue
    }
}

fun convertToCelsius(sourceValue: Double, SOURCE_UNIT: Units): Double {
    if (SOURCE_UNIT.type != UnitType.TEMPERATURE) throw Exception("Conversion to Celsius is impossible")

    return when (SOURCE_UNIT) {
        Units.KELVIN -> {
            sourceValue - 273.15
        }
        Units.FAHRENHEIT -> {
            (sourceValue - 32) * (5.0/9.0)
        }
        else -> {
            sourceValue
        }
    }
}

fun convertFromCelsius(sourceValue: Double, TARGET_UNIT: Units): Double {
    if (TARGET_UNIT.type != UnitType.TEMPERATURE) throw Exception("Conversion from Celsius is impossible")

    return when (TARGET_UNIT) {
        Units.KELVIN -> {
            sourceValue + 273.15
        }
        Units.FAHRENHEIT -> {
            (sourceValue * (9.0/5.0)) + 32
        }
        else -> {
            sourceValue
        }
    }
}