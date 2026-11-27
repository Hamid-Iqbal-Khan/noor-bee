package com.example.english

/*
 * Words, phrases and grammar for the speaking levels (5–8). They prepare a child for the VOA
 * conversations in the same level. Every phrase and grammar example is a line that is really
 * spoken in one of the conversations, so it plays in a real voice (see EnglishAudio).
 */
private fun v(word: String, picture: String) = Vocab(word, picture)
private fun rw(word: String, emoji: String) = ReadWord(word, listOf(word), emoji)

object SpeakingContent {
    data class Level(
        val words: List<Pair<String, List<Vocab>>>,          // lesson title -> picture words
        val pairs: List<Triple<String, String, List<Pair<ReadWord, ReadWord>>>> = emptyList(), // title, join, pairs
        val phrases: Pair<String, List<Phrase>>,
        val tips: List<Tip>,
    )

    val levels: Map<Int, Level> = mapOf(
        5 to Level(
            words = listOf(
                "Numbers 1–20" to listOf(v("one", "1"), v("two", "2"), v("three", "3"), v("four", "4"), v("five", "5"),
                    v("six", "6"), v("seven", "7"), v("eight", "8"), v("nine", "9"), v("ten", "10"), v("eleven", "11"),
                    v("twelve", "12"), v("thirteen", "13"), v("fourteen", "14"), v("fifteen", "15"), v("sixteen", "16"),
                    v("seventeen", "17"), v("eighteen", "18"), v("nineteen", "19"), v("twenty", "20")),
                "Days of the week" to listOf(v("Monday", "Mon"), v("Tuesday", "Tue"), v("Wednesday", "Wed"),
                    v("Thursday", "Thu"), v("Friday", "Fri"), v("Saturday", "Sat"), v("Sunday", "Sun")),
                "Things around me" to listOf(v("pen", "🖊️"), v("book", "📖"), v("map", "🗺️"), v("lamp", "🪔"), v("phone", "📱"),
                    v("key", "🔑"), v("bag", "👜"), v("toy", "🧸"), v("pillow", "🛌"), v("coffee", "☕")),
            ),
            phrases = "Hello and goodbye" to listOf(
                Phrase("Nice to meet you.", "Say it when you meet someone for the first time.", "🤝"),
                Phrase("Where are you from?", "Ask which town or country someone comes from.", "🌍"),
                Phrase("I am from a small town.", "Say where you come from.", "🏘️"),
                Phrase("How are you two?", "Ask two friends how they are.", "🙂"),
                Phrase("I am great!", "A happy answer to \"How are you?\"", "😄"),
                Phrase("Thank you.", "Say it when someone helps you or gives you something.", "🙏"),
                Phrase("I am sorry. You have the wrong number.", "On the phone, when the caller wants someone else.", "☎️"),
                Phrase("Great! Goodbye!", "A cheerful way to end a talk.", "👋"),
            ),
            tips = listOf(
                Tip("I am · you are · she is", "Use am with I, are with you, we and they, and is with he, she and it.",
                    "I am … / You are … / She is …",
                    listOf("I am from a small town.", "She is Anna. She is new to D.C.", "Good. You are there.", "It is not a pen. It is a book. It is a big book.")),
                Tip("This is … / Is this …?", "Say this is to show or name something. Turn it round to ask: is this …?",
                    "This is Marsha. / Is this Marsha?",
                    listOf("Hello. This is Marsha.", "Hello. Is this Marsha? It is Anna.", "No. This is 555-8689.")),
                Tip("I have … / Do you have …?", "Use have for things you own. Ask with do you have …?",
                    "I have a pen. / Do you have a pen?",
                    listOf("Anna, do you have a pen?", "Yes. I have a pen in my bag.", "I have a pen! Let’s get coffee!")),
                Tip("in · on (places)", "In means inside something. On means on top of or along something.",
                    "in the kitchen / on this street",
                    listOf("Marsha, I am in the kitchen! It is a beautiful kitchen!", "I sleep in the bedroom!", "It is on this street on the corner.")),
            ),
        ),
        6 to Level(
            words = listOf(
                "Weather" to listOf(v("sunny", "☀️"), v("cloudy", "☁️"), v("rainy", "🌧️"), v("snowy", "❄️"), v("windy", "🌬️"),
                    v("hot", "🥵"), v("cold", "🥶"), v("warm", "🌤️")),
                "My family" to listOf(v("mother", "👩"), v("father", "👨"), v("sister", "👧"), v("brother", "👦"),
                    v("grandmother", "👵"), v("grandfather", "👴"), v("uncle", "🧔"), v("aunt", "👩‍🦰"), v("cousin", "🧒"), v("baby", "👶")),
                "Places and jobs" to listOf(v("kitchen", "🍳"), v("bedroom", "🛏️"), v("bathroom", "🛁"), v("library", "📚"),
                    v("bank", "🏦"), v("teacher", "🧑‍🏫"), v("doctor", "🧑‍⚕️"), v("chef", "🧑‍🍳"), v("farmer", "🧑‍🌾"), v("writer", "✍️")),
            ),
            phrases = "Asking and answering" to listOf(
                Phrase("Is it sunny today?", "Ask about the weather.", "☀️"),
                Phrase("No, it is not windy today.", "Say what the weather is not.", "🌬️"),
                Phrase("No. Is there a bank near here?", "Ask where a place is.", "🏦"),
                Phrase("There is a bank behind you.", "Tell someone where a place is.", "👉"),
                Phrase("What's wrong?", "Ask a friend who looks sad or worried.", "😟"),
                Phrase("Do you want to talk about it?", "Offer to listen to a friend.", "🤗"),
                Phrase("What is your name?", "Ask someone's name.", "📛"),
                Phrase("What languages do you speak?", "Ask which languages someone knows.", "🗣️"),
                Phrase("Sorry, I can’t hear you.", "Ask someone to speak louder.", "👂"),
                Phrase("Yes, I can. Let me help.", "Offer help.", "🤝"),
            ),
            tips = listOf(
                Tip("Questions: Is it …? Are you …? Do you …?", "Put is or are first to ask a question. With other verbs, start with do.",
                    "It is sunny. → Is it sunny?",
                    listOf("Is it sunny today?", "Are you from Washington, D.C.?", "Do you like Washington, D.C.?", "No, I’m not.")),
                Tip("can · can’t", "Use can for things you are able to do, and can’t for things you are not able to do.",
                    "I can cook. / I can’t drive.",
                    listOf("Can you cook? You can be a chef in a restaurant.", "Yes, I can. Let me help.", "Tons of money? But I can’t code.", "Not everyone can write well. You can be a writer!")),
                Tip("There is …", "Use there is to say that something is in a place.", "There is a bank behind you.",
                    listOf("There is a bank behind you.", "No. Is there a bank near here?")),
                Tip("What · Where · Who", "What asks about things, where about places, and who about people.",
                    "What is your name? / Where are you going? / Who are they?",
                    listOf("What is your name?", "Where are you going?", "Who are they?", "What do they do?")),
            ),
        ),
        7 to Level(
            words = listOf(
                "Clothes" to listOf(v("shirt", "👕"), v("dress", "👗"), v("jeans", "👖"), v("jacket", "🧥"), v("hat", "👒"),
                    v("shoes", "👟"), v("socks", "🧦"), v("scarf", "🧣"), v("gloves", "🧤")),
                "Food and drink" to listOf(v("bread", "🍞"), v("rice", "🍚"), v("chicken", "🍗"), v("beef", "🥩"), v("apple", "🍎"),
                    v("banana", "🍌"), v("carrot", "🥕"), v("cheese", "🧀"), v("soup", "🍲"), v("water", "💧"), v("juice", "🧃")),
            ),
            pairs = listOf(Triple("Yesterday words", "→", listOf(
                rw("go", "🚶") to rw("went", "🚶"), rw("eat", "🍽️") to rw("ate", "🍽️"), rw("see", "👀") to rw("saw", "👀"),
                rw("buy", "🛒") to rw("bought", "🛒"), rw("make", "🛠️") to rw("made", "🛠️"), rw("have", "🎒") to rw("had", "🎒"),
                rw("say", "💬") to rw("said", "💬"), rw("come", "👋") to rw("came", "👋"), rw("write", "✍️") to rw("wrote", "✍️"),
                rw("tell", "🗣️") to rw("told", "🗣️")))),
            phrases = "Shopping and food" to listOf(
                Phrase("What time is it now?", "Ask the time.", "⏰"),
                Phrase("I want chicken.", "Say what food you want.", "🍗"),
                Phrase("You're out of chicken.", "The shop has no more of something.", "🚫"),
                Phrase("I’ll try another food truck. Thanks!", "Say you will go to a different shop.", "🚚"),
                Phrase("You're welcome.", "The answer to \"Thank you.\"", "😊"),
                Phrase("That looks great.", "Say something looks nice.", "👍"),
                Phrase("Oh, I don’t like this outfit.", "Say you don't like some clothes.", "🙅"),
                Phrase("I love the jacket! How about a hat?", "Say you like something and suggest another thing.", "🧥"),
            ),
            tips = listOf(
                Tip("I want … / I’ll have …", "Use I want or I'll have to ask for food. I'll have is a little more polite.",
                    "I’ll have the beef, please.",
                    listOf("I want chicken.", "Hello. I want the chicken dish.", "Excuse me, I’ll have the shrimp.")),
                Tip("going to (plans)", "Use am, is or are + going to + a verb for a plan.", "I am going to cook dinner.",
                    listOf("What are we going to do?", "I’m going to be a writer!", "This show is going to be a lot of fun.")),
                Tip("Yesterday: was · bought · told", "Many verbs change to talk about the past: is → was, buy → bought, tell → told.",
                    "Yesterday it was sunny.",
                    listOf("It was fine.", "Don’t worry, Marsha. I bought everything on the list.", "Yes, I told you: I have the shopping list. Can you hear it?")),
                Tip("will · won’t", "Use will and won't to promise or say what happens later.", "I will help. / I won't be late.",
                    listOf("Yes, Marsha, I will.", "No, Marsha, I won’t. See you later.")),
            ),
        ),
        8 to Level(
            words = listOf(
                "Feelings" to listOf(v("happy", "😊"), v("sad", "😢"), v("angry", "😠"), v("tired", "😴"), v("scared", "😨"),
                    v("excited", "🤩"), v("nervous", "😬"), v("bored", "🥱"), v("surprised", "😮"), v("calm", "😌")),
            ),
            pairs = listOf(Triple("Opposites", "↔", listOf(
                rw("big", "🐘") to rw("small", "🐭"), rw("tall", "🦒") to rw("short", "🐧"), rw("fast", "🐆") to rw("slow", "🐢"),
                rw("hot", "🔥") to rw("cold", "🧊"), rw("full", "🍱") to rw("empty", "🫙"), rw("near", "📍") to rw("far", "🔭"),
                rw("easy", "😎") to rw("hard", "😓"), rw("old", "🏚️") to rw("new", "🏠")))),
            phrases = "Polite and friendly" to listOf(
                Phrase("Can I borrow your scissors? Sorry to bother you.", "Ask politely to use something.", "✂️"),
                Phrase("Of course.", "A kind yes.", "👌"),
                Phrase("Are you okay? You seem... nervous.", "Ask a friend how they feel.", "😬"),
                Phrase("That's a good point.", "Agree that someone's idea is good.", "💡"),
                Phrase("Me too! How was your trip?", "Ask about someone's journey.", "🧳"),
                Phrase("Let me help you with your bags.", "Offer to carry something.", "👜"),
                Phrase("Have fun!", "Wish someone a good time.", "🎉"),
                Phrase("Good idea, Ashley. See you later!", "Agree, then say goodbye.", "👋"),
            ),
            tips = listOf(
                Tip("Have you ever …? (present perfect)", "Use have + a past word (seen, been, lived) for things in your life up to now.",
                    "I have never been to a zoo.",
                    listOf("Excuse me, can I help you? Have we met before?", "I have never seen a garden of sculptures! I’ll write that on my list!",
                        "I've never been to D.C.’s zoo. I've never been to any zoos!", "Well, I have lived here for over a year.")),
                Tip("May I … / Can I …? (asking politely)", "Start with may I or can I to ask to do something. May I is extra polite.",
                    "May I borrow your pen?",
                    listOf("Amelia, may I borrow your stapler?", "Can I borrow your scissors? Sorry to bother you.")),
                Tip("must · mustn’t", "Use must for something very important to do, and mustn't for something you should not do.",
                    "You must eat vegetables. / You mustn't eat too many sweets.",
                    listOf("You mustn’t buy donuts. They are only fat and sugar.")),
                Tip("Giving your opinion", "Say what you like and think, and give a reason with because.",
                    "I like the city because there are many museums.",
                    listOf("I like to live in the city.", "I like the museums. I really like the art museums.", "That's a good point.")),
            ),
        ),
    )
}
