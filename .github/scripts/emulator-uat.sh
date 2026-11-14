#!/usr/bin/env bash
# Emulator smoke/UAT run. Executed by android-emulator-runner as ONE script.
set -u
mkdir -p emu
APK=$(ls out/*.apk | head -1); echo "APK=$APK" | tee emu/install.txt
PKG=com.aistudio.noorbee.newwhiteboard.test
adb install -r "$APK" >> emu/install.txt 2>&1
adb shell pm list packages | grep -q "$PKG" && echo "INSTALLED OK" >> emu/install.txt
adb logcat -c
# Cold start time
adb shell am start -W -n "$PKG/com.example.MainActivity" > emu/start.txt 2>&1
sleep 6
shot() { adb exec-out screencap -p > "emu/$1.png"; adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; adb pull /sdcard/ui.xml "emu/$1.xml" >/dev/null 2>&1; }
tap_text() { # tap the centre of the first node whose text/content-desc contains $1
  adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; adb pull /sdcard/ui.xml /tmp/ui.xml >/dev/null 2>&1
  local b; b=$(python3 - "$1" <<'PY'
import re,sys
x=open('/tmp/ui.xml',encoding='utf-8').read(); t=sys.argv[1]
for m in re.finditer(r'<node [^>]*>', x):
    n=m.group(0)
    if t in (re.search(r'text="([^"]*)"',n) or [0,''])[1] or t in (re.search(r'content-desc="([^"]*)"',n) or [0,''])[1]:
        b=re.search(r'bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"',n)
        if b:
            x1,y1,x2,y2=map(int,b.groups()); print((x1+x2)//2,(y1+y2)//2); break
PY
)
  if [ -n "$b" ]; then adb shell input tap $b; echo "tap '$1' at $b" >> emu/steps.txt; sleep 3; else echo "NOT FOUND '$1'" >> emu/steps.txt; fi
}
shot 01_splash
tap_text "Learn!"; shot 02_after_start
tap_text "Child's Name"; adb shell input text "Aisha"; adb shell input keyevent 111; sleep 1
tap_text "Let's Go"; sleep 2; tap_text "Got it!"; shot 03_dashboard
tap_text "Daily Duas"; sleep 2; shot 04_duas; adb shell input keyevent 4; sleep 2
tap_text "Quran Explorer"; sleep 3; shot 05_quran
tap_text "Surat-ul-Fateha"; sleep 3; shot 06_surah_fatiha; adb shell input keyevent 4; sleep 2; adb shell input keyevent 4; sleep 2; shot 06b_back_to_dashboard
tap_text "Phonics Whiteboard"; sleep 2; shot 07_whiteboard; adb shell input keyevent 4; sleep 2
# Rotation
adb shell settings put system accelerometer_rotation 0; adb shell settings put system user_rotation 1; sleep 3; shot 08_landscape
adb shell settings put system user_rotation 0; sleep 2
# Process death / restart keeps data
adb shell am force-stop "$PKG"; adb shell am start -W -n "$PKG/com.example.MainActivity" >> emu/start.txt 2>&1; sleep 6; shot 09_restart
# Stress: random input
adb shell monkey -p "$PKG" --pct-syskeys 0 --pct-appswitch 0 --throttle 100 -s 42 -v 1500 > emu/monkey.txt 2>&1
sleep 2; shot 10_after_monkey
adb shell dumpsys meminfo "$PKG" > emu/meminfo.txt 2>&1
adb shell dumpsys gfxinfo "$PKG" > emu/gfxinfo.txt 2>&1
adb logcat -d -b crash > emu/crash.txt 2>&1
adb logcat -d '*:E' > emu/errors.txt 2>&1
{ echo "monkey_crashes=$(grep -c 'CRASH' emu/monkey.txt)"; echo "anr=$(grep -c 'NOT RESPONDING' emu/monkey.txt)"; echo "crash_buffer_lines=$(wc -l < emu/crash.txt)"; grep -E 'TotalTime|WaitTime' emu/start.txt; } > emu/summary.txt
cat emu/summary.txt
exit 0
