# Auto generated file, modify if you want to add functions.

.PHONY: all
all: clean init
	make -C src TARGET=../bin
	-make -C test

.PHONY: init
init:
	-mkdir bin

.PHONY: clean
clean:
	-rm -r bin/*
	make -C src clean
	
README: README.md
	pandoc README.md --latex-engine=xelatex -o README.pdf

runServer:
	java -cp "test:bin:lib/org.json.jar:;test;bin;lib/org.json.jar" TestServer

runClient:
	java -cp "test:bin:lib/org.json.jar:;test;bin;lib/org.json.jar" TestClient