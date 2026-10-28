"""closure.py 가 만든 목록의 클래스를 공통컴포넌트 소스에서 이 프로젝트로 복사한다(이미 있는 파일은 건드리지 않는다).

사용법:
    python -I tools/copy_closure.py <공통컴포넌트 src/main/java> <이 프로젝트 src/main/java> <closure_last.txt> [--dry-run]

복사한 뒤에는 반드시 mvn compile 로 빠진 의존성(라이브러리, 설정 빈)을 확인한다.
"""
import os
import shutil
import sys

args = [a for a in sys.argv[1:] if not a.startswith("--")]
dry_run = "--dry-run" in sys.argv
if len(args) != 3:
    sys.exit(__doc__)
source_root, target_root, list_file = args

copied = []
for line in open(list_file, encoding="utf-8").read().split():
    rel = os.path.join(*line.split(".")) + ".java"
    target = os.path.join(target_root, rel)
    if os.path.exists(target):
        continue
    if not dry_run:
        os.makedirs(os.path.dirname(target), exist_ok=True)
        shutil.copy(os.path.join(source_root, rel), target)
    copied.append(line)

print(("복사 예정" if dry_run else "복사함") + ": %d개" % len(copied))
for c in copied:
    print("  " + c)
