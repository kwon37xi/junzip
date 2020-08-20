# JUnzip
JUnzip 은 MS-Windows에서 압축되어 압축 된 파일들의 파일명이 MS949 (혹은 그 외)인
Zip 파일을 UTF-8 (혹은 그 외 유니코드) 리눅스/기타 운영체제에서 풀기 위한
프로그램입니다.

## 필요한 패키지
  * Java 1.4
  * jazzlib : http://jazzlib.sourceforge.net/ 0.07 (실제로 압축 푸는 역할을 함)
  * commons-cli 1.0 : http://jakarta.apache.org (명령행 인자 분석기)

## 설치하기
 * root 권한으로
```
su -
tar xvzf JUnzip_{version}_bin.tar.gz
mv JUnzip_{version} /opt
```
 * 각 사용자 권한으로 ~/.bashrc 에 다음 내용 추가
  export JAVA_HOME=/path/to/java/home
  export JUNZIP_HOME=/opt/JUnzip_{version}
  export PATH=$PATH:$JAVA_HOME/bin:$JUNZIP_HOME/bin
  
## 사용하기
```
junzip -l filename.zip : 압축된 파일 목록 보기
junzip filename.zip : MS949 인코딩으로 가정(한글 MS-Windows에서 압축되었을 때)하고 압축 풀기
junzip -h : 도움말 보기
```

## TODO
- gradle 6.x
- Java 8 or later
- PPA
