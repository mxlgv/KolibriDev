KPACK = ../../../../tools/kpack
FASM = fasm
LD = kos32-ld
OBJCOPY = kos32-objcopy
STRIP = kos32-strip 
CC = kos32-gcc

NAME = fasm

ifndef SDK_DIR
SDK_DIR = /home/autobuild/tools/win32/sdk
endif

ifndef LIB_DIR
LIB_DIR = /home/autobuild/tools/win32/mingw32/lib
endif
	
LDFLAGS = -call_shared -nostdlib -T $(SDK_DIR)/sources/newlib/app-dynamic.lds --image-base 0  --subsystem console
LIBPATH = -L $(SDK_DIR)/lib -L $(LIB_DIR)
CFLAGS = -c -fno-ident -O2 -fomit-frame-pointer -fno-ident -U__WIN32__ -U_Win32 -U_WIN32 -U__MINGW32__ -UWIN32
INCLUDES = -I $(SDK_DIR)/sources/newlib/libc/include

all:
	$(FASM) $(NAME).asm
	$(CC) $(CFLAGS) $(INCLUDES) write.c
	$(LD) $(LDFLAGS) $(LIBPATH) -o $(NAME)x $(NAME).o write.o -lgcc -lc.dll
	$(STRIP) -s $(NAME)x -o fasmx
	$(OBJCOPY) $(NAME)x -O binary
	
install:
	$(KPACK) $(NAME)x
	cp $(NAME)x ../../../../build_iso/kolibrios/develop/fasm
clean:
	rm -f $(NAME)x *.o

