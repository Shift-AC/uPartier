# mkjproj 1.0 by Shift_AC
# Auto generated file, modify to let `make` run correctly.

TARGET := .
JAVAC := javac
PROG := 
JAVALIB := ../bin
JAVASRC = *.java
JAVAFLAGS := -encoding utf8 -Xlint:all

.PHONY: all
all:
	$(JAVAC) $(JAVAFLAGS) $(JAVASRC) -d $(TARGET) -cp $(JAVALIB)

runServer:
	java -cp test:bin:lib/org.json.jar TestServer

runClient:
	java -cp $(JAVALIB) TestClient

.PHONY: clean
clean:
	-rm *.class *~
	