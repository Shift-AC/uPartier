# mkjproj 1.0 by Shift_AC
# Auto generated file, modify to let `make` run correctly.

TARGET := .
JAVAC := javac
PACKAGES := com.github.shiftac.upartier;
PROG := 
JAVASRCPATH := $(subst .,/,$(PACKAGES))
JAVASRC := $(subst ;,/*.java ,$(JAVASRCPATH))
JAVAFLAGS := -encoding utf8

.PHONY: all
all:
	$(JAVAC) $(JAVASRC) -d $(TARGET) $(JAVAFLAGS) -sourcepath $(JAVASRCPATH)

.PHONY: clean
clean:
	-rm *.class *~
	