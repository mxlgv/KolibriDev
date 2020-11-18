#include <stdio.h>
#include "include/mujs.h"
#include "include/import.h"
#include <stdlib.h>


int main(int argc, char **argv)
{
    import_functions();
    js_dofile(J, argv[1]);
    js_freestate (J);
    exit(0);
} 
