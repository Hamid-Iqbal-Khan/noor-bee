#!/usr/bin/env python3
"""Fetch real human recordings for every word of the English course from Wikimedia Commons.

Reads the words straight from EnglishCourse.kt, looks each one up on Commons (American
"En-us-<word>.ogg" first, then Lingua Libre "LL-Q1860 (eng)-<speaker>-<word>.wav", then
other English accents), keeps only files whose licence allows commercial reuse (CC BY,
CC BY-SA, CC0, public domain), converts them to short mono Opus clips and writes:
  app/src/main/assets/audio/en/<key>.ogg, index.txt, CREDITS.csv
and audio-report.txt (found / missing) for the build reports.
Usage: fetch-english-audio.py <EnglishCourse.kt> <out dir> <report file>
"""
import csv, html, json, os, re, subprocess, sys, tempfile, time, urllib.parse, urllib.request

API = "https://commons.wikimedia.org/w/api.php"
UA = "NoorBeeCourseBuilder/1.0 (https://github.com/Hamid-Iqbal-Khan/noor-bee; CI audio fetch)"
OK_LICENCE = re.compile(r"^(CC[ -]BY(-SA)?( \d\.\d)?|CC0|Public domain|PD)", re.I)
BAD_LICENCE = re.compile(r"NC|ND", re.I)


def get(params):
    params = dict(params, format="json", formatversion="2")
    url = API + "?" + urllib.parse.urlencode(params)
    for attempt in range(4):
        try:
            req = urllib.request.Request(url, headers={"User-Agent": UA})
            with urllib.request.urlopen(req, timeout=60) as r:
                return json.load(r)
        except Exception as e:  # network hiccup: back off and retry
            print("retry", attempt, e, file=sys.stderr); time.sleep(2 + attempt * 3)
    raise SystemExit("Commons API unreachable")


def key(word):
    return word.strip().strip(".,!?\"' ").lower().replace(" ", "_")


def words_from(src):
    s = open(src, encoding="utf-8").read()
    out = []
    out += re.findall(r'\bw\("([^"]+)"', s)                         # practice words
    out += re.findall(r'Sound\("[^"]+", "([^"]+)"', s)              # picture keywords
    for body in re.findall(r'val (?:tricky\d|colours|numbers) = listOf\(([^)]*)\)', s):
        out += re.findall(r'"([^"]+)"', body)                       # sight words
    for text in re.findall(r'Sentence\("((?:[^"\\]|\\.)*)"', s):    # every word of every sentence
        out += re.findall(r"[A-Za-z][A-Za-z'-]*", text.replace('\\"', " "))
    seen, words = set(), []
    for wd in out:
        k = key(wd)
        if len(k) > 1 and k not in seen:
            seen.add(k); words.append(wd.lower() if wd != "I" else wd)
    return words


def info(titles):
    """title -> (url, licence, author, page) for files that exist."""
    res = {}
    for i in range(0, len(titles), 50):
        d = get({"action": "query", "titles": "|".join(titles[i:i + 50]), "prop": "imageinfo",
                 "iiprop": "url|extmetadata", "iiextmetadatafilter": "LicenseShortName|Artist"})
        norm = {n["to"]: n["from"] for n in d.get("query", {}).get("normalized", [])}
        for p in d.get("query", {}).get("pages", []):
            if p.get("missing") or not p.get("imageinfo"):
                continue
            ii = p["imageinfo"][0]; md = ii.get("extmetadata", {})
            lic = md.get("LicenseShortName", {}).get("value", "")
            author = re.sub(r"<[^>]+>", "", html.unescape(md.get("Artist", {}).get("value", ""))).strip() or "unknown"
            res[norm.get(p["title"], p["title"])] = (ii["url"], lic, author, ii.get("descriptionurl", ""))
            res[p["title"]] = res[norm.get(p["title"], p["title"])]
    return res


def lingua_libre(word):
    d = get({"action": "query", "list": "search", "srnamespace": "6", "srlimit": "50",
             "srsearch": f'intitle:"LL-Q1860 (eng)" "{word}"'})
    pat = re.compile(r"^File:LL-Q1860 \(eng\)-[^-]+-" + re.escape(word) + r"\.wav$", re.I)
    return [h["title"] for h in d.get("query", {}).get("search", []) if pat.match(h["title"])]


def allowed(lic):
    return bool(OK_LICENCE.match(lic)) and not BAD_LICENCE.search(lic)


def main(src, out_dir, report):
    os.makedirs(out_dir, exist_ok=True)
    words = words_from(src)
    first = []
    for wd in words:
        cap = wd[:1].upper() + wd[1:]
        first.append([f"File:En-us-{wd}.ogg", f"File:En-us-{cap}.ogg"])
    found = info([t for c in first for t in c])
    chosen, missing = {}, []
    for wd, cands in zip(words, first):
        hit = next((found[t] + (t,) for t in cands if t in found and allowed(found[t][1])), None)
        if not hit:
            ll = lingua_libre(wd)
            more = ll + [f"File:En-uk-{wd}.ogg", f"File:En-au-{wd}.ogg", f"File:En-ca-{wd}.ogg"]
            got = info(more)
            hit = next((got[t] + (t,) for t in more if t in got and allowed(got[t][1])), None)
        if hit:
            chosen[wd] = hit
        else:
            missing.append(wd)

    rows, index = [], []
    with tempfile.TemporaryDirectory() as tmp:
        for wd, (url, lic, author, page, title) in chosen.items():
            k = key(wd); raw = os.path.join(tmp, "in")
            req = urllib.request.Request(url, headers={"User-Agent": UA})
            try:
                with urllib.request.urlopen(req, timeout=60) as r, open(raw, "wb") as f:
                    f.write(r.read())
                trim = "silenceremove=start_periods=1:start_threshold=-45dB,areverse," \
                       "silenceremove=start_periods=1:start_threshold=-45dB,areverse,apad=pad_dur=0.08"
                subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-i", raw, "-ac", "1", "-ar", "24000",
                                "-af", trim, "-c:a", "libopus", "-b:a", "32k", os.path.join(out_dir, k + ".ogg")], check=True)
            except Exception as e:
                print("failed", wd, e, file=sys.stderr); missing.append(wd); continue
            index.append(k)
            rows.append([wd, author, lic, page or ("https://commons.wikimedia.org/wiki/" + urllib.parse.quote(title.replace(" ", "_")))])
            time.sleep(0.1)

    open(os.path.join(out_dir, "index.txt"), "w").write("\n".join(sorted(index)) + "\n")
    with open(os.path.join(out_dir, "CREDITS.csv"), "w", newline="") as f:
        w = csv.writer(f); w.writerow(["word", "author", "licence", "source"]); w.writerows(sorted(rows))
    with open(report, "w") as f:
        f.write(f"words: {len(words)}  recorded: {len(index)}  missing (phone voice): {len(missing)}\n")
        f.write("MISSING: " + ", ".join(sorted(set(missing))) + "\n")
        for r in sorted(rows):
            f.write(" | ".join(r) + "\n")
    print(open(report).readline().strip())
    # every bundled clip must have a credit line
    assert len(index) == len(rows)


if __name__ == "__main__":
    main(*sys.argv[1:4])
