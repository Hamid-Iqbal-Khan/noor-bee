"""Builds app/src/main/assets/qaida/ui_en.tsv: Urdu UI string -> English (for English-medium mode).

Inputs: _urdu_strings.txt ("file\\tUrdu", one per line, as extracted from qaida/*.kt) and
en_*.txt ("lineNo\\tEnglish"; "-" = not shown in English mode). Extra pairs can be added
in extra.tsv ("Urdu\\tEnglish") for strings built at run time.
"""
import glob, os, sys
here = os.path.dirname(os.path.abspath(__file__))
root = os.path.abspath(os.path.join(here, "..", "..", ".."))
src = [l.rstrip("\n").split("\t", 1)[1] for l in open(os.path.join(here, "_urdu_strings.txt"), encoding="utf-8")]
en = {}
for f in sorted(glob.glob(os.path.join(here, "en_[a-e].txt"))):
    for l in open(f, encoding="utf-8"):
        l = l.rstrip("\n")
        if not l.strip():
            continue
        n, v = l.split("\t", 1)
        en[int(n)] = v
missing = [i + 1 for i in range(len(src)) if i + 1 not in en]
if missing:
    sys.exit(f"no English for lines {missing[:20]}")
pairs = {}
def unesc(s):
    s = s.replace('\\"', '"').replace("\\$", "$")
    while s.endswith("\\n"):
        s = s[:-2]
    return s.replace("\\n", "\n")
for i, ur in enumerate(src):
    v = en[i + 1]
    if v == "-":
        continue
    pairs[unesc(ur)] = v
# second extraction: every other Arabic-script literal; lines without an English entry are Arabic content, kept as is
xsrc = [l.rstrip("\n").split("\t", 1)[1] for l in open(os.path.join(here, "_extra_strings.txt"), encoding="utf-8")]
for l in open(os.path.join(here, "en_x.txt"), encoding="utf-8"):
    l = l.rstrip("\n")
    if l.strip():
        n, v = l.split("\t", 1)
        pairs[unesc(xsrc[int(n) - 1])] = v
extra = os.path.join(here, "extra.tsv")
if os.path.exists(extra):
    for l in open(extra, encoding="utf-8"):
        l = l.rstrip("\n")
        if l and not l.startswith("#"):
            k, v = l.split("\t", 1)
            pairs[k] = v
out = os.path.join(root, "app/src/main/assets/qaida/ui_en.tsv")
with open(out, "w", encoding="utf-8") as o:
    for k, v in pairs.items():
        assert "\t" not in k and "\n" not in k and "\t" not in v, k
        o.write(f"{k}\t{v}\n")
print(f"{len(pairs)} entries -> {os.path.relpath(out, root)}")
