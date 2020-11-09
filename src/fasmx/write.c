#include <stdio.h>

size_t write(int fd, const void *buf, size_t count)
{
    char *sym = (char*)buf;
    for(int i=0; i<count; i++)
    {
       putchar(sym[i]);
    }
}
