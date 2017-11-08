# mkjproj 1.0 by Shift_AC
# Auto generated file, modify to let `make` run correctly.

TARGET := .
JAVAC := javac
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

.PHONY: all
all:
	$(JAVAC) $(JAVAFLAGS) $(JAVASRC) -d $(TARGET) -cp "$(JAVALIB)"

.PHONY: clean
clean:
	-rm *.class *~
	