package com.example.english

/** A StoryWeaver (Pratham Books) story, CC BY 4.0: text kept as published, one sentence group per page. */
data class StoryBook(val level: Int, val title: String, val author: String, val url: String, val pages: List<Sentence>) {
    val credit get() = "\"$title\" by $author, Pratham Books StoryWeaver, CC BY 4.0 — $url"
}

object Stories {
    val books = listOf(
        StoryBook(4, "Fat King Thin Dog", "Parismita", "https://storyweaver.org.in/stories/7-fat-king-thin-dog", listOf(
            Sentence("This is a fat king.", "🤴"),
            Sentence("The fat king has a thin dog. The fat king and his thin dog go for a walk.", "🐕"),
            Sentence("The dog sees a bird. The dog runs after the bird.", "🐦"),
            Sentence("The king runs after the dog.", "🏃"),
            Sentence("They run and run. They run and run for many days.", "🏃‍♂️"),
            Sentence("The king catches the dog.", "🤲"),
            Sentence("Now the fat king is thin.", "💪"),
        )),
        StoryBook(4, "I lost my bat! I found my bat!", "Meera Tendolkar, translated by Niranjana C", "https://storyweaver.org.in/stories/1897-i-lost-my-bat-i-found-my-bat", listOf(
            Sentence("I can't find my bat, it's lost!", "🏏"),
            Sentence("It's not behind the door.", "🚪"),
            Sentence("It's not on the shelf.", "📚"),
            Sentence("It's not under the bed either.", "🛏️"),
            Sentence("It's not in the box.", "📦"),
            Sentence("It's not in the loft.", "🪜"),
            Sentence("I can't find it anywhere!", "🤷"),
        )),
        StoryBook(4, "Can and Can't", "Radha HS", "https://storyweaver.org.in/stories/137-can-and-can-t", listOf(
            Sentence("I can't knit, cats can't write, fish can't blink...", "🧶"),
            Sentence("... pigs can't fly!", "🐷"),
            Sentence("Dogs can't dance, ants can't read, crows can't sing...", "💃"),
            Sentence("... monkeys can't cook!", "🐒"),
            Sentence("But, monkeys can swing!", "🐵"),
            Sentence("Crows can caw... ants can bite... dogs can bark!", "🐶"),
            Sentence("Pigs can eat, fish can swim, cats can jump... and I can read!", "📖"),
        )),
        StoryBook(7, "The Red Raincoat", "Kiran Kasturia", "https://storyweaver.org.in/stories/369-the-red-raincoat", listOf(
            Sentence("On Sunday, Manu's parents got him a red raincoat. \"Ma, may I wear it now?\" asked Manu. \"No, my dear, the rains are near, but just now the sky is clear,\" said Ma.", "🧥"),
            Sentence("Monday was bright and sunny. \"WILL it rain today, Mummy?\" asked Manu. \"No Manu, not today. If you wear your raincoat, you will look quite funny!\" said Ma.", "☀️"),
            Sentence("On Tuesday, the sky was blue. \"Ma, WHEN will my wish come true?\" asked Manu. \"Not today, my dear, there is just one white cloud in the sky!\" said Ma.", "🌤️"),
            Sentence("Wednesday was hot. \"Ma, WHY doesn't it rain?\" asked Manu. \"Son, I think it will rain very soon. Maybe even before it is noon,\" said Ma.", "🥵"),
            Sentence("On Thursday Manu went on a picnic. \"Ma, WHAT if it rains? Shall I take the raincoat with me?\" asked Manu. \"No my dear, it will not rain today. The little white clouds are too high in the sky,\" said Ma.", "🧺"),
            Sentence("Friday was cloudy. \"Ma, will it rain today?\" asked Manu loudly. \"It might, my dear. There are some dark clouds low down in the sky,\" said Ma.", "☁️"),
            Sentence("Saturday began with a bang! Badaboom! \"Ma, is that thunder I hear? Will it rain very soon?\" asked Manu.", "⛈️"),
            Sentence("And then at last, it started raining! \"Oh, it's raining, it's raining,\" sang Manu, running out.", "🌧️"),
            Sentence("\"But Manu,\" called Ma, running after him, \"you forgot your raincoat!\"", "😄"),
        )),
        StoryBook(8, "Smile Please!", "Sanjiv Jaiswal 'Sanjay', translated by Manisha Chaudhry", "https://github.com/global-asp/pb-source/blob/master/en/0001_smile-please.md", listOf(
            Sentence("A fawn was racing in the forest.", "🦌"),
            Sentence("He was ahead of the rabbit.", "🐇"),
            Sentence("He was ahead of the elephant.", "🐘"),
            Sentence("He leapt and cleared the stream.", "🏞️"),
            Sentence("He ran past the crumbling wall.", "🧱"),
            Sentence("There was a large boulder on the grassy plain. He stumbled and fell down.", "🪨"),
            Sentence("He burst into tears.", "😭"),
            Sentence("The monkey massaged his leg. Tears flowed from the fawn's eyes.", "🐒"),
            Sentence("Brother Bear picked him up. The fawn didn't stop crying.", "🐻"),
            Sentence("His mother came. She said, \"Look, we'll beat up this bad boulder!\"", "🤱"),
            Sentence("The fawn said, \"Oh, don't do that or he will also start crying.\" His mother laughed. So did the fawn.", "😄"),
        )),
    )
    fun forLevel(level: Int) = books.filter { it.level == level }
}
