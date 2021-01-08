# KolibriOS Developer Edition
### Description
KolibriDev is a Kolibri OS distribution built to prove that development in the OS itself is possible!
### Differences from the official

**Added:**

- FasmX(console version Fasm)
- kosjs(in developing)
- Netsurf(pre-installed)
- TinyTextEditor
- NeonTextEditor
- TinyHashView
- TEAtool
- UD86(Disassembler)
- Binutils:
    - objcopy
    - ar
    - strip
- FreePascal
- TinyPython 
- Header files for fasm libraries
- New wallpapers and icons

**Removed:**

- All demos and games
- Some emulators
- fNav - file manager
- Unnecessary skins

### Building and getting

**Getting:** 
You can get the finished ISO image on the [releases page](https://github.com/turbocat2001/KolibriDev/releases)

**Building in Linux:** 

To build you need: fasm, gcc-toolchain (sdk and kos32-gcc). If the SDK is located at "/home/autobuild/tools/win32/sdk" and the libraries are at "/home/autobuild/tools/win32/mingw32/lib", then continue. 
If not, then execute:

`export SDK_DIR="/path/to/sdk"`

`export LIB_DIR="/path/to/lib"`

After that, run ` ./build ` and after that the build will start. At the end of the build, the distribution kit can be run in "Qemu" or
"VirtualBox", commands:

` ./star_qemu`

`./start_qemu_kvm` 

`./start_vbox`

