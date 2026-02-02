package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableLongStateOf
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
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private const val WIDGET_COUNT = 3
private const val ROTATION_INTERVAL_MS = 20_000L // 20 seconds
private val ANSWER_REVEAL_TIME: LocalTime = LocalTime.of(22, 30)
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun RotatingWidget(modifier: Modifier = Modifier) {
    var currentWidgetIndex by rememberSaveable { mutableIntStateOf(0) }
    var questionIndex by rememberSaveable { mutableIntStateOf((System.currentTimeMillis() % Int.MAX_VALUE).toInt()) }
    var currentDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    var lastRotationTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Update time every second and rotate widgets based on interval
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(1000L)
            currentDateTime = LocalDateTime.now()

            val now = System.currentTimeMillis()
            if (now - lastRotationTime >= ROTATION_INTERVAL_MS) {
                currentWidgetIndex = (currentWidgetIndex + 1) % WIDGET_COUNT
                // New random question each time we rotate to riddle widget
                if (currentWidgetIndex == 2) {
                    questionIndex = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
                }
                lastRotationTime = now
            }
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AnimatedContent(
            targetState = currentWidgetIndex,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "widget_rotation",
        ) { index ->
            when (index) {
                0 -> ClockContent(currentDateTime.toLocalTime())
                1 -> BirthdayContent(currentDateTime)
                2 -> RiddleContent(
                    isAfterRevealTime = currentDateTime.toLocalTime() >= ANSWER_REVEAL_TIME,
                    questionIndex = questionIndex,
                )
            }
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

// region Birthday

private data class FamilyMember(
    val name: String,
    val nameGenitive: String, // родовий відмінок для "день народження кого?"
    val birthMonth: Month,
    val birthDay: Int,
    val birthYear: Int,
)

private val familyMembers = listOf(
    FamilyMember("Соня", "Соні", Month.FEBRUARY, 20, 2023),
    FamilyMember("Макс", "Макса", Month.MAY, 11, 2016),
    FamilyMember("мама Юлічка", "мами Юлічки", Month.MAY, 14, 1992),
    FamilyMember("тато Петро", "тата Петра", Month.MAY, 18, 1991),
    FamilyMember("бабуся Аня", "бабусі Ані", Month.FEBRUARY, 9, 1962),
    FamilyMember("дідо Богдан", "діда Богдана", Month.JULY, 2, 1954),
    FamilyMember("бабуся Уля", "бабусі Улі", Month.JUNE, 11, 1972),
)

@Composable
private fun BirthdayContent(currentDateTime: LocalDateTime) {
    val birthdayInfo = remember(currentDateTime.minute) {
        calculateBirthdayInfo(currentDateTime)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = birthdayInfo.emoji,
            style = MaterialTheme.typography.displayMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = birthdayInfo.title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = birthdayInfo.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

private data class BirthdayInfo(
    val emoji: String,
    val title: String,
    val subtitle: String,
)

private fun calculateBirthdayInfo(now: LocalDateTime): BirthdayInfo {
    val today = now.toLocalDate()

    // Check if today is someone's birthday
    val birthdayToday = familyMembers.find { member ->
        member.birthMonth == today.month && member.birthDay == today.dayOfMonth
    }

    if (birthdayToday != null) {
        val age = today.year - birthdayToday.birthYear
        return BirthdayInfo(
            emoji = "🎉",
            title = "Сьогодні день народження!",
            subtitle = "${birthdayToday.name} — ${formatAge(age)}!",
        )
    }

    // Find next birthday with precise time calculation
    val upcomingBirthdays = familyMembers.map { member ->
        var birthdayDateTime = LocalDateTime.of(
            today.year,
            member.birthMonth,
            member.birthDay,
            0,
            0,
        )
        if (birthdayDateTime.isBefore(now) || birthdayDateTime.toLocalDate().isEqual(today)) {
            birthdayDateTime = birthdayDateTime.plusYears(1)
        }

        val minutesUntil = ChronoUnit.MINUTES.between(now, birthdayDateTime)
        val age = birthdayDateTime.year - member.birthYear
        Triple(member, minutesUntil, age)
    }.sortedBy { it.second }

    val (member, minutesUntil, age) = upcomingBirthdays.first()

    val timeText = formatTimeUntil(minutesUntil)

    return BirthdayInfo(
        emoji = "🎂",
        title = "$timeText день народження ${member.nameGenitive}!",
        subtitle = "Виповнюється ${formatAge(age)}",
    )
}

private fun formatTimeUntil(totalMinutes: Long): String {
    val days = totalMinutes / (24 * 60)
    val hours = (totalMinutes % (24 * 60)) / 60
    val minutes = totalMinutes % 60

    return when {
        days > 1 -> "Через ${formatDays(days.toInt())}"
        days == 1L -> {
            if (hours > 0) {
                "Через 1 день і ${formatHours(hours.toInt())}"
            } else {
                "Завтра"
            }
        }
        hours > 0 -> "Через ${formatHours(hours.toInt())} і ${formatMinutes(minutes.toInt())}"
        minutes > 0 -> "Через ${formatMinutes(minutes.toInt())}"
        else -> "Зараз"
    }
}

private fun formatDays(days: Int): String {
    val lastDigit = days % 10
    val lastTwoDigits = days % 100
    return when {
        lastTwoDigits in 11..14 -> "$days днів"
        lastDigit == 1 -> "$days день"
        lastDigit in 2..4 -> "$days дні"
        else -> "$days днів"
    }
}

private fun formatHours(hours: Int): String {
    val lastDigit = hours % 10
    val lastTwoDigits = hours % 100
    return when {
        lastTwoDigits in 11..14 -> "$hours годин"
        lastDigit == 1 -> "$hours годину"
        lastDigit in 2..4 -> "$hours години"
        else -> "$hours годин"
    }
}

private fun formatMinutes(minutes: Int): String {
    val lastDigit = minutes % 10
    val lastTwoDigits = minutes % 100
    return when {
        lastTwoDigits in 11..14 -> "$minutes хвилин"
        lastDigit == 1 -> "$minutes хвилину"
        lastDigit in 2..4 -> "$minutes хвилини"
        else -> "$minutes хвилин"
    }
}

private fun formatAge(age: Int): String {
    val lastDigit = age % 10
    val lastTwoDigits = age % 100
    return when {
        lastTwoDigits in 11..14 -> "$age років"
        lastDigit == 1 -> "$age рік"
        lastDigit in 2..4 -> "$age роки"
        else -> "$age років"
    }
}

// endregion

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
            style = MaterialTheme.typography.displayMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = question.question,
            style = MaterialTheme.typography.titleMedium,
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
