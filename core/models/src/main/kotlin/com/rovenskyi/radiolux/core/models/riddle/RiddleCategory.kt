package com.rovenskyi.radiolux.core.models.riddle

/**
 * Categories of riddles available in the app.
 * Each category has a corresponding JSON file in assets/riddles/.
 *
 * @property fileName The JSON file name in assets/riddles/ directory
 * @property displayEmoji Default emoji for this category
 */
enum class RiddleCategory(
    val fileName: String,
    val displayEmoji: String,
) {
    LOGIC("logic.json", "🧩"),
    SPACE("space.json", "🚀"),
    ANIMALS("animals.json", "🦁"),
    MINECRAFT("minecraft.json", "🎮"),
    COOKING("cooking.json", "🍳"),
    GEOGRAPHY("geography.json", "🌍"),
    UKRAINE_HISTORY("ukraine_history.json", "🇺🇦"),
    WRITERS("writers.json", "📚"),
    CARTOONS("cartoons.json", "🎬"),
    MUSIC("music.json", "🎵"),
    CURRENCY("currency.json", "💵"),
    ECOLOGY("ecology.json", "🌱"),
    SCIENCE("science.json", "🔬"),
    ENGLISH_VOCAB("english_vocab.json", "🇬🇧"),
    DINOSAURS("dinosaurs.json", "🦖"),
    SPORTS("sports.json", "⚽"),
    ENGLISH_GRAMMAR("english_grammar.json", "📝"),
    HARRY_POTTER("harry_potter.json", "⚡"),
}
