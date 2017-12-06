# Auto generated file, modify if you want to add functions.

CPWIN := upartier.jar:lib/org.json.jar:/lib/jce.jar:/lib/sunjce_provider.jar
CPLINUX := $(subst :,;,$(CPWIN))
CP := $(CPWIN);$(CPLINUX)

.PHONY: all
all: clean init
	make -C src TARGET=../bin
	make jar

.PHONY: init
init:
	-mkdir bin
	-mkdir doc

.PHONY: jar
jar:
	cp Makefile bin/Makefile
	make -C bin dojar
	mv bin/upartier.jar ./upartier.jar
	rm bin/Makefile

.PHONY: dojar
dojar:
	jar cf upartier.jar com/

.PHONY: javadoc
javadoc:
	make -C src javadoc TARGET=../doc

.PHONY: clean
clean:
	-rm -r bin/*
	-rm upartier.jar
	make -C src clean
	
README: README.md
	pandoc README.md --latex-engine=xelatex -o README.pdf

runServerDemo:
	java -cp "$(CP)" com.github.shiftac.upartier.network.demo.EchoServer

runClientDemo:
	java -cp "$(CP)" com.github.shiftac.upartier.network.demo.EchoClient

runServerTest:
	java -cp "$(CP)" com.github.shiftac.upartier.network.server.Server

runClientTest:
	java -cp "$(CP)"  com.github.shiftac.upartier.network.app.Client