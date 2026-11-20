#!/usr/bin/env python3
"""Build the speaking levels from VOA Learning English "Let's Learn English" (public domain).

For each lesson in .github/voa/lessons.tsv: download the page, read the conversation transcript
(speaker lines) and the conversation MP3, find when each line is spoken with Whisper word
timestamps, cut one clip per line, and write
  <assets>/voa/<lesson>/all.ogg, <lesson>/<i>.ogg
  VoaData.kt (lessons + text -> clip map) and a report.
Lines that the recogniser cannot find in the audio (headings, notes, narration that isn't there)
are dropped, so only lines that are really spoken end up in the app.
Usage: fetch-voa.py <lessons.tsv> <assets dir> <VoaData.kt out> <report>
"""
import difflib, html, os, re, subprocess, sys, time, urllib.request

UA = "Mozilla/5.0 (X11; Linux x86_64) NoorBeeCourseBuilder/1.0 (+https://github.com/Hamid-Iqbal-Khan/noor-bee)"
SPEAKER = re.compile(r"^\s*([A-Z][A-Za-z.'’]{1,15}(?: [A-Z][A-Za-z.'’]{1,15})?)\s*:\s*(.{2,400})$")
NOT_SPEAKERS = {"Note", "Listen", "Speak", "Download", "Source", "Writer", "Editor", "Quiz", "Words", "Example", "Grammar",
                "Pronunciation", "Speaking", "Conversation", "Learning Strategy", "Writing", "Assessment", "Activity",
                "New Words", "Answer", "Question", "Task", "Summary", "Practice", "Comments", "Hint", "Tip", "Video", "Audio",
                "Test Yourself", "Lesson", "Level", "Hello", "Day", "Teacher", "Student", "Students"}


def fetch(url, binary=False):
    for attempt in range(4):
        try:
            req = urllib.request.Request(url, headers={"User-Agent": UA})
            with urllib.request.urlopen(req, timeout=90) as r:
                data = r.read()
                return data if binary else data.decode("utf-8", "replace")
        except Exception as e:
            print("retry", url, e, file=sys.stderr); time.sleep(3 + attempt * 4)
    raise RuntimeError("cannot fetch " + url)


def page_lines(page):
    page = re.sub(r"(?is)<(script|style|noscript)[^>]*>.*?</\1>", " ", page)
    page = re.sub(r"(?i)<br\s*/?>|</(p|div|li|h\d|tr)>", "\n", page)
    text = html.unescape(re.sub(r"<[^>]+>", " ", page))
    return [re.sub(r"\s+", " ", l).strip() for l in text.split("\n") if l.strip()]


def transcript(lines):
    out = []
    for l in lines:
        m = SPEAKER.match(l)
        if not m or m.group(1) in NOT_SPEAKERS:
            continue
        who, said = m.group(1).strip(), m.group(2).strip().strip('"“”').strip()
        if said and not said.lower().startswith("http"):
            out.append((who, said))
    # keep one copy of each line (pages repeat the transcript in mobile and desktop markup)
    seen, uniq = set(), []
    for t in out:
        if t not in seen:
            seen.add(t); uniq.append(t)
    return uniq


def conversation_mp3(page):
    links = re.findall(r"https://voa-audio[^\"'\s<>]+?\.mp3", page)
    plain = [u for u in links if "_hq" not in u]
    return (plain or links or [None])[0]


def norm(word):
    return re.sub(r"[^a-z0-9]", "", word.lower().replace("’", "'"))


def align(lines, words):
    """lines: [(who, text)], words: [(word, start, end)] -> [(who, text, start, end) or None]"""
    t_tokens, owner = [], []
    for i, (_, text) in enumerate(lines):
        for tok in text.split():
            n = norm(tok)
            if n:
                t_tokens.append(n); owner.append(i)
    a_tokens = [norm(w) for w, _, _ in words]
    sm = difflib.SequenceMatcher(None, t_tokens, a_tokens, autojunk=False)
    hit = {}
    for blk in sm.get_matching_blocks():
        for k in range(blk.size):
            hit[blk.a + k] = blk.b + k
    result = []
    for i, (who, text) in enumerate(lines):
        idx = [j for j, o in enumerate(owner) if o == i]
        got = [hit[j] for j in idx if j in hit]
        if not idx or len(got) < max(1, 0.6 * len(idx)):
            result.append(None); continue
        result.append((who, text, words[min(got)][1], words[max(got)][2]))
    return result


