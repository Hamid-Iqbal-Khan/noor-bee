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
find_text() { # print the centre of the first node whose text/content-desc contains $1
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
  echo "$b"
}
tap_text() { # tap $1; if it is not on screen, scroll down a little and look again (up to 3 times)
  local b; local tries=0
  b=$(find_text "$1")
  while [ -z "$b" ] && [ $tries -lt 3 ] && [ "$1" != "Wait" ]; do
    adb shell input swipe 540 1600 540 1000 300; sleep 1; tries=$((tries+1)); b=$(find_text "$1")
  done
  if [ -n "$b" ]; then adb shell input tap $b; echo "tap '$1' at $b" >> emu/steps.txt; sleep 3; else echo "NOT FOUND '$1'" >> emu/steps.txt; fi
}
shot 01_splash
# a slow CI emulator sometimes shows "Pixel Launcher isn't responding" over the app: close it, then bring the app back
adb shell am force-stop com.google.android.apps.nexuslauncher || true
adb shell am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS >/dev/null 2>&1 || true
adb shell am start -n "$PKG/com.example.MainActivity" >/dev/null 2>&1; sleep 3
tap_text "Wait"; sleep 1  # dismiss a system "isn't responding" dialog on slow CI emulators, if any
tap_text "Learn!"; shot 02_after_start
tap_text "Child's Name"; adb shell input text "Aisha"; adb shell input keyevent 111; sleep 1
tap_text "Let's Go"; sleep 2; tap_text "Got it!"; shot 03_dashboard
# Noorani Qaida (Urdu-medium Arabic course): English | Urdu selector, Urdu row = Stage 1 / Stage 2
tap_text "اردو"; sleep 2; shot 10_dashboard_urdu
tap_text "مرحلہ ۲: جوڑ"; sleep 3; shot 15_qaida_stage2_home
tap_text "حروفِ مقطعات"; sleep 2; shot 18_qaida_muqattaat; adb shell input keyevent 4; sleep 1
tap_text "تنوین: دو زبر"; sleep 2; shot 16_qaida_tanween; adb shell input keyevent 4; sleep 1
adb shell input swipe 540 1700 540 900 300; sleep 1
tap_text "کھڑا زبر، کھڑی زیر"; sleep 2; shot 17_qaida_khara; adb shell input keyevent 4; sleep 1
adb shell input keyevent 4; sleep 2
tap_text "مرحلہ ۱: حروف"; sleep 3; shot 11_qaida_home
tap_text "حروفِ مفردات"; sleep 2; shot 12_qaida_letters
tap_text "ثَا"; sleep 2; shot 13_qaida_letter_detail; adb shell input keyevent 4; sleep 1
adb shell input keyevent 4; sleep 1; tap_text "حرکات: زبر، زیر، پیش"; sleep 2; shot 14_qaida_harakat
adb shell input keyevent 4; sleep 1; adb shell input keyevent 4; sleep 2
tap_text "مرحلہ ۳: تلاوت"; sleep 3; shot 20_qaida_stage3_home
adb shell input swipe 540 1700 540 800 300; sleep 1
tap_text "تلاوت: سورۃ الفاتحہ"; sleep 2; shot 21_qaida_fatiha; adb shell input keyevent 4; sleep 1
adb shell input swipe 540 1700 540 800 300; sleep 1
tap_text "حروفِ شمسی و قمری"; sleep 2; shot 22_qaida_shamsi; adb shell input keyevent 4; sleep 1
adb shell input keyevent 4; sleep 2
tap_text "مرحلہ ۴: وقف"; sleep 3; shot 23_qaida_stage4_home
tap_text "کہاں رکنا ہے"; sleep 2; shot 24_qaida_waqf_signs; adb shell input keyevent 4; sleep 1
adb shell input swipe 540 1700 540 800 300; sleep 1
tap_text "پانچ آیتیں"; sleep 2; shot 25_qaida_falaq; adb shell input keyevent 4; sleep 1
adb shell input keyevent 4; sleep 2
tap_text "مرحلہ ۵: معنی"; sleep 3; shot 26_qaida_stage5_home
tap_text "ہر لفظ کے نیچے"; sleep 2; shot 27_qaida_fatiha_wbw; adb shell input keyevent 4; sleep 1
adb shell input keyevent 4; sleep 2
tap_text "مرحلہ ۶: سورتیں"; sleep 3; shot 28_qaida_stage6_home
tap_text "تمہارا دین تمہارے لیے"; sleep 2; shot 29_qaida_kafirun; adb shell input keyevent 4; sleep 1
adb shell input keyevent 4; sleep 2
tap_text "مرحلہ ۷: تجوید"; sleep 3; shot 30_qaida_stage7_home
tap_text "حلق کے ۶ حروف"; sleep 2; shot 31_qaida_izhar; adb shell input keyevent 4; sleep 1
adb shell input keyevent 4; sleep 2
adb shell input swipe 540 1700 540 1000 300; sleep 1
tap_text "مرحلہ ۸: عمّ ۱"; sleep 3; shot 32_qaida_stage8_home
tap_text "سورۃ الہمزہ"; sleep 3; shot 33_qaida_humazah; adb shell input keyevent 4; sleep 1
adb shell input keyevent 4; sleep 2
adb shell input swipe 540 1700 540 1100 300; sleep 1
tap_text "مرحلہ ۱۱: گرامر"; sleep 3; shot 34_qaida_stage11_home
tap_text "تین حرفی جڑ"; sleep 2; shot 35_qaida_roots; adb shell input keyevent 4; sleep 1
adb shell input keyevent 4; sleep 2
adb shell input swipe 540 1000 540 1700 300; sleep 1
adb shell input swipe 540 1700 540 700 300; sleep 1; shot 19_dashboard_arabic_section
adb shell input swipe 540 700 540 1700 300; sleep 1
adb shell input swipe 540 600 540 2000 200; sleep 1; adb shell input swipe 540 600 540 2000 200; sleep 1; adb shell input swipe 540 600 540 2000 200; sleep 1
tap_text "English"; sleep 2
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
