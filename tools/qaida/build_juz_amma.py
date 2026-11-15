"""Builds app/src/main/assets/qaida/juz_amma.tsv for the Urdu Qaida's longer-surah stages.

Quran text: semarketir/quranjson (MIT, see source/LICENSE-quranjson.txt), the standard Tanzil-based text,
converted to IndoPak-style marks by indopak.py (letters unchanged; checked below and again in CI against
api.alquran.cloud). Urdu word meanings: meanings/<surah>.txt, one line per ayah, words separated by " | ".

Run:  python3 tools/qaida/build_juz_amma.py
"""
import json, os, sys
HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
from indopak import convert, skeleton

ROOT = os.path.abspath(os.path.join(HERE, '..', '..'))
OUT = os.path.join(ROOT, 'app', 'src', 'main', 'assets', 'qaida', 'juz_amma.tsv')

def main():
    rows, errors, done = [], [], []
    for fn in sorted(os.listdir(os.path.join(HERE, 'meanings')), key=lambda f: -int(f.split('.')[0])):
        n = int(fn.split('.')[0])
        src = json.load(open(os.path.join(HERE, 'source', f'surah_{n}.json'), encoding='utf-8'))
        ayat = [v for k, v in sorted(src['verse'].items(), key=lambda kv: int(kv[0][6:])) if k != 'verse_0']
        lines = [l.rstrip('\n') for l in open(os.path.join(HERE, 'meanings', fn), encoding='utf-8') if l.strip()]
        if len(lines) != len(ayat):
            errors.append(f'{n}: {len(ayat)} ayat but {len(lines)} meaning lines'); continue
        for i, (a, line) in enumerate(zip(ayat, lines), 1):
            text = convert(a)
            if skeleton(text) != skeleton(a):
                errors.append(f'{n}:{i} letters changed by conversion')
            words, means = text.split(' '), [m.strip() for m in line.split('|')]
            if len(words) != len(means):
                errors.append(f'{n}:{i} has {len(words)} words but {len(means)} meanings\n    {" | ".join(words)}\n    {line}')
            if any(not m or '\t' in m for m in means):
                errors.append(f'{n}:{i} empty meaning')
            sajdah = '1' if '\u06E9' in a else '0'
            rows.append(f'{n}\t{i}\t{text}\t{"|".join(means)}\t{sajdah}')
        done.append(n)
    if errors:
        print('\n'.join(errors)); sys.exit(1)
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, 'w', encoding='utf-8') as f:
        f.write('# surah\tayah\ttext (IndoPak style, from quranjson/Tanzil, MIT)\tUrdu word meanings separated by |\tsajdah ayah (1/0)\n')
        f.write('\n'.join(rows) + '\n')
    print(f'wrote {len(rows)} ayat from surahs {done} to {OUT}')

MAFHOOM_OUT = os.path.join(ROOT, 'app', 'src', 'main', 'assets', 'qaida', 'mafhoom.tsv')

def mafhoom():
    """Easy whole-ayah Urdu meaning (mafhoom/<surah>.txt, one line per ayah) for every surah in the course."""
    rows, errors = [], []
    for fn in sorted(os.listdir(os.path.join(HERE, 'mafhoom')), key=lambda f: int(f.split('.')[0])):
        n = int(fn.split('.')[0])
        src = json.load(open(os.path.join(HERE, 'source', f'surah_{n}.json'), encoding='utf-8'))
        count = len([k for k in src['verse'] if k != 'verse_0']) if n != 1 else len(src['verse'])
        lines = [l.strip() for l in open(os.path.join(HERE, 'mafhoom', fn), encoding='utf-8') if l.strip()]
        if len(lines) != count:
            errors.append(f'mafhoom {n}: {count} ayat but {len(lines)} lines')
        rows += [f'{n}\t{i}\t{t}' for i, t in enumerate(lines, 1) if '\t' not in t]
    if errors:
        print('\n'.join(errors)); sys.exit(1)
    with open(MAFHOOM_OUT, 'w', encoding='utf-8') as f:
        f.write('# surah\tayah\teasy Urdu meaning of the whole ayah (written for this course; to be reviewed by a scholar)\n')
        f.write('\n'.join(rows) + '\n')
    print(f'wrote {len(rows)} easy meanings to {MAFHOOM_OUT}')

if __name__ == '__main__':
    main()
    mafhoom()
