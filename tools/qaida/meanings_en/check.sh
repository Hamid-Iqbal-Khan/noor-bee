#!/bin/bash
# usage: ./check.sh 80 81 ...   compares the number of meanings per ayah with the Urdu file
for f in "$@"; do
  if diff <(awk -F'|' '{print NF}' ../meanings/$f.txt) <(awk -F'|' '{print NF}' $f.txt) >/dev/null; then echo "$f ok"; else echo "$f MISMATCH"; diff <(awk -F'|' '{print NR": "NF}' ../meanings/$f.txt) <(awk -F'|' '{print NR": "NF}' $f.txt); fi
done