def kt(s):
    return '"' + s.replace("\\", "\\\\").replace('"', '\\"').replace("$", "\\$") + '"'


def main(tsv, assets, kt_out, report):
    from faster_whisper import WhisperModel
    model = WhisperModel("base.en", device="cpu", compute_type="int8")
    voa_dir = os.path.join(assets, "voa"); os.makedirs(voa_dir, exist_ok=True)
    lessons, clips, rep = [], {}, []
    for row in open(tsv, encoding="utf-8"):
        if not row.strip() or row.startswith("#"):
            continue
        level, num, title, url = row.rstrip("\n").split("\t")
        d = os.path.join(voa_dir, num); os.makedirs(d, exist_ok=True)
        try:
            page = fetch(url)
            lines = transcript(page_lines(page))
            mp3 = conversation_mp3(page)
            if not lines or not mp3:
                rep.append(f"L{num} {title}: no transcript ({len(lines)}) or no mp3 ({mp3})"); continue
            raw = os.path.join(d, "conv.mp3")
            open(raw, "wb").write(fetch(mp3, binary=True))
            segs, _ = model.transcribe(raw, language="en", word_timestamps=True, vad_filter=False)
            words = [(w.word, w.start, w.end) for s in segs for w in (s.words or [])]
            aligned = align(lines, words)
            kept = []
            for i, a in enumerate(aligned):
                if a is None:
                    continue
                who, text, start, end = a
                nxt = next((b[2] for b in aligned[i + 1:] if b), None)
                start = max(0.0, start - 0.12); end = end + 0.3 if nxt is None else min(end + 0.3, max(end, nxt - 0.02))
                clip = f"voa/{num}/{len(kept)}.ogg"
                subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-ss", f"{start:.2f}", "-to", f"{end:.2f}", "-i", raw,
                                "-ac", "1", "-ar", "24000", "-c:a", "libopus", "-b:a", "32k", os.path.join(assets, clip)], check=True)
                kept.append((who, text)); clips.setdefault(text, clip)
            subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-i", raw, "-ac", "1", "-ar", "24000", "-c:a", "libopus",
                            "-b:a", "32k", os.path.join(d, "all.ogg")], check=True)
            os.remove(raw)
            if len(kept) >= 2:
                lessons.append((int(level), int(num), title, url, f"voa/{num}/all.ogg", kept))
                clips.setdefault(" ".join(t for _, t in kept), f"voa/{num}/all.ogg")
            dropped = [t for (w, t), a in zip(lines, aligned) if a is None]
            rep.append(f"L{num} {title}: {len(kept)}/{len(lines)} lines kept; mp3 {mp3}")
            rep += [f"   {w}: {t}" for w, t in kept] + [f"   DROPPED: {t}" for t in dropped]
        except Exception as e:
            rep.append(f"L{num} {title}: FAILED {e}")

    with open(kt_out, "w", encoding="utf-8") as f:
        f.write("package com.example.english\n\n// Generated by .github/scripts/fetch-voa.py from VOA Learning English,\n"
                "// \"Let's Learn English\" (public domain). Do not edit by hand.\nobject VoaData {\n"
                "    val lessons: List<VoaLesson> = listOf(\n")
        for level, num, title, url, audio, kept in lessons:
            f.write(f"        VoaLesson({level}, {num}, {kt(title)}, {kt(url)}, {kt(audio)}, listOf(\n")
            f.write("".join(f"            Line({kt(w)}, {kt(t)}),\n" for w, t in kept))
            f.write("        )),\n")
        f.write("    )\n    val clips: Map<String, String> = mapOf(\n")
        f.write("".join(f"        {kt(EnglishKey(t))} to {kt(c)},\n" for t, c in clips.items()))
        f.write("    )\n}\n")
    open(report, "w").write("\n".join(rep) + "\n")
    print("\n".join(r for r in rep if not r.startswith("   ")))


def EnglishKey(text):  # must match EnglishAudio.key()
    return text.strip().strip(".,!?\"' ").lower().replace(" ", "_")


if __name__ == "__main__":
    main(*sys.argv[1:5])
