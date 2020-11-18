KPACK = ../../../../tools/kpack
FASM = fasm
LD = kos32-ld
OBJCOPY = kos32-objcopy
STRIP = kos32-strip 
CC = kos32-gcc

NAME = kosjs

ifndef SDK_DIR
SDK_DIR = /home/autobuild/tools/win32/sdk
endif

ifndef LIB_DIR
LIB_DIR = /home/autobuild/tools/win32/mingw32/lib
endif
	
LDFLAGS = -call_shared -nostdlib -T $(SDK_DIR)/sources/newlib/app-dynamic.lds --image-base 0  --subsystem native
LIBPATH = -L $(SDK_DIR)/lib -L $(LIB_DIR) -L $(LIB_DIR)/../mingw32/lib -Llib
CFLAGS = -c -fno-ident -O2 -fomit-frame-pointer -fno-ident -U__WIN32__ -U_Win32 -U_WIN32 -U__MINGW32__ -UWIN32
INCLUDES = -I $(SDK_DIR)/sources/newlib/libc/include

all:
	$(CC) $(CFLAGS) $(INCLUDES) kosjs.c import.c
	$(LD) $(LDFLAGS) $(LIBPATH) -o $(NAME) $(NAME).o import.o -lmujs -lgcc -lc.dll 
	$(STRIP) -s $(NAME) -o $(NAME)
	$(OBJCOPY) $(NAME) -O binary
	
install:
	$(KPACK) $(NAME)
	cp $(NAME) ../../../../build_iso/kolibrios/develop/js
clean:
	rm -f $(NAME) *.o

