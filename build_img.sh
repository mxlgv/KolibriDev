#!/bin/bash
IMG_DIR=build_img
ISO_DIR=build_iso
KERNEL_DIR="src/kernel"

# BUILD KERNEL
cd $KERNEL_DIR
make clean 
env lang=en make
cp -f bin/boot_fat12.bin ../..
../../tools/kerpack bin/kernel.mnt ../../$IMG_DIR/KERNEL.MNT
cd ../..

# CHECK IMG_DIR SIZE
size=$(du -shb build_img | cut -f1)
if (( $size > 1474560 )); then
    zenity --error --text="Error! Files do not fit into the image!" --no-wrap
    exit
fi

# BUILD IMAGE
mkdir tmp
dd if=/dev/zero of=$ISO_DIR/kolibri.img bs=1k count=1440
mkfs.vfat -F12 $ISO_DIR/kolibri.img 
dd if=boot_fat12.bin of=$ISO_DIR/kolibri.img conv=notrunc bs=512 count=1
sudo mount -o loop,rw,sync $ISO_DIR/kolibri.img tmp
sudo cp -rfv $IMG_DIR/* tmp
sync
sudo umount tmp
sudo rm -rf tmp

rm build_img/KERNEL.MNT

# THIS FILE WAS WRITEN BY TURBOCAT2001 AND UPDATED BY MAXCODEHACK
