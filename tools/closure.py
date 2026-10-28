"""공통컴포넌트(egovframe-common-components) 소스에서 어떤 클래스를 시작점으로 가져올 때,
함께 가져와야 하는 자바 클래스 범위(closure)를 계산한다.

사용법 (공통컴포넌트 소스를 받은 뒤):
    python -I tools/closure.py <공통컴포넌트 src/main/java 경로> [옵션] <시작점...>

시작점은 패키지 접두사(예: egovframework.com.cop.bbs.web)나 클래스 전체 이름이다.

옵션:
    --out FILE          계산한 클래스 목록을 파일로 저장 (기본: closure_last.txt)
    --missing-in DIR    이 프로젝트 소스(src/main/java)에 이미 있는 클래스는 결과에서 뺀다
    --exclude TEXT      이름에 TEXT 가 들어간 클래스는 결과에서 뺀다 (여러 번 지정 가능)

항상 제외하는 클래스: EgovUserDetailsSessionServiceImpl
    (세션 방식 사용자 서비스는 5.0.7 에서 권한 조회가 서로를 끝없이 호출하는 구조라 쓰면 안 된다. README 참고)

무엇을 따라가는가 (Spring 은 구현체를 컴포넌트 스캔으로 찾으므로 import 만 보면 빠진다):
    1. import 한 클래스, 같은 패키지에서 import 없이 쓰는 클래스
    2. import 없이 전체 이름(egovframework.com...)으로 직접 쓰는 클래스
    3. 인터페이스를 구현한 클래스(구현체). 부모 클래스의 모든 하위 클래스는 따라가지 않는다.
따라가지 못하는 것: 매퍼 XML 안의 OGNL 참조(@패키지.클래스@메서드), JSP 가 쓰는 클래스, 리플렉션.
그 부분은 XML/JSP 에서 "egovframework.com" 문자열을 검색해서 직접 확인한다.
"""
import argparse
import os
import re
import sys
from collections import Counter, defaultdict

parser = argparse.ArgumentParser()
parser.add_argument("root")
parser.add_argument("seeds", nargs="+")
parser.add_argument("--out", default="closure_last.txt")
parser.add_argument("--missing-in")
parser.add_argument("--exclude", action="append", default=[])
args = parser.parse_args()

file_info = {}  # 전체 이름 -> (패키지, 소스)
pkg_classes = defaultdict(dict)  # 패키지 -> {단순 이름: 전체 이름}

for d, _, files in os.walk(args.root):
    for f in files:
        if not f.endswith(".java"):
            continue
        src = open(os.path.join(d, f), encoding="utf-8", errors="ignore").read()
        m = re.search(r"^\s*package\s+([\w.]+)\s*;", src, re.M)
        if not m:
            continue
        pkg, name = m.group(1), f[:-5]
        file_info[pkg + "." + name] = (pkg, src)
        pkg_classes[pkg][name] = pkg + "." + name

IMPORT = re.compile(r"^\s*import\s+(static\s+)?([\w.]+?)(\.\*)?\s*;", re.M)
TOKEN = re.compile(r"[A-Za-z_][A-Za-z0-9_]*")
FQ_REF = re.compile(r"egovframework\.com(?:\.[a-z0-9]+)+\.[A-Z][A-Za-z0-9_]*")
DECL = re.compile(
    r"(?:class|interface)\s+\w+(?:<[^>{]*>)?\s*(?:extends\s+([\w.<>, ]+?))?\s*(?:implements\s+([\w.<>, ]+?))?\s*\{")

implementers = defaultdict(set)  # 단순 이름 -> 이를 구현/상속한 클래스
interfaces = set()
for fq, (pkg, src) in file_info.items():
    simple = fq.rsplit(".", 1)[-1]
    if re.search(r"\binterface\s+" + simple + r"\b", src):
        interfaces.add(fq)
    for m in DECL.finditer(src):
        for group in (m.group(1), m.group(2)):
            if group:
                for n in re.split(r"[,\s]+", re.sub(r"<[^>]*>", "", group)):
                    if n:
                        implementers[n.split(".")[-1]].add(fq)


def dependencies(fq):
    pkg, src = file_info[fq]
    out = set()
    tokens = set(TOKEN.findall(IMPORT.sub("", src)))
    for m in IMPORT.finditer(src):
        name, star = m.group(2), m.group(3)
        if star:
            for n, f in pkg_classes.get(name, {}).items():
                if n in tokens:
                    out.add(f)
        elif name in file_info:
            out.add(name)
        elif name.rsplit(".", 1)[0] in file_info:  # 중첩 클래스 import
            out.add(name.rsplit(".", 1)[0])
    for n, f in pkg_classes[pkg].items():  # 같은 패키지는 import 없이 참조
        if n in tokens and f != fq:
            out.add(f)
    out.update(c for c in FQ_REF.findall(src) if c in file_info)  # 전체 이름 직접 참조
    return out


start = [fq for fq in file_info
         if any(fq.startswith(s) or file_info[fq][0].startswith(s) for s in args.seeds)]
if not start:
    sys.exit("시작점에 해당하는 클래스가 없습니다: " + " ".join(args.seeds))

seen, stack = set(start), list(start)
while stack:
    cur = stack.pop()
    extra = implementers.get(cur.rsplit(".", 1)[-1], set()) if cur in interfaces else set()
    for dep in list(dependencies(cur)) + list(extra):
        if dep not in seen:
            seen.add(dep)
            stack.append(dep)

ALWAYS_EXCLUDE = ["EgovUserDetailsSessionServiceImpl"]
result = sorted(c for c in seen if not any(x in c for x in args.exclude + ALWAYS_EXCLUDE))
if args.missing_in:
    result = [c for c in result
              if not os.path.exists(os.path.join(args.missing_in, *c.split(".")) + ".java")]

by_package = Counter()
for c in result:
    parts = file_info[c][0].split(".")
    by_package[".".join(parts[:5])] += 1

print("시작 클래스: %d, 필요한 클래스: %d (전체 %d 중)" % (len(start), len(result), len(file_info)))
for pkg, n in by_package.most_common(15):
    print("%5d  %s" % (n, pkg))
with open(args.out, "w", encoding="utf-8", newline="\n") as fh:
    fh.write("\n".join(result) + "\n")
print("목록 저장:", args.out)
