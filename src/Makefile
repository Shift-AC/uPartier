# mkjproj 1.0 by Shift_AC
# Auto generated file, modify to let `make` run correctly.

TARGET := .
JAVAC := javac
JAVADOC := javadoc
#JAVAC := /usr/share/jdk1.8.0_152/bin/javac
MAIN_PACKAGE := com.github.shiftac.upartier
PACKAGES := \
com.github.shiftac.upartier;\
com.github.shiftac.upartier.network;\
com.github.shiftac.upartier.network.server;\
com.github.shiftac.upartier.network.app;\
com.github.shiftac.upartier.network.demo;\
com.github.shiftac.upartier.data;
#com.github.shiftac.upartier.serverdata;
JAVASRCPATH := $(subst .,/,$(PACKAGES))
JAVASRC := $(subst ;,/*.java ,$(JAVASRCPATH))
LIB := ../lib
JAVALIBLINUX := $(LIB)/org.json.jar:#$(LIB)/jce.jar:$(LIB)/sunjce_provider.jar:
JAVALIBWIN := $(subst :,;,$(JAVALIBLINUX))
JAVALIB := $(JAVALIBWIN):$(JAVALIBLINUX)
JAVAFLAGS := -encoding utf8 -Xlint:all
JAVADOCFLAGS := -private
#MANIFEST := "Class-Path: lib/org.json.jar lib/jce.jar lib/sunjce_provider.jar"

.PHONY: all
all:
	$(JAVAC) $(JAVAFLAGS) $(JAVASRC) -d $(TARGET) -cp "$(JAVALIB)"

.PHONY: javadoc
javadoc:
	$(JAVADOC) $(JAVASRC) -d $(TARGET) $(JAVADOCFLAGS) -cp "$(JAVALIB)"

.PHONY: clean
clean:
	-rm *.class *~
	