# mkjproj 1.0 by Shift_AC
# Auto generated file, modify to let `make` run correctly.

TARGET := .
JAVAC := javac
MAIN_PACKAGE := com.github.shiftac.upartier
PACKAGES := \
com.github.shiftac.upartier;\
com.github.shiftac.upartier.network;\
com.github.shiftac.upartier.network.server;\
com.github.shiftac.upartier.network.app;\
com.github.shiftac.upartier.network.demo;\
com.github.shiftac.upartier.data;
JAVASRCPATH := $(subst .,/,$(PACKAGES))
JAVASRC := $(subst ;,/*.java ,$(JAVASRCPATH))
LIB := ../lib
JAVALIBLINUX := $(LIB)/org.json.jar:$(LIB)/jce.jar:$(LIB)/sunjce_provider.jar:
JAVALIBWIN := $(subst :,;,$(JAVALIBLINUX))
JAVALIB := $(JAVALIBWIN):$(JAVALIBLINUX)
JAVAFLAGS := -encoding utf8 -Xlint:all
#MANIFEST := "Class-Path: lib/org.json.jar lib/jce.jar lib/sunjce_provider.jar"

.PHONY: all
all:
	$(JAVAC) $(JAVAFLAGS) $(JAVASRC) -d $(TARGET) -cp "$(JAVALIB)"

.PHONY: jar
jar:
	jar cf $(TARGET)/$(MAIN_PACKAGE).jar $(TARGET)/*

.PHONY: clean
clean:
	-rm *.class *~
	