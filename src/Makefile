# mkjproj 1.0 by Shift_AC
# Auto generated file, modify to let `make` run correctly.

TARGET := .
JAVAC := javac
PACKAGES := com.github.shiftac.upartier;com.github.shiftac.upartier.network;com.github.shiftac.upartier.network.server;com.github.shiftac.upartier.network.app;com.github.shiftac.upartier.network.demo;
PROG := 
JAVASRCPATH := $(subst .,/,$(PACKAGES))
JAVASRC := $(subst ;,/*.java ,$(JAVASRCPATH))
JAVALIB := ../lib/org.json.jar
JAVAFLAGS := -encoding utf8 -Xlint:all

.PHONY: all
all:
	$(JAVAC) $(JAVAFLAGS) $(JAVASRC) -d $(TARGET) -cp "$(JAVALIB)" 
# -sourcepath "$(JAVASRCPATH)"

.PHONY: clean
clean:
	-rm *.class *~
	