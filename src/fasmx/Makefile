KPACK=../../tools/kpack
FASM=fasm
LD = kos32-ld
OBJCOPY=kos32-objcopy
STRIP=kos32-strip 
CC=kos32-gcc
 
SDK_DIR:= /usr/local/kos32/sdk
LDFLAGS =-call_shared -nostdlib -T $(SDK_DIR)/sources/newlib/app-dynamic.lds --image-base 0  --subsystem console
LIBPATH:= -L $(SDK_DIR)/lib -L $(SDK_DIR)/../lib
CFLAGS = -c -fno-ident -O2 -fomit-frame-pointer -fno-ident -U__WIN32__ -U_Win32 -U_WIN32 -U__MINGW32__ -UWIN32
INCLUDES= -I $(SDK_DIR)/sources/newlib/libc/include

all:
	$(FASM) fasm.asm
	$(CC) $(CFLAGS) $(INCLUDES) write.c
	$(LD) $(LDFLAGS) $(LIBPATH) -o fasmx fasm.o write.o -lgcc -lc.dll
	$(STRIP) -s fasmx -o fasmx
	$(OBJCOPY) fasmx -O binary
	
install:
	$(KPACK) fasmx
	cp fasmx ../../build_iso/kolibrios/develop/fasm
clean:
	rm -f fasmx *.o

