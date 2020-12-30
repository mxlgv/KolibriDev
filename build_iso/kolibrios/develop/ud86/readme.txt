Дизассемблер
============
http://board.kolibrios.org/viewtopic.php?f=45&t=1574&start=15#p36962
___________________________________________________________

Usage: udcli [-option[s]] file
  Options:
      -16      : Set the disassembly mode to 16 bits. 
      -32      : Set the disassembly mode to 32 bits. (default)
      -64      : Set the disassembly mode to 64 bits.
      -intel   : Set the output to INTEL (NASM like) syntax. (default)
      -att     : Set the output to AT&T (GAS like) syntax.
      -v <v>   : Set vendor. <v> = {intel, amd}.
      -o <pc>  : Set the value of program counter to <pc>. (default = 0)
      -s <n>   : Set the number of bytes to skip before disassembly to <n>.
      -c <n>   : Set the number of bytes to disassemble to <n>.
      -x       : Set the input mode to whitespace seperated 8-bit numbers in
                 hexadecimal representation. Example: 0f 01 ae 00
      -noff    : Do not display the offset of instructions.
      -nohex   : Do not display the hexadecimal code of instructions.
      -h       : Display this help message.
      --version: Show version.
  
  Udcli is a front-end to the Udis86 Disassembler Library. 
  http://udis86.sourceforge.net/
____________________________________________________________

Порт на newlib - maxcodehack
