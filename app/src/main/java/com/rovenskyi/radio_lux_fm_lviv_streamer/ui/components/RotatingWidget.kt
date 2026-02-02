package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val ROTATION_INTERVAL_MS = 20_000L
private val ANSWER_REVEAL_TIME: LocalTime = LocalTime.of(22, 30)
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/**
 * Widget that displays clock and rotating riddles.
 * Clock is always visible, riddles rotate every 20 seconds.
 */
@Composable
fun RotatingWidget(modifier: Modifier = Modifier) {
    var questionIndex by rememberSaveable { mutableIntStateOf((System.currentTimeMillis() % Int.MAX_VALUE).toInt()) }
    var currentTime by remember { mutableStateOf(LocalTime.now()) }

    // Update time every second and rotate riddles based on interval
    LaunchedEffect(Unit) {
        var lastRotationTime = System.currentTimeMillis()
        while (isActive) {
            delay(1000L)
            currentTime = LocalTime.now()

            val now = System.currentTimeMillis()
            if (now - lastRotationTime >= ROTATION_INTERVAL_MS) {
                questionIndex = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
                lastRotationTime = now
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Clock - always visible
        ClockContent(currentTime)

        Spacer(modifier = Modifier.height(24.dp))

        // Riddle - rotating with animation
        AnimatedContent(
            targetState = questionIndex,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "riddle_rotation",
        ) { index ->
            RiddleContent(
                isAfterRevealTime = currentTime >= ANSWER_REVEAL_TIME,
                questionIndex = index,
            )
        }
    }
}

@Composable
private fun ClockContent(currentTime: LocalTime) {
    Text(
        text = currentTime.format(timeFormatter),
        style = MaterialTheme.typography.displayLarge,
    )
}

// region Riddle / Fun Facts

private data class FunQuestion(
    val question: String,
    val answer: String,
    val emoji: String = "🧩",
)

private val funQuestions = listOf(
    // Логічні загадки (однозначні відповіді)
    FunQuestion("Чим більше з неї береш, тим більшою вона стає", "Яма"),
    FunQuestion("Що належить тобі, але інші користуються ним частіше?", "Твоє ім'я"),
    FunQuestion("Що має голову і хвіст, але не має тіла?", "Монета"),
    FunQuestion("Що стає мокрішим, чим більше висихає?", "Рушник"),
    FunQuestion("Що має багато ключів, але не може відкрити жодних дверей?", "Піаніно"),
    FunQuestion("Що має руки, але не може плескати?", "Годинник"),
    FunQuestion("Що росте вниз головою?", "Бурулька"),
    FunQuestion("Скільки місяців мають 28 днів?", "Усі 12"),
    FunQuestion("У батька Марії 5 дочок: Чача, Чече, Чичі, Чочо. Як звати п'яту?", "Марія"),
    FunQuestion("Електричка їде на схід. Куди йде дим?", "Електрички не димлять"),
    FunQuestion("Що більше слона, але нічого не важить?", "Тінь слона"),
    FunQuestion("Три коти за 3 хвилини ловлять 3 мишей. 100 котів за скільки зловлять 100?", "За 3 хвилини"),

    // 🚀 Космос
    FunQuestion("Яка планета найближча до Сонця?", "Меркурій", "🚀"),
    FunQuestion("Скільки планет у Сонячній системі?", "8", "🚀"),
    FunQuestion("Яка найбільша планета Сонячної системи?", "Юпітер", "🚀"),
    FunQuestion("Як називається супутник Землі?", "Місяць", "🌙"),
    FunQuestion("Яка планета відома своїми кільцями?", "Сатурн", "🪐"),
    FunQuestion("Скільки триває доба на Землі?", "24 години", "🌍"),
    FunQuestion("Яка найгарячіша планета?", "Венера (через парниковий ефект)", "🔥"),
    FunQuestion("Хто був першою людиною в космосі?", "Юрій Гагарін", "👨‍🚀"),
    FunQuestion("Як називається наша галактика?", "Чумацький Шлях", "🌌"),
    FunQuestion("Скільки триває рік на Землі?", "365 днів", "🌍"),

    // 🎮 Minecraft
    FunQuestion("З чого зроблений портал у Незер?", "Обсидіан", "🎮"),
    FunQuestion("Скільки блоків обсидіану потрібно для порталу в Незер?", "10 (мінімум)", "🎮"),
    FunQuestion("Як називається головний бос у Minecraft?", "Ендер Дракон", "🐉"),
    FunQuestion("Що дропає Крипер?", "Порох", "💥"),
    FunQuestion("Чим можна приручити вовка в Minecraft?", "Кістка", "🐺"),
    FunQuestion("Який найміцніший матеріал у Minecraft?", "Незерит", "⚒️"),
    FunQuestion("Що потрібно щоб зробити факел?", "Палка + вугілля", "🔥"),
    FunQuestion("Скільки блоків висоти має Стів?", "2 блоки", "🧱"),
    FunQuestion("Чим годувати свиней у Minecraft?", "Морквою", "🐷"),
    FunQuestion("Як називається вимір з Ендерменами?", "Енд", "🟣"),

    // 🍳 Кулінарія
    FunQuestion("З чого роблять шоколад?", "Какао-боби", "🍫"),
    FunQuestion("Яка країна — батьківщина піци?", "Італія", "🍕"),
    FunQuestion("Скільки кольорів у веселці?", "7", "🌈"),
    FunQuestion("З чого роблять сир?", "Молоко", "🧀"),
    FunQuestion("Яка ягода росте на болоті?", "Журавлина", "🫐"),
    FunQuestion("Що додають у тісто, щоб воно піднялось?", "Дріжджі", "🍞"),
    FunQuestion("З якої країни родом суші?", "Японія", "🍣"),
    FunQuestion("Скільки хвилин варити яйце некруто?", "3-4 хвилини", "🥚"),
    FunQuestion("Який овоч змушує плакати?", "Цибуля", "🧅"),
    FunQuestion("Яка найпопулярніша спеція у світі?", "Перець", "🌶️"),

    // 🦁 Тварини
    FunQuestion("Яка тварина найшвидша у світі?", "Гепард", "🐆"),
    FunQuestion("Скільки ніг у павука?", "8", "🕷️"),
    FunQuestion("Яка тварина найбільша на Землі?", "Синій кит", "🐋"),
    FunQuestion("Скільки років живе черепаха?", "100+ років", "🐢"),
    FunQuestion("Яка тварина ніколи не спить?", "Мураха", "🐜"),
    FunQuestion("Скільки сердець у восьминога?", "3", "🐙"),
    FunQuestion("Яка тварина може літати назад?", "Колібрі", "🐦"),
    FunQuestion("Який птах найбільший у світі?", "Страус", "🦅"),
    FunQuestion("Скільки років може жити слон?", "60-70 років", "🐘"),
    FunQuestion("Яка тварина спить стоячи?", "Кінь", "🐴"),

    // 🌍 Географія та Україна
    FunQuestion("Яка найдовша річка України?", "Дніпро", "🇺🇦"),
    FunQuestion("Яка столиця України?", "Київ", "🇺🇦"),
    FunQuestion("Скільки областей в Україні?", "24 (+1 АР Крим)", "🇺🇦"),
    FunQuestion("Який океан найбільший?", "Тихий", "🌊"),
    FunQuestion("Яка найвища гора у світі?", "Еверест", "🏔️"),
    FunQuestion("Скільки континентів на Землі?", "7", "🌍"),
    FunQuestion("Яка найменша країна у світі?", "Ватикан", "🏰"),
    FunQuestion("Де знаходиться Ейфелева вежа?", "Париж, Франція", "🗼"),

    // 🔬 Наука
    FunQuestion("Скільки кісток у тілі дорослої людини?", "206", "🦴"),
    FunQuestion("Яка формула води?", "H₂O", "💧"),
    FunQuestion("При якій температурі замерзає вода?", "0°C", "❄️"),
    FunQuestion("При якій температурі кипить вода?", "100°C", "♨️"),
    FunQuestion("Яка найтвердіша речовина у природі?", "Алмаз", "💎"),
    FunQuestion("Скільки зубів у дорослої людини?", "32", "🦷"),
    FunQuestion("Як називається наука про зірки?", "Астрономія", "⭐"),
    FunQuestion("Скільки кольорів бачить людське око?", "~10 мільйонів", "👁️"),
)

@Composable
private fun RiddleContent(isAfterRevealTime: Boolean, questionIndex: Int) {
    val question = remember(questionIndex) {
        funQuestions[questionIndex % funQuestions.size]
    }
    var showAnswer by rememberSaveable(questionIndex) { mutableStateOf(false) }
    val shouldShowAnswer = showAnswer || isAfterRevealTime

    Column(
        modifier = Modifier
            .clickable { showAnswer = !showAnswer }
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = question.emoji,
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = question.question,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (shouldShowAnswer) {
            Text(
                text = "💡 ${question.answer}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                text = "Натисни для відповіді",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// endregion
