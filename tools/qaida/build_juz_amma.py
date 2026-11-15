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
            rows.append(f'{n}\t{i}\t{text}\t{"|".join(means)}')
        done.append(n)
    if errors:
        print('\n'.join(errors)); sys.exit(1)
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, 'w', encoding='utf-8') as f:
        f.write('# surah\tayah\ttext (IndoPak style, from quranjson/Tanzil, MIT)\tUrdu word meanings separated by |\n')
        f.write('\n'.join(rows) + '\n')
    print(f'wrote {len(rows)} ayat from surahs {done} to {OUT}')

if __name__ == '__main__':
    main()
