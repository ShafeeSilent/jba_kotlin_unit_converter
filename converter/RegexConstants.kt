package converter

/**
 * A regex that is used to check if a user entered valid data for conversion.
 *
 * 21.5 kg to grams, 5 meters to km and so on (even if the specified units doesn't exist)
 */
val FOR_CONVERSION = "-?\\d+(\\.\\d+)? [a-zA-Z]+ ([a-zA-Z]+ )?(in|to|[a-zA-Z]+) [a-zA-Z]+( [a-zA-Z]+)?+\\s*".toRegex()

/**
 * A regex that is used to check if a user entered the exit command.
 */
val EXIT = "\\s*(exit|EXIT)\\s*".toRegex()


