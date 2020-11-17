# Build type and install directories:
CC=kos32-gcc
AR=kos32-ar
STRIP=kos32-strip
OBJCOPY=kos32-objcopy
LD=kos32-ld

CFLAGS=-c -fno-ident -O2 -fomit-frame-pointer -fno-ident -U__WIN32__ -U_Win32 -U_WIN32 -U__MINGW32__ -UWIN32 -Wno-pointer-arith
LDFLAGS= -call_shared -nostdlib -T $(SDK_DIR)/sources/newlib/app-dynamic.lds --image-base 0 --subsystem console
INCLUDES = -I $(SDK_DIR)/sources/newlib/libc/include -I $(SDK_DIR)/sources/libstdc++-v3/include -I.
LIBPATH = -L $(SDK_DIR)/../lib -L /home/autobuild/tools/win32/mingw32/lib -L $(SDK_DIR)/../mingw32/lib
LIBS =  -lstdc++ -lsupc++ -lgcc -lc
# You shouldn't need to edit anything below here.

OUT := build

SRCS := $(wildcard js*.c utf*.c regexp.c)
HDRS := $(wildcard js*.h mujs.h utf.h regexp.h)

default: shell
shell: $(OUT)/mujs $(OUT)/mujs-pp
static: $(OUT)/libmujs.a

astnames.h: jsparse.h
	grep -E '(AST|EXP|STM)_' jsparse.h | sed 's/^[^A-Z]*\(AST_\)*/"/;s/,.*/",/' | tr A-Z a-z > $@

opnames.h: jscompile.h
	grep -E 'OP_' jscompile.h | sed 's/^[^A-Z]*OP_/"/;s/,.*/",/' | tr A-Z a-z > $@

one.c: $(SRCS)
	ls $(SRCS) | awk '{print "#include \""$$1"\""}' > $@

jsdump.c: astnames.h opnames.h

$(OUT)/%.o: %.c $(HDRS)
	@ mkdir -p $(dir $@)
	$(CC) $(CFLAGS) $(INCLUDES) -o $@ -c $<

$(OUT)/libmujs.o: one.c $(HDRS)
	@ mkdir -p $(dir $@)
	$(CC) $(CFLAGS) $(INCLUDES) -o $@ -c $<

$(OUT)/libmujs.a: $(OUT)/libmujs.o
	@ mkdir -p $(dir $@)
	$(AR) cr $@ $^


$(OUT)/mujs: $(OUT)/libmujs.o $(OUT)/main.o
	@ mkdir -p $(dir $@)
	$(LD) $(LDFLAGS) $(LIBPATH)  -o $@ $^ $(LIBREADLINE) $(LIBS)
	$(STRIP) -s $(OUT)/mujs -o $(OUT)/mujs
	$(OBJCOPY) $(OUT)/mujs -O binary

$(OUT)/mujs-pp: $(OUT)/libmujs.o $(OUT)/pp.o
	@ mkdir -p $(dir $@)
	$(LD) $(LDFLAGS) $(LIBPATH) $ -o $@ $^ $(LIBS)
	$(STRIP) -s $(OUT)/mujs-pp -o $(OUT)/mujs-pp
	$(OBJCOPY) $(OUT)/mujs-pp -O binary

.PHONY: $(OUT)/mujs.pc
$(OUT)/mujs.pc:
	@ echo Creating $@
	@ echo > $@ Name: mujs
	@ echo >> $@ Description: MuJS embeddable Javascript interpreter
	@ echo >> $@ Version: $(VERSION)
	@ echo >> $@ Cflags: -I$(incdir)
	@ echo >> $@ Libs: -L$(libdir) -lmujs
	@ echo >> $@ Libs.private: -lm

watch:
	@ while ! inotifywait -q -e modify $(SRCS) $(HDRS) ; do time -p $(MAKE) ; done

clean:
	rm -rf build
