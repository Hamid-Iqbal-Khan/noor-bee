"""Convert Tanzil-style Uthmani text (semarketir/quranjson, MIT) to IndoPak-style presentation.
Only presentation marks change; the letters stay the same, so the text still matches the reference."""
import re
FATHA, DAMMA, KASRA, SUKUN, SHADDA, KHARA, MADDA = 'َ', 'ُ', 'ِ', 'ْ', 'ّ', 'ٰ', 'ٓ'
MARKS = set('ًٌٍَُِّْٰٖۭٓٔٗۢۜ')
DROP = '۟۠ۖۗۘۙۚۛ۩۞۝'
LETTERS = set(chr(c) for c in range(0x0621, 0x064B)) - {'ـ'}

def is_letter(c): return c in LETTERS

def word(w, nxt_shadda=False, nxt_wasl=False, nxt_ba=False):
    w = w.replace('ٱ', 'ا')
    w = re.sub('([\u0648\u064A])\u06DF', '\\1\u200B', w)   # silent waw/ya (اُولٰٓئِكَ): mark so it gets no jazm
    for ch in DROP: w = w.replace(ch, '')
    w = w.replace('ۥ', 'ٗ').replace('ۦ', 'ٖ')                       # small waw / ya
    w = w.replace(DAMMA + 'ٗ', 'ٗ').replace(KASRA + 'ٖ', 'ٖ')         # هُۥ -> هٗ ، بِهِۦ -> بِهٖ
    w = re.sub('\u0640(?:([\u064B-\u0650])\u0654|\u0654([\u064B-\u0650]))',
               lambda m: 'ئ' + (m.group(1) or m.group(2)), w)                              # ـَٔ -> ئَ
    if not nxt_ba:
        w = re.sub('([ًٌٍ])[ۭۢ]', r'\1', w)                     # tanween marker only before ب
    w = w.replace('ۭ', 'ۢ')
    w = w.replace('ئ' + FATHA + 'ا', 'ا' + KHARA)                                        # فَـَٔاوٰى -> فَاٰوٰى
    w = w.replace('ء' + FATHA + 'ا', 'ا' + KHARA)                                        # ءَامَنُوا -> اٰمَنُوا
    w = w.replace('أ', 'ا').replace('إ', 'ا')                                            # hamza on alif -> plain alif
    w = re.sub(FATHA + '([وى])' + KHARA, KHARA + r'\1', w)                               # عَلَىٰ -> عَلٰى
    w = w.replace(FATHA + KHARA, KHARA)                                                  # ذَٰلِكَ -> ذٰلِكَ
    w = re.sub('(ل[\u064B-\u0652]*)ل(?:' + SHADDA + FATHA + '|' + FATHA + SHADDA + ')ه', r'\1ل' + SHADDA + KHARA + 'ه', w)  # Allah
    w = re.sub('ى([\u064E\u064F\u0650\u0651\u0652])', r'ي\1', w)                    # هِىَ -> هِيَ ، شَىْءٍ -> شَيْءٍ
    out, res = list(w), []
    for i, ch in enumerate(out):
        res.append(ch)
        if ch in 'وي' and i + 1 < len(out) and out[i + 1] == '\u200B':
            continue                                # silent letter: written bare, no jazm
        if not is_letter(ch) or ch in 'اآءة':
            continue
        nxt = out[i + 1] if i + 1 < len(out) else ''
        if nxt in MARKS and nxt != MADDA:
            continue
        prev = out[i - 1] if i else ''
        at_end = i == len(out) - 1
        long_vowel = (ch == 'و' and prev == DAMMA) or (ch in 'يى' and prev == KASRA)
        if long_vowel:
            if ch == 'ى': res[-1] = 'ي'
            if not (at_end and nxt_wasl):           # فِي الْ... : the long vowel drops, so no jazm
                res.append(SUKUN)
            continue
        if ch in 'وى':                              # silent / alif-maqsura letters stay bare
            continue
        if nxt == MADDA:
            continue
        following = out[i + 1:]
        doubled_next = len(following) >= 2 and is_letter(following[0]) and SHADDA in following[1:3]
        if (at_end and nxt_shadda) or (not at_end and doubled_next):
            continue                                # idgham / sun letter: no jazm
        res.append(SUKUN)
    return ''.join(res).replace('\u200B', '')

def first_has_shadda(w):
    w = w.lstrip('ٱا')
    return len(w) > 1 and SHADDA in w[1:3]

def convert(ayah):
    ws = []
    for x in ayah.split():
        if not x.strip(DROP): continue
        if ws and all(c == '\u06DC' for c in x):   # a lone sakta sign belongs to the word before it
            ws[-1] += x; continue
        ws.append(x)
    out = []
    for i, w in enumerate(ws):
        n = ws[i + 1] if i + 1 < len(ws) else ''
        out.append(word(w, first_has_shadda(n) if n else False, n.startswith('ٱ'), n.lstrip('ٱا').startswith('ب')))
    return ' '.join(out).strip()

def skeleton(t):
    t = t.replace('﻿', '').replace('ٖ', 'ي').replace('ۦ', 'ي')
    t = re.sub('[ً-ٕٗ-ٰٟۖ-ۭـ]', '', t)
    t = re.sub('\u0649(?=[\u0622-\u064A])', '', t)
    t = re.sub('[اأإآٱءئؤ]', '', t).replace('ى', 'ي').replace('ة', 'ه')
    return re.sub(r'\s', '', t)
