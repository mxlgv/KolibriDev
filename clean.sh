#!/bin/bash

IMG_DIR=build_img
ISO_DIR=build_iso
KERNEL_DIR="src/kernel"

rm -fv $ISO_DIR/kolibri.img boot_fat12.bin $IMG_DIR/KERNEL.MNT $KERNEL_DIR/bootbios.bin $KERNEL_DIR/kernel.mnt
cd $KERNEL_DIR
make clean
