CC = kos32-gcc
LD = kos32-ld 
OBJCOPY=kos32-objcopy
KPACK = ../../../../tools/kpack

NAME=lua


ifndef SDK_DIR
SDK_DIR = /home/autobuild/tools/win32/sdk
endif

ifndef LIB_DIR
LIB_DIR = /home/autobuild/tools/win32/mingw32/lib
endif
	
LDFLAGS = -call_shared -S -nostdlib -T $(SDK_DIR)/sources/newlib/app-dynamic.lds \
          --image-base 0  --disable-runtime-pseudo-reloc  --subsystem native

CFLAGS = -c -fno-ident -O2 -fomit-frame-pointer -U__WIN32__ -U_Win32 -U_WIN32 -U__MINGW32__ -UWIN32  -DLUA_COMPAT_ALL -DLUA_ANSI

INCLUDES= -I $(SDK_DIR)/sources/newlib/libc/include
LIBPATH:= -L $(SDK_DIR)/lib -L $(LIB_DIR) 
LIBS= -lgcc -ldll -lc.dll

SOURCES =  lapi.c \
  lauxlib.c \
  lbaselib.c \
  lbitlib.c \
  lcode.c \
  lcorolib.c\
  lctype.c \
  ldblib.c \
  ldebug.c \
  ldo.c \
  ldump.c \
  lfunc.c \
  lgc.c \
  linit.c \
  liolib.c\
  llex.c \
  lmem.c \
  loadlib.c \
  lmathlib.c \
  lobject.c \
  lopcodes.c \
  loslib.c \
  lparser.c \
  lstate.c \
  lstring.c \
  lstrlib.c \
  ltable.c \
  ltablib.c \
  ltm.c \
  lua.c \
  lundump.c \
  lvm.c \
  lzio.c \
  kolibri.c \
  $(NULL)
 

OBJECTS =  $(patsubst %.c, %.o, $(SOURCES))

default: $(NAME)

$(NAME): $(OBJECTS) 
	$(LD) $(LDFLAGS) $(LIBPATH) -o $(NAME) $(OBJECTS) $(LIBS)
	$(OBJCOPY) $(NAME) -O binary
	$(KPACK) $(NAME)

%.o : %.c $(SOURCES)
	$(CC) $(CFLAGS) $(INCLUDES) -o $@ $<
	
clean:
	rm -f $(NAME) *.o
install:
	cp -f $(NAME) ../../../../build_iso/kolibrios/develop/lua
