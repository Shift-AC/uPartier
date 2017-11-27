# Auto generated file, modify if you want to add functions.

JAVALIBWIN := test:bin/upartier.jar:lib/org.json.jar:/lib/jce.jar:/lib/sunjce_provider.jar
JAVALIBLINUX := $(subst :,;,$(JAVALIBWIN))
JAVALIB := $(JAVALIBWIN);$(JAVALIBLINUX)

.PHONY: all
all: clean init
	make -C src TARGET=../bin
	-make -C test

.PHONY: init
init:
	-mkdir bin

.PHONY: jar
jar:
	cp bin/com . -r
	jar cf upartier.jar com/
	rm com -r

.PHONY: clean
clean:
	-rm -r bin/*
	-rm upartier.jar
	make -C src clean
	
README: README.md
	pandoc README.md --latex-engine=xelatex -o README.pdf

runServer:
	java -cp "$(JAVALIB)" TestServer

runClient:
	java -cp "$(JAVALIB)" TestClient